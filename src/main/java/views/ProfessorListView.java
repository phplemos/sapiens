package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProfessorListView extends JDialog {

    private final JTable tabelaProfessores;
    private final DefaultTableModel tableModel;
    private final JButton btnNovo;
    private final JButton btnEditar;
    private final JButton btnExcluir;
    private final JButton btnBuscar;
    private final JTextField txtBusca;

    public ProfessorListView(Window parent) {
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        setTitle("Sapiens - Gestão de Alunos");
        setSize(800, 600);
        setLocationRelativeTo(parent);

        setLayout(new BorderLayout(10, 10));

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBusca = new JTextField(20);
        btnBuscar = new JButton("Buscar por Nome/CPF");
        painelTopo.add(new JLabel("Pesquisar:"));
        painelTopo.add(txtBusca);
        painelTopo.add(btnBuscar);
        add(painelTopo, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome Completo", "CPF", "Email"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Trava a edição direta na tabela
            }
        };
        tabelaProfessores = new JTable(tableModel);
        tabelaProfessores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Só pode selecionar 1 por vez

        JScrollPane scrollPane = new JScrollPane(tabelaProfessores);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        btnNovo = new JButton("Novo Professor");
        btnEditar = new JButton("Editar Professor");
        btnExcluir = new JButton("Excluir Professor");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    public JTable getTabelaProfessores() {
        return tabelaProfessores;
    }
    public DefaultTableModel getTableModel() {
        return tableModel;
    }
    public JTextField getTxtBusca() { return txtBusca; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnNovo() {
        return btnNovo;
    }
    public JButton getBtnEditar() {
        return btnEditar;
    }
    public JButton getBtnExcluir() {
        return btnExcluir;
    }
}