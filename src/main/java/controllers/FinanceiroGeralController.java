package controllers;

import enums.TipoTransacao;
import models.CategoriaFinanceira;
import models.Transacao;
import repositories.CategoriaFinanceiraRepository;
import repositories.TransacaoRepository;
import views.FluxoCaixaView;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FinanceiroGeralController {

    private final FluxoCaixaView view;
    private final TransacaoRepository transacaoRepo;
    private final CategoriaFinanceiraRepository categoriaRepo;

    public FinanceiroGeralController(FluxoCaixaView view) {
        this.view = view;
        this.transacaoRepo = new TransacaoRepository();
        this.categoriaRepo = new CategoriaFinanceiraRepository();

        configurarTabelaAlertas();
        initController();
    }

    private void initController() {
        listarTransacoes();

        view.getBtnNovaReceita().addActionListener(e -> adicionarTransacao(TipoTransacao.RECEITA));
        view.getBtnNovaDespesa().addActionListener(e -> adicionarTransacao(TipoTransacao.DESPESA));

        view.getBtnBaixar().addActionListener(e -> baixarConta());
        view.getBtnExcluir().addActionListener(e -> excluirTransacao());

        view.getBtnRecibo().addActionListener(e -> emitirRecibo());
        view.getBtnAtualizarRelatorio().addActionListener(e -> calcularFluxoCaixa());

        view.getBtnExportarCSV().addActionListener(e -> exportarCSV());
    }

    private void listarTransacoes() {
        view.getTableModel().setRowCount(0);
        List<Transacao> lista = transacaoRepo.listarTodos();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Transacao t : lista) {
            String categoria = buscarNomeCategoria(t.getCategoriaId());

            String status = "PENDENTE";
            if (t.getDataPagamento() != null) status = "PAGO";
            else if (t.getDataTransacao().isBefore(LocalDate.now())) status = "ATRASADO";

            view.getTableModel().addRow(new Object[]{
                    t.getId(),
                    t.getDescricao(),
                    categoria,
                    t.getDataTransacao().format(fmt),
                    String.format("%.2f", t.getValor()),
                    t.getTipo(),
                    status
            });
        }
        calcularFluxoCaixa();
    }

    private void configurarTabelaAlertas() {
        view.getTabela().setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                String status = table.getValueAt(row, 6).toString();
                String tipo = table.getValueAt(row, 5).toString();

                if ("ATRASADO".equals(status)) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("PAGO".equals(status)) {
                    c.setForeground(new Color(0, 100, 0));
                } else if ("PENDENTE".equals(status) && "DESPESA".equals(tipo)) {
                    c.setForeground(Color.ORANGE.darker());
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
    }

    private void adicionarTransacao(TipoTransacao tipo) {
        String descricao = JOptionPane.showInputDialog(view, "Descrição da " + tipo + ":");
        if (descricao == null || descricao.trim().isEmpty()) return;

        String valorStr = JOptionPane.showInputDialog(view, "Valor (R$):");
        if (valorStr == null) return;

        List<CategoriaFinanceira> categorias = categoriaRepo.listarTodos().stream()
                .filter(c -> c.getTipo() == tipo)
                .toList();

        Object[] catsArray = categorias.isEmpty() ? new Object[]{"Geral"} : categorias.stream().map(CategoriaFinanceira::getNome).toArray();

        Object catSelecionada = JOptionPane.showInputDialog(view, "Categoria:", "Categorização",
                JOptionPane.QUESTION_MESSAGE, null, catsArray, catsArray[0]);

        int catId = resolverIdCategoria(catSelecionada, categorias);

        String dataStr = JOptionPane.showInputDialog(view, "Data de Vencimento/Competência (dd/MM/yyyy):",
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        try {
            Transacao t = new Transacao();
            t.setDescricao(descricao);
            t.setValor(Double.parseDouble(valorStr.replace(",", ".")));
            t.setTipo(tipo);
            t.setCategoriaId(catId);
            t.setDataTransacao(LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            int pago = JOptionPane.showConfirmDialog(view, "Já foi pago/recebido?", "Status", JOptionPane.YES_NO_OPTION);
            if (pago == JOptionPane.YES_OPTION) {
                t.setDataPagamento(LocalDate.now());
            } else {
                t.setDataPagamento(null);
            }

            transacaoRepo.salvar(t);
            listarTransacoes();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erro nos dados: " + ex.getMessage());
        }
    }

    private void baixarConta() {
        int row = view.getTabela().getSelectedRow();
        if (row == -1) return;

        int id = (int) view.getTabela().getValueAt(row, 0);
        transacaoRepo.buscarPorId(id).ifPresent(t -> {
            if (t.getDataPagamento() != null) {
                JOptionPane.showMessageDialog(view, "Já está baixado.");
                return;
            }
            t.setDataPagamento(LocalDate.now());
            transacaoRepo.editar(t);
            listarTransacoes();
            JOptionPane.showMessageDialog(view, "Baixa realizada com sucesso!");
        });
    }

    private void emitirRecibo() {
        int row = view.getTabela().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione uma transação.");
            return;
        }
        int id = (int) view.getTabela().getValueAt(row, 0);
        Transacao t = transacaoRepo.buscarPorId(id).orElseThrow();

        if (t.getDataPagamento() == null) {
            JOptionPane.showMessageDialog(view, "Só é possível emitir recibo de contas pagas.");
            return;
        }

        String html = String.format("<html><body style='width: 300px'>" +
                        "<h2 style='text-align:center'>RECIBO</h2><hr>" +
                        "<b>Valor:</b> R$ %.2f<br>" +
                        "<b>Descrição:</b> %s<br>" +
                        "<b>Data Pagamento:</b> %s<br>" +
                        "<b>Tipo:</b> %s<br><br>" +
                        "<i>Assinado eletronicamente pelo Sistema Sapiens</i>" +
                        "</body></html>",
                t.getValor(), t.getDescricao(), t.getDataPagamento(), t.getTipo());

        JOptionPane.showMessageDialog(view, new JLabel(html), "Emissão de Recibo", JOptionPane.PLAIN_MESSAGE);
    }

    private void calcularFluxoCaixa() {
        List<Transacao> todas = transacaoRepo.listarTodos();

        double entradas = 0;
        double saidas = 0;

        for (Transacao t : todas) {
            if (t.getDataPagamento() != null) {
                if (t.getTipo() == TipoTransacao.RECEITA) {
                    entradas += t.getValor();
                } else {
                    saidas += t.getValor();
                }
            }
        }

        view.setSaldos(
                String.format("R$ %.2f", entradas),
                String.format("R$ %.2f", saidas),
                String.format("R$ %.2f", entradas - saidas)
        );
    }

    private void exportarCSV() {
        try {
            String path = "relatorio_financeiro.csv";
            PrintWriter pw = new PrintWriter(new FileWriter(path));

            pw.println("ID;Descricao;Categoria;Vencimento;Pagamento;Valor;Tipo");

            for (Transacao t : transacaoRepo.listarTodos()) {
                pw.printf("%d;%s;%d;%s;%s;%.2f;%s%n",
                        t.getId(), t.getDescricao(), t.getCategoriaId(),
                        t.getDataTransacao(), t.getDataPagamento(),
                        t.getValor(), t.getTipo());
            }
            pw.close();
            JOptionPane.showMessageDialog(view, "Exportado com sucesso para: " + path);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erro ao exportar: " + e.getMessage());
        }
    }

    private void excluirTransacao() {
        int row = view.getTabela().getSelectedRow();
        if (row == -1) return;
        int id = (int) view.getTabela().getValueAt(row, 0);
        transacaoRepo.excluir(id);
        listarTransacoes();
    }

    private String buscarNomeCategoria(int id) {
        return categoriaRepo.buscarPorId(id).map(CategoriaFinanceira::getNome).orElse("Geral");
    }

    private int resolverIdCategoria(Object nomeSelecionado, List<CategoriaFinanceira> lista) {
        if (nomeSelecionado == null) return 0;
        return lista.stream()
                .filter(c -> c.getNome().equals(nomeSelecionado.toString()))
                .findFirst()
                .map(CategoriaFinanceira::getId)
                .orElse(0);
    }
}