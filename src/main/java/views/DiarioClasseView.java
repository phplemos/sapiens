package views;

import views.components.ComboItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DiarioClasseView extends JDialog {

    private final JComboBox<ComboItem> cbPeriodo;
    private final JButton btnBuscar;

    private final JTable tabelaDiario;
    private final DefaultTableModel tableModel;

    private final JButton btnSalvar;
    private final JButton btnFechar;

    public DiarioClasseView(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Diário de Classe - Professor");
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.setBorder(new EmptyBorder(10, 10, 0, 10));

        painelTopo.add(new JLabel("Selecione o Período (Bimestre):"));
        cbPeriodo = new JComboBox<>();
        cbPeriodo.setPreferredSize(new Dimension(200, 25));
        painelTopo.add(cbPeriodo);

        btnBuscar = new JButton("Carregar Diário");
        painelTopo.add(btnBuscar);

        add(painelTopo, BorderLayout.NORTH);

        String[] colunas = {"ID_Ref", "Aluno", "Nota (0-10)", "Faltas (Qtd)"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Professor edita Nota (col 2) e Faltas (col 3)
                return col == 2 || col == 3;
            }
        };

        tabelaDiario = new JTable(tableModel);
        tabelaDiario.setRowHeight(30);
        tabelaDiario.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabelaDiario.getColumnModel().getColumn(0).setMinWidth(0);
        tabelaDiario.getColumnModel().getColumn(0).setMaxWidth(0);
        tabelaDiario.getColumnModel().getColumn(0).setPreferredWidth(0);

        tabelaDiario.getColumnModel().getColumn(1).setPreferredWidth(400); // Nome
        tabelaDiario.getColumnModel().getColumn(2).setPreferredWidth(100); // Nota
        tabelaDiario.getColumnModel().getColumn(3).setPreferredWidth(100); // Faltas

        add(new JScrollPane(tabelaDiario), BorderLayout.CENTER);

        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnFechar = new JButton("Fechar");
        btnSalvar = new JButton("💾 Salvar Diário");
        btnSalvar.setBackground(new Color(100, 200, 100));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Arial", Font.BOLD, 14));

        painelSul.add(btnFechar);
        painelSul.add(btnSalvar);

        add(painelSul, BorderLayout.SOUTH);
    }

    public JComboBox<ComboItem> getCbPeriodo() { return cbPeriodo; }
    public int getPeriodoSelecionadoId() {
        return (cbPeriodo.getSelectedItem() != null) ? ((ComboItem)cbPeriodo.getSelectedItem()).getId() : 0;
    }
    public void adicionarPeriodo(ComboItem item) { cbPeriodo.addItem(item); }

    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnFechar() { return btnFechar; }

    public DefaultTableModel getModel() { return tableModel; }
    public JTable getTabela() { return tabelaDiario; }
}