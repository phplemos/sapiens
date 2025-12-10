package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PeriodoLetivoListView extends JDialog {
    private final JTable tabela;
    private final DefaultTableModel tableModel;
    private final JButton btnNovo, btnEditar, btnExcluir;

    public PeriodoLetivoListView(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Configuração - Períodos Letivos");
        setSize(600, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        String[] colunas = {"ID", "Nome", "Ano (ID)", "Início", "Fim"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNovo = new JButton("Novo Período");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    public JTable getTabela() { return tabela; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JButton getBtnNovo() { return btnNovo; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnExcluir() { return btnExcluir; }
}