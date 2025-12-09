package views;

import views.components.ComboItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NotaLancamentoView extends JDialog {

    private JComboBox<ComboItem> cbTurma;
    private JComboBox<ComboItem> cbDisciplina;
    private JComboBox<ComboItem> cbPeriodo;
    private JButton btnBuscar;

    private JTable tabelaNotas;
    private DefaultTableModel tableModel;

    private JButton btnSalvarNotas;

    public NotaLancamentoView() {
        setTitle("Lançamento de Notas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- 1. Painel de Filtros (Topo) ---
        JPanel painelFiltros = new JPanel(new GridLayout(2, 4, 10, 10)); // Grid para alinhar
        painelFiltros.setBorder(new EmptyBorder(10, 10, 10, 10));

        painelFiltros.add(new JLabel("1. Selecione a Turma:"));
        painelFiltros.add(new JLabel("2. Selecione a Disciplina:"));
        painelFiltros.add(new JLabel("3. Selecione o Período:"));
        painelFiltros.add(new JLabel("")); // Espaço vazio

        cbTurma = new JComboBox<>();
        painelFiltros.add(cbTurma);

        cbDisciplina = new JComboBox<>();
        painelFiltros.add(cbDisciplina);

        cbPeriodo = new JComboBox<>();
        painelFiltros.add(cbPeriodo);

        btnBuscar = new JButton("Listar Alunos");
        painelFiltros.add(btnBuscar);

        add(painelFiltros, BorderLayout.NORTH);

        // --- 2. Tabela de Notas (Centro) ---
        // Colunas: ID (Oculto/Ref), Nome do Aluno, Nota (Editável)
        String[] colunas = {"ID_MatDisc", "Nome do Aluno", "Nota (0-10)"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // SÓ a coluna 2 (Nota) é editável
                return col == 2;
            }
        };

        tabelaNotas = new JTable(tableModel);
        tabelaNotas.setRowHeight(25); // Altura melhor para digitar
        tabelaNotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Define a largura das colunas
        tabelaNotas.getColumnModel().getColumn(0).setPreferredWidth(0); // ID pequeno/oculto
        tabelaNotas.getColumnModel().getColumn(0).setMinWidth(0);
        tabelaNotas.getColumnModel().getColumn(0).setMaxWidth(0); // Truque para esconder a coluna ID

        tabelaNotas.getColumnModel().getColumn(1).setPreferredWidth(400); // Nome grande

        add(new JScrollPane(tabelaNotas), BorderLayout.CENTER);

        // --- 3. Botão Salvar (Sul) ---
        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvarNotas = new JButton("💾 Salvar Todas as Notas");
        btnSalvarNotas.setFont(new Font("Arial", Font.BOLD, 14));
        btnSalvarNotas.setBackground(new Color(200, 255, 200)); // Verdinho claro

        painelSul.add(btnSalvarNotas);
        add(painelSul, BorderLayout.SOUTH);
    }

    // --- Métodos Auxiliares ---

    // Combos
    public void adicionarTurma(ComboItem item) { cbTurma.addItem(item); }
    public int getTurmaSelecionadaId() {
        return (cbTurma.getSelectedItem() != null) ? ((ComboItem)cbTurma.getSelectedItem()).getId() : 0;
    }

    public void limparDisciplinas() { cbDisciplina.removeAllItems(); }
    public void adicionarDisciplina(ComboItem item) { cbDisciplina.addItem(item); }
    public int getDisciplinaSelecionadaId() {
        return (cbDisciplina.getSelectedItem() != null) ? ((ComboItem)cbDisciplina.getSelectedItem()).getId() : 0;
    }

    public void limparPeriodos() { cbPeriodo.removeAllItems(); }
    public void adicionarPeriodo(ComboItem item) { cbPeriodo.addItem(item); }
    public int getPeriodoSelecionadoId() {
        return (cbPeriodo.getSelectedItem() != null) ? ((ComboItem)cbPeriodo.getSelectedItem()).getId() : 0;
    }

    // Componentes
    public JComboBox<ComboItem> getCbTurma() { return cbTurma; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnSalvarNotas() { return btnSalvarNotas; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getTabela() { return tabelaNotas; }
}