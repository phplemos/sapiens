package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BoletimView extends JDialog {

    private final JTable tabelaBoletim;
    private final DefaultTableModel tableModel;
    private final JLabel lblAlunoInfo;
    private final JLabel lblTurmaInfo;
    private final JButton btnFechar;

    public BoletimView(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Sapiens - Boletim Escolar");
        setSize(900, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel painelTopo = new JPanel(new GridLayout(2, 1));
        painelTopo.setBackground(new Color(245, 245, 245));
        painelTopo.setBorder(new EmptyBorder(10, 20, 10, 20));

        lblAlunoInfo = new JLabel("Aluno: Carregando...");
        lblAlunoInfo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTurmaInfo = new JLabel("Turma: -");

        painelTopo.add(lblAlunoInfo);
        painelTopo.add(lblTurmaInfo);
        add(painelTopo, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaBoletim = new JTable(tableModel);
        tabelaBoletim.setRowHeight(30);

        JScrollPane scroll = new JScrollPane(tabelaBoletim);
        scroll.setBorder(new TitledBorder("Notas e Frequência"));
        add(scroll, BorderLayout.CENTER);

        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnFechar = new JButton("Fechar");
        painelSul.add(btnFechar);
        add(painelSul, BorderLayout.SOUTH);
    }

    public void configurarTabela(List<String> nomesPeriodos) {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);

        tableModel.addColumn("Disciplina");

        for (String nome : nomesPeriodos) {
            tableModel.addColumn(nome);
        }

        tableModel.addColumn("Faltas");
        tableModel.addColumn("Situação");

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < tableModel.getColumnCount(); i++) {
            tabelaBoletim.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public void setInfo(String aluno, String turma) {
        lblAlunoInfo.setText("Aluno: " + aluno);
        lblTurmaInfo.setText("Turma: " + turma);
    }

    public DefaultTableModel getTableModel() { return tableModel; }
    public JButton getBtnFechar() { return btnFechar; }
}