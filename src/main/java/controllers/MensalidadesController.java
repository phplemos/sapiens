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



    private void criarTransacaoReceita(Mensalidade m, double valorPago) {
        String nomeAluno = "Aluno Desconhecido";
        Optional<Matricula> mat = matriculaRepo.buscarPorId(m.getMatriculaId());
        if (mat.isPresent()) {
            Optional<Aluno> al = alunoRepo.buscarPorPessoaId(mat.get().getAlunoPessoaId());
            if (al.isPresent()) {
                nomeAluno = pessoaRepo.buscarPorId(al.get().getPessoaId()).get().getNomeCompleto();
            }
        }

        Transacao t = new Transacao();
        t.setDescricao("Mensalidade Venc: " + m.getDataVencimento().format(DateTimeFormatter.ofPattern("dd/MM")) + " - " + nomeAluno);

        t.setValor(valorPago);
        t.setTipo(TipoTransacao.RECEITA);
        t.setDataTransacao(LocalDate.now());
        t.setDataPagamento(LocalDate.now());

        t.setMensalidadeId(m.getId());

        t.setCategoriaId(resolverCategoriaMensalidade());

        transacaoRepo.salvar(t);
    }

    private int resolverCategoriaMensalidade() {
        return categoriaFinanceiraRepo.listarTodos().stream()
                .filter(c -> c.getNome().equalsIgnoreCase("Mensalidades") && c.getTipo() == TipoTransacao.RECEITA)
                .findFirst()
                .map(CategoriaFinanceira::getId)
                .orElseGet(() -> {
                    CategoriaFinanceira nova = new CategoriaFinanceira();
                    nova.setNome("Mensalidades");
                    nova.setTipo(TipoTransacao.RECEITA);
                    categoriaFinanceiraRepo.salvar(nova);
                    return nova.getId();
                });
    }

    private void carregarDadosIniciais() {
        view.adicionarAluno(new ComboItem(0, "Selecione..."));
        alunoRepo.listarTodos().forEach(a -> {
            pessoaRepo.buscarPorId(a.getPessoaId()).ifPresent(p ->
                    view.adicionarAluno(new ComboItem(a.getPessoaId(), p.getNomeCompleto()))
            );
        });

        view.adicionarTurma(new ComboItem(0, "Selecione..."));
        turmaRepo.listarTodos().forEach(t ->
                view.adicionarTurma(new ComboItem(t.getId(), t.getNome()))
        );
    }

    private void initController() {
        view.getBtnBuscar().addActionListener(e -> listarMensalidades());
        view.getBtnGerarCarneAluno().addActionListener(e -> gerarCarne(view.getAlunoSelecionadoPessoaId()));
        view.getBtnBaixar().addActionListener(e -> baixarPagamento());

        view.getBtnDesconto().addActionListener(e -> aplicarDesconto());

        view.getBtnBoleto().addActionListener(e -> simularBoleto());

        view.getBtnGerarParaTurma().addActionListener(e -> gerarParaTurma());

        view.getBtnRelatorioInadimplentes().addActionListener(e -> gerarRelatorioInadimplentes());
        view.getBtnRelatorioPagos().addActionListener(e -> gerarRelatorioPagos());
    }

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
            verificarAtraso(m);

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

    private void verificarAtraso(Mensalidade m) {
        if (m.getStatusPagamento() == StatusPagamento.PENDENTE &&
                m.getDataVencimento().isBefore(LocalDate.now())) {
            m.setStatusPagamento(StatusPagamento.ATRASADO);
        }
    }

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

        double valorBase = m.getValorTotal() - m.getValorDesconto();
        double multa = 0;
        double juros = 0;

        if (LocalDate.now().isAfter(m.getDataVencimento())) {
            long diasAtraso = ChronoUnit.DAYS.between(m.getDataVencimento(), LocalDate.now());
            multa = valorBase * 0.02;
            juros = (valorBase * 0.001) * diasAtraso;

            String msg = String.format("Atraso: %d dias\nMulta: R$ %.2f\nJuros: R$ %.2f\n\nTotal Final: R$ %.2f",
                    diasAtraso, multa, juros, (valorBase + multa + juros));

            int confirm = JOptionPane.showConfirmDialog(view, msg, "Confirmar valores", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        double valorFinalPago = valorBase + multa + juros;

        m.setValorJurosMulta(multa + juros);
        m.setStatusPagamento(StatusPagamento.PAGO);
        m.setDataPagamento(LocalDate.now());
        mensalidadeRepo.editar(m);

        criarTransacaoReceita(m, valorFinalPago);

        JOptionPane.showMessageDialog(view, "Pagamento baixado e Receita registrada no caixa!");
        listarMensalidades();
    }

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

    private void simularBoleto() {
        int row = view.getTabela().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione uma mensalidade para imprimir.");
            return;
        }
        int id = (int) view.getTabela().getValueAt(row, 0);
        Mensalidade m = mensalidadeRepo.buscarPorId(id).orElseThrow();

        Matricula mat = matriculaRepo.buscarPorId(m.getMatriculaId()).orElse(new Matricula());
        Optional<Aluno> alunoOpt = alunoRepo.buscarPorPessoaId(mat.getAlunoPessoaId());
        String nomeAluno = "Desconhecido";
        if (alunoOpt.isPresent()) {
            nomeAluno = pessoaRepo.buscarPorId(alunoOpt.get().getPessoaId()).get().getNomeCompleto();
        }

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

            String nome = "Matricula " + mat.getNumeroMatricula();

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
                m.setValorDesconto(0);
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

    private void gerarRelatorioInadimplentes() {
        view.getModelRelatorio().setRowCount(0);
        List<Mensalidade> todas = mensalidadeRepo.listarTodos();

        for (Mensalidade m : todas) {
            if (m.getStatusPagamento() == StatusPagamento.PENDENTE && m.getDataVencimento().isBefore(LocalDate.now())) {
                adicionarLinhaRelatorio(m, "ATRASADO");
            }
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
                .findFirst();
    }
}