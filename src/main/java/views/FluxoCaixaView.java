package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FluxoCaixaView extends JPanel {

    private JTabbedPane abas;

    private JTable tabelaTransacoes;
    private DefaultTableModel tableModel;
    private JButton btnNovaReceita;
    private JButton btnNovaDespesa; // RF051 (Contas a Pagar)
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnBaixar; // Pagar uma conta pendente
    private JButton btnRecibo; // RF047

    private JLabel lblSaldoEntradas;
    private JLabel lblSaldoSaidas;
    private JLabel lblSaldoFinal; // RF048
    private JButton btnExportarCSV; // RF050
    private JButton btnAtualizarRelatorio;

    public FluxoCaixaView() {
        setSize(950, 600);
        setLayout(new BorderLayout());

        abas = new JTabbedPane();

        initAbaLancamentos();
        initAbaRelatorios();

        add(abas, BorderLayout.CENTER);
    }

    private void initAbaLancamentos() {
        JPanel painel = new JPanel(new BorderLayout(10,10));
        painel.setBorder(new EmptyBorder(10,10,10,10));

        String[] colunas = {"ID", "Descrição", "Categoria", "Data", "Valor", "Tipo", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaTransacoes = new JTable(tableModel);
        tabelaTransacoes.setRowHeight(25);
        painel.add(new JScrollPane(tabelaTransacoes), BorderLayout.CENTER);

        JPanel pSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNovaReceita = new JButton("➕ Nova Receita");
        btnNovaReceita.setBackground(new Color(144, 238, 144));

        btnNovaDespesa = new JButton("➖ Nova Despesa / Conta a Pagar");
        btnNovaDespesa.setBackground(new Color(255, 182, 193));

        btnBaixar = new JButton("✅ Baixar/Pagar");
        btnRecibo = new JButton("📄 Recibo");
        btnExcluir = new JButton("🗑️ Excluir");

        pSul.add(btnNovaReceita);
        pSul.add(btnNovaDespesa);
        pSul.add(Box.createHorizontalStrut(20));
        pSul.add(btnBaixar);
        pSul.add(btnRecibo);
        pSul.add(btnExcluir);

        painel.add(pSul, BorderLayout.SOUTH);
        abas.addTab("Lançamentos & Contas", painel);
    }

    private void initAbaRelatorios() {
        JPanel painel = new JPanel(new BorderLayout(10,10));
        painel.setBorder(new EmptyBorder(20,20,20,20));

        JPanel pValores = new JPanel(new GridLayout(1, 3, 20, 0));

        lblSaldoEntradas = criarCard("Entradas (Receitas)", Color.BLUE);
        lblSaldoSaidas = criarCard("Saídas (Despesas)", Color.RED);
        lblSaldoFinal = criarCard("Saldo Líquido", new Color(0, 100, 0));

        pValores.add(lblSaldoEntradas);
        pValores.add(lblSaldoSaidas);
        pValores.add(lblSaldoFinal);

        painel.add(pValores, BorderLayout.CENTER);

        JPanel pSul = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnAtualizarRelatorio = new JButton("🔄 Recalcular Fluxo");
        btnExportarCSV = new JButton("📂 Exportar para Excel/CSV");

        pSul.add(btnAtualizarRelatorio);
        pSul.add(btnExportarCSV);
        painel.add(pSul, BorderLayout.SOUTH);

        abas.addTab("Relatórios & Fluxo", painel);
    }

    private JLabel criarCard(String titulo, Color cor) {
        JLabel lbl = new JLabel("R$ 0,00", SwingConstants.CENTER);
        lbl.setBorder(new TitledBorder(titulo));
        lbl.setFont(new Font("Arial", Font.BOLD, 24));
        lbl.setForeground(cor);
        return lbl;
    }

    public JTable getTabela() { return tabelaTransacoes; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JButton getBtnNovaReceita() { return btnNovaReceita; }
    public JButton getBtnNovaDespesa() { return btnNovaDespesa; }
    public JButton getBtnBaixar() { return btnBaixar; }
    public JButton getBtnRecibo() { return btnRecibo; }
    public JButton getBtnExcluir() { return btnExcluir; }
    public JButton getBtnExportarCSV() { return btnExportarCSV; }
    public JButton getBtnAtualizarRelatorio() { return btnAtualizarRelatorio; }

    public void setSaldos(String in, String out, String total) {
        lblSaldoEntradas.setText(in);
        lblSaldoSaidas.setText(out);
        lblSaldoFinal.setText(total);
    }
}