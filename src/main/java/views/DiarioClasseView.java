package views;

import models.TurmaDisciplina;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DiarioClasseView extends JDialog {

    private JLabel lblInfoTurma;
    private JComboBox<String> cbPeriodo; // Selecionar Bimestre
    private JButton btnCarregarPeriodo;

    private JTable tabelaDiario;
    private DefaultTableModel modelDiario;

    private JButton btnSalvar;
    private JButton btnFechar;

    public DiarioClasseView(Window parent) {
        super(parent, "Diário de Classe Digital", ModalityType.APPLICATION_MODAL);
        setSize(800, 500
        );
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Topo
        JPanel pTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblInfoTurma = new JLabel("Turma: - | Disciplina: -");
        lblInfoTurma.setFont(new Font("Arial", Font.BOLD, 14));

        pTopo.add(lblInfoTurma);
        pTopo.add(new JLabel("   |   Selecione o Período:"));
        cbPeriodo = new JComboBox<>(); // Será preenchido pelo Controller
        pTopo.add(cbPeriodo);

        btnCarregarPeriodo = new JButton("Carregar");
        pTopo.add(btnCarregarPeriodo);

        add(pTopo, BorderLayout.NORTH);

        // Tabela Editável (Notas e Faltas)
        // Colunas: ID_MatriculaDisc (Oculto), Nome Aluno, Nota (Edit), Faltas (Edit)
        String[] colunas = {"ID_MD", "Aluno", "Nota (0-10)", "Total Faltas"};
        modelDiario = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 2 || col == 3; // Só edita Nota e Faltas
            }
        };
        tabelaDiario = new JTable(modelDiario);
        tabelaDiario.setRowHeight(30);
        tabelaDiario.getColumnModel().getColumn(0).setMinWidth(0);
        tabelaDiario.getColumnModel().getColumn(0).setMaxWidth(0);

        add(new JScrollPane(tabelaDiario), BorderLayout.CENTER);

        // Rodapé
        JPanel pSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("💾 Salvar Lançamentos");
        btnFechar = new JButton("Fechar");
        pSul.add(btnSalvar);
        pSul.add(btnFechar);
        add(pSul, BorderLayout.SOUTH);
    }

    // Getters
    public void setInfoTurma(String info) { lblInfoTurma.setText(info); }
    public JComboBox<String> getCbPeriodo() { return cbPeriodo; }
    public JButton getBtnCarregar() { return btnCarregarPeriodo; }
    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnFechar() { return btnFechar; }
    public JTable getTabela() { return tabelaDiario; }
    public DefaultTableModel getModel() { return modelDiario; }
}