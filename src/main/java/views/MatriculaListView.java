package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MatriculaListView extends JDialog {

    private final JTable tabela;
    private final DefaultTableModel tableModel;
    private final JButton btnNovaMatricula, btnExcluir;

    public MatriculaListView(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Gestão de Matrículas");
        setSize(800, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        String[] colunas = {"ID", "Aluno", "Turma", "Data", "Nº Matrícula"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNovaMatricula = new JButton("Nova Matrícula");
        btnExcluir = new JButton("Cancelar Matrícula");

        painelBotoes.add(btnNovaMatricula);
        painelBotoes.add(btnExcluir);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    public JTable getTabela() { return tabela; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JButton getBtnNovaMatricula() { return btnNovaMatricula; }
    public JButton getBtnExcluir() { return btnExcluir; }
}