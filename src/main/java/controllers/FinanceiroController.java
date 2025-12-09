package controllers;

import enums.StatusPagamento;
import models.*;
import repositories.*;
import views.components.ComboItem;
import views.FinanceiroView;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class FinanceiroController {

    private final FinanceiroView listView;

    private final MensalidadeRepository mensalidadeRepo;
    private final MatriculaRepository matriculaRepo;
    private final AlunoRepository alunoRepo;
    private final PessoaRepository pessoaRepo;

    public FinanceiroController(FinanceiroView view) {
        this.listView = view;

        this.mensalidadeRepo = new MensalidadeRepository();
        this.matriculaRepo = new MatriculaRepository();
        this.alunoRepo = new AlunoRepository();
        this.pessoaRepo = new PessoaRepository();

        carregarAlunos();
        initController();
    }

    private void carregarAlunos() {
        // Carrega alunos que possuem matrícula ATIVA
        // (Simplificação: Carrega todos os alunos e vamos filtrar depois)
        List<Aluno> alunos = alunoRepo.listarTodos();
        listView.adicionarAluno(new ComboItem(0, "Selecione..."));

        for (Aluno a : alunos) {
            Pessoa p = pessoaRepo.buscarPorId(a.getPessoaId()).orElse(null);
            if (p != null) {
                listView.adicionarAluno(new ComboItem(a.getPessoaId(), p.getNomeCompleto()));
            }
        }
    }

    private void initController() {
        listView.getBtnBuscar().addActionListener(e -> listarMensalidades());
        listView.getBtnGerarCarne().addActionListener(e -> gerarMensalidadesEmLote());
        listView.getBtnBaixar().addActionListener(e -> baixarPagamento());
    }

    private void listarMensalidades() {
        int pessoaId = listView.getAlunoSelecionadoPessoaId();
        if (pessoaId == 0) return;

        listView.getTableModel().setRowCount(0);

        // 1. Achar a matrícula ativa do aluno (simplificado: pega a primeira que achar)
        Optional<Matricula> matOpt = matriculaRepo.listarTodos().stream()
                .filter(m -> m.getAlunoPessoaId() == pessoaId)
                .findFirst();

        if (matOpt.isEmpty()) {
            listView.setResumo("Aluno não possui matrícula.");
            return;
        }

        int matriculaId = matOpt.get().getId();
        List<Mensalidade> mensalidades = mensalidadeRepo.buscarPorMatriculaId(matriculaId);

        double totalPendente = 0;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Mensalidade m : mensalidades) {
            String dataPagto = (m.getDataVencimento() != null) ? m.getDataVencimento().format(fmt) : "-";

            listView.getTableModel().addRow(new Object[]{
                    m.getId(),
                    m.getDataVencimento().format(fmt),
                    String.format("%.2f", m.getValorTotal()),
                    m.getStatusPagamento(),
                    dataPagto
            });

            if (m.getStatusPagamento() == StatusPagamento.PENDENTE ||
                    m.getStatusPagamento() == StatusPagamento.ATRASADO) {
                totalPendente += m.getValorTotal();
            }
        }

        listView.setResumo("Total Pendente: R$ " + String.format("%.2f", totalPendente));
    }

    /**
     * GERADOR: Cria 12 mensalidades para o aluno selecionado
     */
    private void gerarMensalidadesEmLote() {
        int pessoaId = listView.getAlunoSelecionadoPessoaId();
        if (pessoaId == 0) {
            JOptionPane.showMessageDialog(listView, "Selecione um aluno primeiro.");
            return;
        }

        // Busca matrícula
        Optional<Matricula> matOpt = matriculaRepo.listarTodos().stream()
                .filter(m -> m.getAlunoPessoaId() == pessoaId)
                .findFirst();

        if (matOpt.isEmpty()) {
            JOptionPane.showMessageDialog(listView, "Este aluno não está matriculado.");
            return;
        }

        // Pergunta valor
        String inputValor = JOptionPane.showInputDialog(listView, "Qual o valor da mensalidade?", "500.00");
        if (inputValor == null) return;

        double valor = Double.parseDouble(inputValor.replace(",", "."));
        int ano = 2025; // Pode vir de um input também
        int matriculaId = matOpt.get().getId();
        int geradas = 0;

        for (int mes = 1; mes <= 12; mes++) {
            // Verifica duplicidade antes de criar
            if (!mensalidadeRepo.existeMensalidade(matriculaId, mes, ano)) {
                Mensalidade m = new Mensalidade();
                m.setMatriculaId(matriculaId);
                m.setValorTotal(valor);
                m.setValorDesconto(0);
                m.setValorJurosMulta(0);
                // Vencimento dia 10
                m.setDataVencimento(LocalDate.of(ano, mes, 10));
                m.setStatusPagamento(StatusPagamento.PENDENTE);

                mensalidadeRepo.salvar(m);
                geradas++;
            }
        }

        JOptionPane.showMessageDialog(listView, "Sucesso! " + geradas + " mensalidades geradas para 2025.");
        listarMensalidades(); // Atualiza a tabela
    }

    /**
     * BAIXA: Marca como pago
     */
    private void baixarPagamento() {
        int row = listView.getTabela().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(listView, "Selecione uma mensalidade na tabela.");
            return;
        }

        int idMensalidade = (int) listView.getTabela().getValueAt(row, 0);
        String statusAtual = listView.getTabela().getValueAt(row, 3).toString();

        if ("PAGO".equals(statusAtual)) {
            JOptionPane.showMessageDialog(listView, "Esta mensalidade já está paga.");
            return;
        }

        Optional<Mensalidade> mOpt = mensalidadeRepo.buscarPorId(idMensalidade);
        if (mOpt.isPresent()) {
            Mensalidade m = mOpt.get();
            m.setStatusPagamento(StatusPagamento.PAGO);
            m.setDataPagamento(LocalDate.now()); // Data de hoje

            mensalidadeRepo.editar(m);

            JOptionPane.showMessageDialog(listView, "Pagamento confirmado!");
            listarMensalidades();
        }
    }
}