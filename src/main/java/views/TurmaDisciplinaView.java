package views;

import views.components.ComboItem;
import views.components.SearchComboBox;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TurmaDisciplinaView extends JDialog {

    private final JComboBox<ComboItem> cbDisciplina;
    private final JComboBox<ComboItem> cbProfessor;
    private final JButton btnAdicionar;
    private final JButton btnRemover;

    private final JTable tabela;
    private final DefaultTableModel tableModel;

    private final JLabel lblTituloTurma;

    public TurmaDisciplinaView(JDialog parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblTituloTurma = new JLabel("Turma: Carregando...");
        lblTituloTurma.setFont(new Font("Arial", Font.BOLD, 16));
        lblTituloTurma.setBorder(new EmptyBorder(0, 0, 10, 0));
        painelTopo.add(lblTituloTurma, BorderLayout.NORTH);

        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(new TitledBorder("Adicionar Disciplina à Turma"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        painelForm.add(new JLabel("Disciplina:"), gbc);

        gbc.gridy = 1;
        cbDisciplina = new JComboBox<>();
        painelForm.add(cbDisciplina, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.4;
        painelForm.add(new JLabel("Professor:"), gbc);

        gbc.gridy = 1;
        cbProfessor = new JComboBox<>();
        painelForm.add(cbProfessor, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.2; gbc.fill = GridBagConstraints.NONE;
        btnAdicionar = new JButton("Adicionar");
        painelForm.add(btnAdicionar, gbc);

        painelTopo.add(painelForm, BorderLayout.CENTER);
        add(painelTopo, BorderLayout.NORTH);

        String[] colunas = {"ID Vínculo", "Disciplina", "Professor"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRemover = new JButton("Remover Selecionada");
        painelSul.add(btnRemover);
        add(painelSul, BorderLayout.SOUTH);
    }

    public void setTituloTurma(String nome) { lblTituloTurma.setText("Gerenciando: " + nome); }

    public void adicionarDisciplinaCombo(ComboItem item) { cbDisciplina.addItem(item); }
    public int getDisciplinaSelecionadaId() {
        ComboItem item = (ComboItem) cbDisciplina.getSelectedItem();
        return (item != null) ? item.getId() : 0;
    }

    public void adicionarProfessorCombo(ComboItem item) { cbProfessor.addItem(item); }
    public int getProfessorSelecionadoPessoaId() {
        ComboItem item = (ComboItem) cbProfessor.getSelectedItem();
        return (item != null) ? item.getId() : 0; // Aqui o ID é o pessoaId do professor
    }
    public JComboBox<ComboItem> getCbDisciplina() {
        return cbDisciplina;
    }
    public JComboBox<ComboItem> getCbProfessor() {
        return cbProfessor;
    }
    public JButton getBtnAdicionar() { return btnAdicionar; }
    public JButton getBtnRemover() { return btnRemover; }
    public JTable getTabela() { return tabela; }
    public DefaultTableModel getTableModel() { return tableModel; }
}