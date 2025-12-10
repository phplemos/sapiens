package views;

import views.components.ComboItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MensalidadesView extends JPanel {

    private JTabbedPane tabbedPane;

    // --- ABA 1: GESTÃO INDIVIDUAL ---
    private JComboBox<ComboItem> cbAluno;
    private JButton btnBuscar;
    private JTable tabela;
    private DefaultTableModel tableModel;
    private JButton btnGerarCarneAluno;
    private JButton btnBaixar;
    private JButton btnBoleto;    // RF029
    private JButton btnDesconto;  // RF032
    private JLabel lblResumo;

    // --- ABA 2: GESTÃO POR TURMA (RF028) ---
    private JComboBox<ComboItem> cbTurma;
    private JButton btnGerarParaTurma;
    private JTextArea txtLogTurma;

    // --- ABA 3: RELATÓRIOS (RF033, RF034) ---
    private JButton btnRelatorioInadimplentes;
    private JButton btnRelatorioPagos;
    private JTable tabelaRelatorio; // Tabela genérica para exibir relatórios
    private DefaultTableModel modelRelatorio;

    public MensalidadesView() {
        setSize(900, 650);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        initAbaIndividual();
        initAbaTurma();
        initAbaRelatorios();

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void initAbaIndividual() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10,10,10,10));

        // Topo
        JPanel pTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pTopo.add(new JLabel("Aluno:"));
        cbAluno = new JComboBox<>();
        cbAluno.setPreferredSize(new Dimension(300, 25));
        btnBuscar = new JButton("🔍 Listar");
        pTopo.add(cbAluno);
        pTopo.add(btnBuscar);
        panel.add(pTopo, BorderLayout.NORTH);

        // Centro (Tabela)
        String[] colunas = {"ID", "Vencimento", "Valor Original", "Juros/Multa", "Total", "Status", "Pagamento"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setRowHeight(25);
        panel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Sul (Ações)
        JPanel pSul = new JPanel(new BorderLayout());
        lblResumo = new JLabel("Resumo financeiro...");
        lblResumo.setFont(new Font("Arial", Font.BOLD, 13));
        pSul.add(lblResumo, BorderLayout.NORTH);

        JPanel pBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGerarCarneAluno = new JButton("⚙️ Gerar Carnê (Aluno)");
        btnDesconto = new JButton("🏷️ Aplicar Desconto");
        btnBoleto = new JButton("📄 Simular Boleto");
        btnBaixar = new JButton("💰 Baixar / Pagar");
        btnBaixar.setBackground(new Color(144, 238, 144));

        pBotoes.add(btnGerarCarneAluno);
        pBotoes.add(btnDesconto);
        pBotoes.add(btnBoleto);
        pBotoes.add(btnBaixar);
        pSul.add(pBotoes, BorderLayout.SOUTH);

        panel.add(pSul, BorderLayout.SOUTH);
        tabbedPane.addTab("Aluno Individual", panel);
    }

    private void initAbaTurma() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20,20,20,20));

        JPanel pForm = new JPanel(new GridLayout(4, 1, 10, 10));
        pForm.setBorder(new TitledBorder("Geração em Massa"));

        JPanel pSel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pSel.add(new JLabel("Selecione a Turma:"));
        cbTurma = new JComboBox<>();
        cbTurma.setPreferredSize(new Dimension(250, 25));
        pSel.add(cbTurma);

        btnGerarParaTurma = new JButton("🚀 Gerar Mensalidades para TODA a Turma");
        btnGerarParaTurma.setBackground(new Color(173, 216, 230));

        pForm.add(pSel);
        pForm.add(new JLabel("<html><i>Isso irá gerar mensalidades para o ano corrente (12 meses)<br>para todos os alunos ativos nesta turma.</i></html>"));
        pForm.add(btnGerarParaTurma);

        panel.add(pForm, BorderLayout.NORTH);

        txtLogTurma = new JTextArea();
        txtLogTurma.setEditable(false);
        panel.add(new JScrollPane(txtLogTurma), BorderLayout.CENTER);

        tabbedPane.addTab("Por Turma (Em Lote)", panel);
    }

    private void initAbaRelatorios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10,10,10,10));

        JPanel pTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRelatorioInadimplentes = new JButton("🔴 Ver Inadimplentes");
        btnRelatorioPagos = new JButton("🟢 Ver Recebimentos do Mês");

        pTopo.add(btnRelatorioInadimplentes);
        pTopo.add(btnRelatorioPagos);
        panel.add(pTopo, BorderLayout.NORTH);

        // Tabela de Relatório
        String[] col = {"Aluno", "Vencimento", "Valor", "Status"};
        modelRelatorio = new DefaultTableModel(col, 0);
        tabelaRelatorio = new JTable(modelRelatorio);
        panel.add(new JScrollPane(tabelaRelatorio), BorderLayout.CENTER);

        tabbedPane.addTab("Relatórios", panel);
    }

    // --- GETTERS E HELPERS ---
    public void adicionarAluno(ComboItem item) { cbAluno.addItem(item); }
    public void adicionarTurma(ComboItem item) { cbTurma.addItem(item); }

    public int getAlunoSelecionadoPessoaId() {
        ComboItem item = (ComboItem) cbAluno.getSelectedItem();
        return (item != null) ? item.getId() : 0;
    }
    public int getTurmaSelecionadaId() {
        ComboItem item = (ComboItem) cbTurma.getSelectedItem();
        return (item != null) ? item.getId() : 0;
    }

    public DefaultTableModel getTableModel() { return tableModel; }
    public DefaultTableModel getModelRelatorio() { return modelRelatorio; }
    public JTable getTabela() { return tabela; }
    public void setResumo(String t) { lblResumo.setText(t); }
    public void appendLog(String t) { txtLogTurma.append(t + "\n"); }

    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnGerarCarneAluno() { return btnGerarCarneAluno; }
    public JButton getBtnBaixar() { return btnBaixar; }
    public JButton getBtnDesconto() { return btnDesconto; }
    public JButton getBtnBoleto() { return btnBoleto; }
    public JButton getBtnGerarParaTurma() { return btnGerarParaTurma; }
    public JButton getBtnRelatorioInadimplentes() { return btnRelatorioInadimplentes; }
    public JButton getBtnRelatorioPagos() { return btnRelatorioPagos; }
}