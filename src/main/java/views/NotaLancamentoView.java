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

    private JButton btnSalvarCorrecao;
    private JButton btnPublicarBoletim;
    private JLabel lblStatusBoletim; // Para mostrar se já está publicado ou não
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
        String[] colunas = {"ID_MatDisc", "Aluno", "Nota Atual", "Faltas Atuais"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Admin também pode editar nota e falta para corrigir erros
                return col == 2 || col == 3;
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

        // --- 3. Painel de Controle (Sul) ---
        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.setBorder(new EmptyBorder(10,10,10,10));

        // Info de Status à esquerda
        lblStatusBoletim = new JLabel("Status: Aguardando seleção...");
        lblStatusBoletim.setFont(new Font("Arial", Font.BOLD, 12));
        painelSul.add(lblStatusBoletim, BorderLayout.WEST);

        // Botões à direita
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnSalvarCorrecao = new JButton("Salvar Correções");

        btnPublicarBoletim = new JButton("📢 Publicar/Ocultar Boletim");
        btnPublicarBoletim.setBackground(new Color(255, 140, 0)); // Laranja
        btnPublicarBoletim.setForeground(Color.WHITE);
        btnPublicarBoletim.setFont(new Font("Arial", Font.BOLD, 12));

        painelBotoes.add(btnSalvarCorrecao);
        painelBotoes.add(Box.createHorizontalStrut(20)); // Espaço
        painelBotoes.add(btnPublicarBoletim);

        painelSul.add(painelBotoes, BorderLayout.EAST);
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
    public JButton getBtnPublicar() { return btnPublicarBoletim; }
    public JComboBox<ComboItem> getCbTurma() { return cbTurma; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnSalvarCorrecao() { return btnSalvarCorrecao; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getTabela() { return tabelaNotas; }
}