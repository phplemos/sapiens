package controllers;

import enums.StatusPagamento;
import models.*;
import repositories.*;
import views.FinanceiroResponsavelView;
import views.components.ComboItem;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FinanceiroResponsavelController {

    private final FinanceiroResponsavelView view;
    private final int responsavelPessoaId;

    private final AlunoResponsavelRepository alunoRespRepo;
    private final AlunoRepository alunoRepo;
    private final PessoaRepository pessoaRepo;
    private final MatriculaRepository matriculaRepo;
    private final MensalidadeRepository mensalidadeRepo;
    private final ParametrosEscolaRepository paramRepo; // Para saber juros oficiais (opcional)

    public FinanceiroResponsavelController(FinanceiroResponsavelView view, int responsavelPessoaId) {
        this.view = view;
        this.responsavelPessoaId = responsavelPessoaId;

        this.alunoRespRepo = new AlunoResponsavelRepository();
        this.alunoRepo = new AlunoRepository();
        this.pessoaRepo = new PessoaRepository();
        this.matriculaRepo = new MatriculaRepository();
        this.mensalidadeRepo = new MensalidadeRepository();
        this.paramRepo = new ParametrosEscolaRepository();

        initController();
    }

    private void initController() {
        carregarFilhos();

        view.getCbFilhos().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                listarMensalidades();
            }
        });

        view.getBtnVisualizarBoleto().addActionListener(e -> visualizarBoleto());
        view.getBtnFechar().addActionListener(e -> view.dispose());

        listarMensalidades();
    }

    private void carregarFilhos() {
        List<Integer> idsAlunos = alunoRespRepo.buscarIdsAlunosDoResponsavel(responsavelPessoaId);

        if (idsAlunos.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhum aluno vinculado ao seu cadastro.");
            view.dispose();
            return;
        }

        for (Integer id : idsAlunos) {
            Optional<Pessoa> p = pessoaRepo.buscarPorId(id);
            p.ifPresent(pessoa -> view.adicionarFilho(new ComboItem(id, pessoa.getNomeCompleto())));
        }
    }

    private void listarMensalidades() {
        view.getTableModel().setRowCount(0);

        ComboItem itemSelecionado = (ComboItem) view.getCbFilhos().getSelectedItem();
        if (itemSelecionado == null) return;

        int alunoId = itemSelecionado.getId();

        Optional<Matricula> matOpt = matriculaRepo.listarTodos().stream()
                .filter(m -> m.getAlunoPessoaId() == alunoId)
                .max(Comparator.comparingInt(Matricula::getId));

        if (matOpt.isEmpty()) {
            view.setResumo("Aluno sem matrícula ativa.");
            return;
        }

        List<Mensalidade> mensalidades = mensalidadeRepo.buscarPorMatriculaId(matOpt.get().getId());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        double totalEmAberto = 0;

        for (Mensalidade m : mensalidades) {
            double valorBase = m.getValorTotal() - m.getValorDesconto();
            double encargos = m.getValorJurosMulta(); // Valor gravado no banco

            String statusExibicao = m.getStatusPagamento().toString();

            if (m.getStatusPagamento() == StatusPagamento.PENDENTE &&
                    m.getDataVencimento().isBefore(LocalDate.now())) {
                statusExibicao = "ATRASADO";

                long dias = ChronoUnit.DAYS.between(m.getDataVencimento(), LocalDate.now());
                double multaSimulada = valorBase * 0.02;
                double jurosSimulado = (valorBase * 0.001) * dias;
                encargos = multaSimulada + jurosSimulado;
            }

            double totalFinal = valorBase + encargos;
            String dataPagto = (m.getDataPagamento() != null) ? m.getDataPagamento().format(fmt) : "-";

            view.getTableModel().addRow(new Object[]{
                    m.getId(),
                    m.getDataVencimento().format(fmt),
                    String.format("R$ %.2f", valorBase),
                    String.format("R$ %.2f", encargos),
                    String.format("R$ %.2f", totalFinal),
                    statusExibicao,
                    dataPagto
            });

            if (!statusExibicao.equals("PAGO")) {
                totalEmAberto += totalFinal;
            }
        }

        view.setResumo("Total em Aberto: R$ " + String.format("%.2f", totalEmAberto));
    }

    private void visualizarBoleto() {
        int row = view.getTabela().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione uma parcela para ver o boleto.");
            return;
        }

        String status = view.getTabela().getValueAt(row, 5).toString();
        if ("PAGO".equals(status)) {
            JOptionPane.showMessageDialog(view, "Esta parcela já está paga. (Recibo disponível)");
            return;
        }

        String vencimento = view.getTabela().getValueAt(row, 1).toString();
        String total = view.getTabela().getValueAt(row, 4).toString();
        ComboItem alunoItem = (ComboItem) view.getCbFilhos().getSelectedItem();

        String html = String.format("<html>" +
                        "<div style='border: 1px solid black; padding: 20px; width: 350px; font-family: Arial;'>" +
                        "<h2 style='text-align:center'>BANCO SAPIENS</h2><hr>" +
                        "<b>Pagador:</b> %s (Responsável)<br>" +
                        "<b>Aluno:</b> %s<br>" +
                        "<b>Vencimento:</b> %s<br>" +
                        "<br>" +
                        "<h1 style='text-align:right'>%s</h1>" +
                        "<hr>" +
                        "<p style='text-align:center; font-size:10px'>Use o código de barras abaixo para pagar no seu banco:</p>" +
                        "<div style='background-color:black; height:30px; margin: 10px'></div>" +
                        "<p style='text-align:center'>23793.38128 60032.01230 48000.03212 1 894000000</p>" +
                        "</div></html>",
                "Eu (Responsável Logado)", alunoItem.toString(), vencimento, total);

        JOptionPane.showMessageDialog(view, new JLabel(html), "Boleto Bancário", JOptionPane.PLAIN_MESSAGE);
    }
}