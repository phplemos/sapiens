package views;

import views.components.ComboItem;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardProfessorView extends JFrame {

    private final JLabel lblNomeProf, lblCargaHorariaTotal;

    private final JComboBox<ComboItem> cbTurmas;
    private final JButton btnCarregarTurma;

    private final JTable tabelaAlunos;
    private final DefaultTableModel modelAlunos;

    private final JButton btnLancarNotasFrequencia; // RF019
    private final JButton btnVerHistoricoAluno;      // RF020
    private final JButton btnSair;

    public DashboardProfessorView() {
        setTitle("Portal do Professor - Sapiens");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBorder(new TitledBorder("Dados do Professor"));

        JPanel pDados = new JPanel(new GridLayout(2, 1));
        lblNomeProf = new JLabel("Professor: -");
        lblNomeProf.setFont(new Font("Arial", Font.BOLD, 16));

        lblCargaHorariaTotal = new JLabel("Carga Horária Total: 0h");
        lblCargaHorariaTotal.setForeground(new Color(0, 100, 0)); // Verde escuro

        pDados.add(lblNomeProf);
        pDados.add(lblCargaHorariaTotal);

        JPanel pSeletor = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pSeletor.add(new JLabel("Selecione a Turma/Disciplina:"));
        cbTurmas = new JComboBox<>();
        cbTurmas.setPreferredSize(new Dimension(300, 25));
        pSeletor.add(cbTurmas);
        btnCarregarTurma = new JButton("Carregar Alunos");
        pSeletor.add(btnCarregarTurma);

        painelTopo.add(pDados, BorderLayout.WEST);
        painelTopo.add(pSeletor, BorderLayout.EAST);

        add(painelTopo, BorderLayout.NORTH);

        String[] colunas = {"ID_Aluno", "Nome do Aluno", "Nº Matrícula", "Situação"};
        modelAlunos = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaAlunos = new JTable(modelAlunos);
        tabelaAlunos.setRowHeight(25);
        tabelaAlunos.getColumnModel().getColumn(0).setMinWidth(0); // Esconde ID
        tabelaAlunos.getColumnModel().getColumn(0).setMaxWidth(0);

        add(new JScrollPane(tabelaAlunos), BorderLayout.CENTER);

        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnVerHistoricoAluno = new JButton("📄 Ver Histórico Completo (RF020)");
        btnLancarNotasFrequencia = new JButton("📝 Lançar Notas e Frequência (RF019)");
        btnSair = new JButton("Sair");

        painelSul.add(btnVerHistoricoAluno);
        painelSul.add(btnLancarNotasFrequencia);
        painelSul.add(btnSair);

        add(painelSul, BorderLayout.SOUTH);
    }

    public void setDadosProfessor(String nome, int cargaHoraria) {
        lblNomeProf.setText("Professor: " + nome);
        lblCargaHorariaTotal.setText("Carga Horária Total: " + cargaHoraria + "h");
    }

    public void adicionarTurmaCombo(ComboItem item) { cbTurmas.addItem(item); }
    public int getTurmaDisciplinaSelecionadaId() {
        return (cbTurmas.getSelectedItem() != null) ? ((ComboItem)cbTurmas.getSelectedItem()).getId() : 0;
    }

    public DefaultTableModel getModelAlunos() { return modelAlunos; }
    public JTable getTabelaAlunos() { return tabelaAlunos; }

    public JButton getBtnCarregar() { return btnCarregarTurma; }
    public JButton getBtnLancar() { return btnLancarNotasFrequencia; }
    public JButton getBtnHistorico() { return btnVerHistoricoAluno; }
    public JButton getBtnSair() { return btnSair; }
}