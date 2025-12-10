package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TurmaListView extends JDialog {

    private final JTable tabela;
    private final DefaultTableModel tableModel;
    private final JButton btnNovo, btnEditar, btnExcluir, btnDisciplinas;

    public TurmaListView(Window parent) {
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        setTitle("Gestão de Turmas");
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        String[] colunas = {"ID", "Nome", "Turno", "Série", "Ano Letivo"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        tabela.getColumnModel().getColumn(1).setPreferredWidth(150); // Nome

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNovo = new JButton("Nova Turma");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnDisciplinas = new JButton("Gerenciar Disciplinas");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnDisciplinas);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    public JButton getBtnDisciplinas() { return btnDisciplinas; }
    public JTable getTabela() { return tabela; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JButton getBtnNovo() { return btnNovo; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnExcluir() { return btnExcluir; }
}