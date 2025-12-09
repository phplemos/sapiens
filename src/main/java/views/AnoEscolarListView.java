package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AnoEscolarListView extends JDialog {

    private JTable tabela;
    private DefaultTableModel tableModel;
    private JButton btnNovo, btnEditar, btnExcluir;

    public AnoEscolarListView() {
        setTitle("Configuração - Anos Escolares");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Tabela
        String[] colunas = {"ID", "Ano", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNovo = new JButton("Novo Ano");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    // Getters
    public JTable getTabela() { return tabela; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JButton getBtnNovo() { return btnNovo; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnExcluir() { return btnExcluir; }
}