package controllers;

import enums.StatusMatricula;
import enums.StatusPagamento;
import enums.TipoTransacao;
import models.*;
import repositories.*;
import views.components.ComboItem;
import views.MensalidadesView;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MensalidadesController {

    private final MensalidadesView view;
    private final MensalidadeRepository mensalidadeRepo;
    private final MatriculaRepository matriculaRepo;
    private final AlunoRepository alunoRepo;
    private final PessoaRepository pessoaRepo;
    private final TurmaRepository turmaRepo;
    private final TransacaoRepository transacaoRepo;
    private final CategoriaFinanceiraRepository categoriaFinanceiraRepo;

    public MensalidadesController(MensalidadesView view) {
        this.view = view;
        this.mensalidadeRepo = new MensalidadeRepository();
        this.matriculaRepo = new MatriculaRepository();
        this.alunoRepo = new AlunoRepository();
        this.pessoaRepo = new PessoaRepository();
        this.turmaRepo = new TurmaRepository();
        this.transacaoRepo = new TransacaoRepository();
        this.categoriaFinanceiraRepo = new CategoriaFinanceiraRepository();

        carregarDadosIniciais();
        initController();
    }



    // --- NOVO MÉTODO AUXILIAR PARA CRIAR A TRANSAÇÃO ---
    private void criarTransacaoReceita(Mensalidade m, double valorPago) {
        // Recupera nome do aluno para a descrição ficar bonita
        String nomeAluno = "Aluno Desconhecido";
        Optional<Matricula> mat = matriculaRepo.buscarPorId(m.getMatriculaId());
        if (mat.isPresent()) {
            Optional<Aluno> al = alunoRepo.buscarPorPessoaId(mat.get().getAlunoPessoaId());
            if (al.isPresent()) {
                nomeAluno = pessoaRepo.buscarPorId(al.get().getPessoaId()).get().getNomeCompleto();
            }
        }

        Transacao t = new Transacao();
        // Descrição informativa
        t.setDescricao("Mensalidade Venc: " + m.getDataVencimento().format(DateTimeFormatter.ofPattern("dd/MM")) + " - " + nomeAluno);

        t.setValor(valorPago);
        t.setTipo(TipoTransacao.RECEITA); // Importante: É dinheiro entrando!
        t.setDataTransacao(LocalDate.now()); // Data da competência (ou vencimento original, depende da regra contábil)
        t.setDataPagamento(LocalDate.now()); // Data que o dinheiro entrou efetivamente (Regime de Caixa)

        t.setMensalidadeId(m.getId()); // VÍNCULO IMPORTANTE

        // Define categoria "Mensalidades" (Cria se não existir ou busca ID)
        t.setCategoriaId(resolverCategoriaMensalidade());

        transacaoRepo.salvar(t);
    }

    private int resolverCategoriaMensalidade() {
        // Tenta achar uma categoria chamada "Mensalidades"
        return categoriaFinanceiraRepo.listarTodos().stream()
                .filter(c -> c.getNome().equalsIgnoreCase("Mensalidades") && c.getTipo() == TipoTransacao.RECEITA)
                .findFirst()
                .map(CategoriaFinanceira::getId)
                .orElseGet(() -> {
                    // Se não existir, cria automaticamente para não dar erro
                    CategoriaFinanceira nova = new CategoriaFinanceira();
                    nova.setNome("Mensalidades");
                    nova.setTipo(TipoTransacao.RECEITA);
                    categoriaFinanceiraRepo.salvar(nova); // Adapte se seu repo usar 'salvar' ou 'add' direto
                    return nova.getId();
                });
    }

    private void carregarDadosIniciais() {
        // Carrega Alunos
        view.adicionarAluno(new ComboItem(0, "Selecione..."));
        alunoRepo.listarTodos().forEach(a -> {
            pessoaRepo.buscarPorId(a.getPessoaId()).ifPresent(p ->
                    view.adicionarAluno(new ComboItem(a.getPessoaId(), p.getNomeCompleto()))
            );
        });

        // Carrega Turmas (RF028)
        view.adicionarTurma(new ComboItem(0, "Selecione..."));
        turmaRepo.listarTodos().forEach(t ->
                view.adicionarTurma(new ComboItem(t.getId(), t.getNome()))
        );
    }

    private void initController() {
        // Individual
        view.getBtnBuscar().addActionListener(e -> listarMensalidades());
        view.getBtnGerarCarneAluno().addActionListener(e -> gerarCarne(view.getAlunoSelecionadoPessoaId()));
        view.getBtnBaixar().addActionListener(e -> baixarPagamento());

        // RF032 - Desconto
        view.getBtnDesconto().addActionListener(e -> aplicarDesconto());

        // RF029 - Boleto
        view.getBtnBoleto().addActionListener(e -> simularBoleto());

        // RF028 - Turma
        view.getBtnGerarParaTurma().addActionListener(e -> gerarParaTurma());

        // RF033 e RF034 - Relatórios
        view.getBtnRelatorioInadimplentes().addActionListener(e -> gerarRelatorioInadimplentes());
        view.getBtnRelatorioPagos().addActionListener(e -> gerarRelatorioPagos());
    }

    // --- LÓGICA DE LISTAGEM INDIVIDUAL ---
    private void listarMensalidades() {
        int pessoaId = view.getAlunoSelecionadoPessoaId();
        if (pessoaId == 0) return;

        view.getTableModel().setRowCount(0);

        Optional<Matricula> matOpt = buscarMatriculaAtiva(pessoaId);
        if (matOpt.isEmpty()) {
            view.setResumo("Aluno sem matrícula ativa.");
            return;
        }

        List<Mensalidade> mensalidades = mensalidadeRepo.buscarPorMatriculaId(matOpt.get().getId());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        double totalDevido = 0;

        for (Mensalidade m : mensalidades) {
            verificarAtraso(m); // Atualiza status se venceu

            double valorFinal = (m.getValorTotal() - m.getValorDesconto()) + m.getValorJurosMulta();
            String dataPagto = (m.getDataPagamento() != null) ? m.getDataPagamento().format(fmt) : "-";

            view.getTableModel().addRow(new Object[]{
                    m.getId(),
                    m.getDataVencimento().format(fmt),
                    String.format("R$ %.2f", m.getValorTotal()),
                    String.format("R$ %.2f", m.getValorJurosMulta()),
                    String.format("R$ %.2f", valorFinal),
                    m.getStatusPagamento(),
                    dataPagto
            });

            if (m.getStatusPagamento() != StatusPagamento.PAGO) {
                totalDevido += valorFinal;
            }
        }
        view.setResumo("Total a Pagar (com multas): R$ " + String.format("%.2f", totalDevido));
    }

    // Helper: Verifica se está atrasado apenas ao listar (sem salvar juros ainda)
    private void verificarAtraso(Mensalidade m) {
        if (m.getStatusPagamento() == StatusPagamento.PENDENTE &&
                m.getDataVencimento().isBefore(LocalDate.now())) {
            m.setStatusPagamento(StatusPagamento.ATRASADO);
        }
    }

    // --- RF031: BAIXA COM JUROS E MULTA ---
    private void baixarPagamento() {
        int row = view.getTabela().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione uma mensalidade.");
            return;
        }

        int id = (int) view.getTabela().getValueAt(row, 0);
        Mensalidade m = mensalidadeRepo.buscarPorId(id).orElseThrow();

        if (m.getStatusPagamento() == StatusPagamento.PAGO) {
            JOptionPane.showMessageDialog(view, "Esta mensalidade já está paga!");
            return;
        }

        // 1. Cálculos Financeiros (Juros/Multa)
        double valorBase = m.getValorTotal() - m.getValorDesconto();
        double multa = 0;
        double juros = 0;

        if (LocalDate.now().isAfter(m.getDataVencimento())) {
            long diasAtraso = ChronoUnit.DAYS.between(m.getDataVencimento(), LocalDate.now());
            multa = valorBase * 0.02; // 2%
            juros = (valorBase * 0.001) * diasAtraso; // 0.1% ao dia

            String msg = String.format("Atraso: %d dias\nMulta: R$ %.2f\nJuros: R$ %.2f\n\nTotal Final: R$ %.2f",
                    diasAtraso, multa, juros, (valorBase + multa + juros));

            int confirm = JOptionPane.showConfirmDialog(view, msg, "Confirmar valores", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        double valorFinalPago = valorBase + multa + juros;

        // 2. Atualiza a Mensalidade (Módulo Acadêmico)
        m.setValorJurosMulta(multa + juros);
        m.setStatusPagamento(StatusPagamento.PAGO);
        m.setDataPagamento(LocalDate.now());
        mensalidadeRepo.editar(m);

        // 3. INTEGRAÇÃO: Gera a Receita no Fluxo de Caixa (Módulo Financeiro Geral)
        criarTransacaoReceita(m, valorFinalPago);

        JOptionPane.showMessageDialog(view, "Pagamento baixado e Receita registrada no caixa!");
        listarMensalidades();
    }

    // --- RF032: APLICAR DESCONTO ---
    private void aplicarDesconto() {
        int row = view.getTabela().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione uma mensalidade.");
            return;
        }
        int id = (int) view.getTabela().getValueAt(row, 0);
        Mensalidade m = mensalidadeRepo.buscarPorId(id).orElseThrow();

        if (m.getStatusPagamento() == StatusPagamento.PAGO) {
            JOptionPane.showMessageDialog(view, "Não pode dar desconto em mensalidade paga.");
            return;
        }

        String input = JOptionPane.showInputDialog(view, "Valor do desconto (R$):", m.getValorDesconto());
        if (input != null) {
            try {
                double desc = Double.parseDouble(input.replace(",", "."));
                if (desc > m.getValorTotal()) {
                    JOptionPane.showMessageDialog(view, "Desconto maior que o valor!");
                    return;
                }
                m.setValorDesconto(desc);
                mensalidadeRepo.editar(m);
                listarMensalidades();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Valor inválido.");
            }
        }
    }

    // --- RF029: SIMULAR BOLETO ---
    private void simularBoleto() {
        int row = view.getTabela().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione uma mensalidade para imprimir.");
            return;
        }
        int id = (int) view.getTabela().getValueAt(row, 0);
        Mensalidade m = mensalidadeRepo.buscarPorId(id).orElseThrow();

        // Dados do Aluno para o boleto
        Matricula mat = matriculaRepo.buscarPorId(m.getMatriculaId()).orElse(new Matricula());
        Optional<Aluno> alunoOpt = alunoRepo.buscarPorPessoaId(mat.getAlunoPessoaId());
        String nomeAluno = "Desconhecido";
        if (alunoOpt.isPresent()) {
            nomeAluno = pessoaRepo.buscarPorId(alunoOpt.get().getPessoaId()).get().getNomeCompleto();
        }

        // Gera visualização HTML simples em um Dialog
        double valorCobrado = (m.getValorTotal() - m.getValorDesconto()) + m.getValorJurosMulta();

        String html = String.format("<html>" +
                        "<div style='border: 1px solid black; padding: 20px; width: 300px;'>" +
                        "<h2>BANCO SAPIENS</h2>" +
                        "<hr>" +
                        "<b>Pagador:</b> %s<br>" +
                        "<b>Vencimento:</b> %s<br>" +
                        "<b>Nosso Número:</b> 0000%d<br>" +
                        "<br>" +
                        "<h1 style='text-align:right'>R$ %.2f</h1>" +
                        "<hr>" +
                        "<barcode>|| ||| || |||| ||| ||</barcode>" +
                        "</div></html>",
                nomeAluno, m.getDataVencimento(), m.getId(), valorCobrado);

        JOptionPane.showMessageDialog(view, new JLabel(html), "Visualização de Boleto", JOptionPane.PLAIN_MESSAGE);
    }

    // --- RF028: GERAR PARA TURMA ---
    private void gerarParaTurma() {
        int turmaId = view.getTurmaSelecionadaId();
        if (turmaId == 0) {
            JOptionPane.showMessageDialog(view, "Selecione uma turma.");
            return;
        }

        String inputValor = JOptionPane.showInputDialog(view, "Valor da mensalidade padrão:", "500.00");
        if (inputValor == null) return;
        double valor = Double.parseDouble(inputValor.replace(",", "."));

        List<Matricula> matriculasTurma = matriculaRepo.listarTodos().stream()
                .filter(m -> m.getTurmaId() == turmaId && m.getStatus() == StatusMatricula.ATIVA)
                .collect(Collectors.toList());

        view.appendLog("Iniciando geração para " + matriculasTurma.size() + " alunos...");
        int totalGerado = 0;

        for (Matricula mat : matriculasTurma) {
            int geradasAluno = gerarMensalidadesAno(mat.getId(), valor, 2025);
            totalGerado += geradasAluno;

            // Busca nome para logar
            String nome = "Matricula " + mat.getNumeroMatricula();
            // (Opcional: buscar nome no PessoaRepo para ficar bonito o log)

            view.appendLog("> " + nome + ": " + geradasAluno + " mensalidades geradas.");
        }
        view.appendLog("--- FIM. Total de boletos: " + totalGerado + " ---");
    }

    private int gerarMensalidadesAno(int matriculaId, double valor, int ano) {
        int count = 0;
        for (int mes = 1; mes <= 12; mes++) {
            if (!mensalidadeRepo.existeMensalidade(matriculaId, mes, ano)) {
                Mensalidade m = new Mensalidade();
                m.setMatriculaId(matriculaId);
                m.setValorTotal(valor);
                m.setValorDesconto(0); // RF032: Poderia buscar desconto do cadastro do aluno aqui
                m.setValorJurosMulta(0);
                m.setDataVencimento(LocalDate.of(ano, mes, 10));
                m.setStatusPagamento(StatusPagamento.PENDENTE);
                mensalidadeRepo.salvar(m);
                count++;
            }
        }
        return count;
    }

    // Método auxiliar individual (chama o gerador genérico)
    private void gerarCarne(int pessoaId) {
        Optional<Matricula> mat = buscarMatriculaAtiva(pessoaId);
        if (mat.isPresent()) {
            String input = JOptionPane.showInputDialog("Valor:", "500.00");
            if (input != null) {
                gerarMensalidadesAno(mat.get().getId(), Double.parseDouble(input), 2025);
                listarMensalidades();
                JOptionPane.showMessageDialog(view, "Carnê gerado.");
            }
        }
    }

    // --- RF033 e RF034: RELATÓRIOS ---
    private void gerarRelatorioInadimplentes() {
        view.getModelRelatorio().setRowCount(0);
        List<Mensalidade> todas = mensalidadeRepo.listarTodos();

        for (Mensalidade m : todas) {
            // Verifica atraso
            if (m.getStatusPagamento() == StatusPagamento.PENDENTE && m.getDataVencimento().isBefore(LocalDate.now())) {
                adicionarLinhaRelatorio(m, "ATRASADO");
            }
            // Se já está marcado como ATRASADO no banco
            else if (m.getStatusPagamento() == StatusPagamento.ATRASADO) {
                adicionarLinhaRelatorio(m, "ATRASADO");
            }
        }
    }

    private void gerarRelatorioPagos() {
        view.getModelRelatorio().setRowCount(0);
        List<Mensalidade> todas = mensalidadeRepo.listarTodos();
        int mesAtual = LocalDate.now().getMonthValue();

        for (Mensalidade m : todas) {
            if (m.getStatusPagamento() == StatusPagamento.PAGO &&
                    m.getDataPagamento() != null &&
                    m.getDataPagamento().getMonthValue() == mesAtual) {
                adicionarLinhaRelatorio(m, "PAGO EM " + m.getDataPagamento());
            }
        }
    }

    private void adicionarLinhaRelatorio(Mensalidade m, String statusCustom) {
        // Precisa recuperar o nome do aluno via Matricula -> Aluno -> Pessoa
        Matricula mat = matriculaRepo.buscarPorId(m.getMatriculaId()).orElse(new Matricula());
        Optional<Aluno> aluno = alunoRepo.buscarPorPessoaId(mat.getAlunoPessoaId());
        String nome = "Desconhecido";

        if (aluno.isPresent()) {
            nome = pessoaRepo.buscarPorId(aluno.get().getPessoaId()).get().getNomeCompleto();
        }

        view.getModelRelatorio().addRow(new Object[]{
                nome,
                m.getDataVencimento(),
                String.format("%.2f", m.getValorTotal()),
                statusCustom
        });
    }

    private Optional<Matricula> buscarMatriculaAtiva(int pessoaId) {
        return matriculaRepo.listarTodos().stream()
                .filter(m -> m.getAlunoPessoaId() == pessoaId)
                .findFirst(); // Pegar Status ATIVA idealmente
    }
}