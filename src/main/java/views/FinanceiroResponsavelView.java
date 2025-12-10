package views;

import views.components.ComboItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FinanceiroResponsavelView extends JDialog {

    private final JComboBox<ComboItem> cbFilhos;
    private final JTable tabela;
    private final DefaultTableModel tableModel;
    private final JButton btnVisualizarBoleto;
    private final JButton btnFechar;
    private final JLabel lblResumo;

    public FinanceiroResponsavelView(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Meu Financeiro - Mensalidades");
        setSize(800, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.setBorder(new EmptyBorder(10, 10, 0, 10));
        painelTopo.add(new JLabel("Selecione o Dependente:"));

        cbFilhos = new JComboBox<>();
        cbFilhos.setPreferredSize(new Dimension(300, 30));
        painelTopo.add(cbFilhos);

        add(painelTopo, BorderLayout.NORTH);


        String[] colunas = {"ID", "Vencimento", "Valor", "Encargos", "Total a Pagar", "Status", "Pago em"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tabela = new JTable(tableModel);
        tabela.setRowHeight(30);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configurarCoresTabela();

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(new TitledBorder("Extrato Financeiro"));
        add(scroll, BorderLayout.CENTER);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblResumo = new JLabel("Total em Aberto: R$ 0,00");
        lblResumo.setFont(new Font("Arial", Font.BOLD, 14));
        lblResumo.setForeground(Color.RED);

        JPanel pBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnVisualizarBoleto = new JButton("📄 Visualizar Boleto / Pagar");
        btnFechar = new JButton("Fechar");

        pBotoes.add(btnVisualizarBoleto);
        pBotoes.add(btnFechar);

        painelSul.add(lblResumo, BorderLayout.WEST);
        painelSul.add(pBotoes, BorderLayout.EAST);

        add(painelSul, BorderLayout.SOUTH);
    }

    private void configurarCoresTabela() {
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                String status = table.getValueAt(row, 5).toString();

                if ("ATRASADO".equals(status)) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("PAGO".equals(status)) {
                    c.setForeground(new Color(0, 128, 0));
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
    }

    public void adicionarFilho(ComboItem item) { cbFilhos.addItem(item); }
    public JComboBox<ComboItem> getCbFilhos() { return cbFilhos; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getTabela() { return tabela; }
    public JButton getBtnVisualizarBoleto() { return btnVisualizarBoleto; }
    public JButton getBtnFechar() { return btnFechar; }
    public void setResumo(String texto) { lblResumo.setText(texto); }
}