package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DisciplinaListView extends JDialog {

    private final JTable tabelaDisciplinas;
    private final DefaultTableModel tableModel;
    private final JButton btnNovo;
    private final JButton btnEditar;
    private final JButton btnExcluir;
    private final JTextField txtBusca; // Campo extra para busca (RF025)
    private final JButton btnBuscar;

    public DisciplinaListView(Window parent) {
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        setTitle("Sapiens - Gestão de Disciplinas");
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBusca = new JTextField(20);
        btnBuscar = new JButton("Buscar por Nome/Código");
        painelTopo.add(new JLabel("Pesquisar:"));
        painelTopo.add(txtBusca);
        painelTopo.add(btnBuscar);
        add(painelTopo, BorderLayout.NORTH);


        String[] colunas = {"ID", "Código", "Nome", "C. Horária"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaDisciplinas = new JTable(tableModel);
        tabelaDisciplinas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabelaDisciplinas.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID pequeno
        tabelaDisciplinas.getColumnModel().getColumn(1).setPreferredWidth(100); // Código
        tabelaDisciplinas.getColumnModel().getColumn(2).setPreferredWidth(400); // Nome grande

        add(new JScrollPane(tabelaDisciplinas), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNovo = new JButton("Nova Disciplina");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    public JTable getTabelaDisciplinas() { return tabelaDisciplinas; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JButton getBtnNovo() { return btnNovo; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnExcluir() { return btnExcluir; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public String getTermoBusca() { return txtBusca.getText(); }
}