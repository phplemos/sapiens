package views;

import views.components.ComboItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FinanceiroView extends JDialog {

    private JComboBox<ComboItem> cbAluno;
    private JButton btnBuscar;

    private JTable tabela;
    private DefaultTableModel tableModel;

    private JButton btnGerarCarne; // O Gerador
    private JButton btnBaixar;     // A Baixa

    private JLabel lblResumo; // Ex: "Total Pendente: R$ 500,00"

    public FinanceiroView() {
        setTitle("Financeiro - Mensalidades");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- 1. Filtros (Topo) ---
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.setBorder(new EmptyBorder(10, 10, 10, 10));

        painelTopo.add(new JLabel("Selecione o Aluno:"));
        cbAluno = new JComboBox<>();
        cbAluno.setPreferredSize(new Dimension(300, 25)); // Aumenta largura
        painelTopo.add(cbAluno);

        btnBuscar = new JButton("Listar Mensalidades");
        painelTopo.add(btnBuscar);

        add(painelTopo, BorderLayout.NORTH);

        // --- 2. Tabela (Centro) ---
        String[] colunas = {"ID", "Vencimento", "Valor (R$)", "Status", "Data Pagto"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(25);

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // --- 3. Ações (Sul) ---
        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblResumo = new JLabel("Selecione um aluno...");
        lblResumo.setFont(new Font("Arial", Font.BOLD, 14));
        painelSul.add(lblResumo, BorderLayout.WEST);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnGerarCarne = new JButton("⚙️ Gerar Carnê 2025 (12 meses)");
        btnBaixar = new JButton("💰 Confirmar Pagamento");
        btnBaixar.setBackground(new Color(200, 255, 200)); // Verde claro

        painelBotoes.add(btnGerarCarne);
        painelBotoes.add(btnBaixar);

        painelSul.add(painelBotoes, BorderLayout.EAST);
        add(painelSul, BorderLayout.SOUTH);
    }

    // Helpers
    public void adicionarAluno(ComboItem item) { cbAluno.addItem(item); }
    public int getAlunoSelecionadoPessoaId() {
        ComboItem item = (ComboItem) cbAluno.getSelectedItem();
        return (item != null) ? item.getId() : 0;
    }

    public JTable getTabela() { return tabela; }
    public DefaultTableModel getTableModel() { return tableModel; }

    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnGerarCarne() { return btnGerarCarne; }
    public JButton getBtnBaixar() { return btnBaixar; }
    public void setResumo(String texto) { lblResumo.setText(texto); }
}