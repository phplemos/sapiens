package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AlunoListView extends JDialog {

    private JTable tabelaAlunos;
    private DefaultTableModel tableModel;
    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnBuscar;
    private JTextField txtBusca;
    private JButton btnHistorico;

    public AlunoListView() {
        setTitle("Sapiens - Gestão de Alunos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centralizar na tela
        setLayout(new BorderLayout(10, 10));

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBusca = new JTextField(20);
        btnBuscar = new JButton("Buscar por Nome/CPF");
        painelTopo.add(new JLabel("Pesquisar:"));
        painelTopo.add(txtBusca);
        painelTopo.add(btnBuscar);
        add(painelTopo, BorderLayout.NORTH);

        // --- 1. Tabela (Centro) ---
        String[] colunas = {"ID", "Nome Completo", "CPF", "Email"};

        // O 'tableModel' permite adicionar e remover linhas da tabela dinamicamente
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Trava a edição direta na tabela
            }
        };
        tabelaAlunos = new JTable(tableModel);
        tabelaAlunos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Só pode selecionar 1 por vez

        // Adiciona a tabela a um painel com barra de rolagem
        JScrollPane scrollPane = new JScrollPane(tabelaAlunos);
        add(scrollPane, BorderLayout.CENTER);

        // --- 2. Painel de Botões (Sul) ---
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        btnNovo = new JButton("Novo Aluno");
        btnEditar = new JButton("Editar Aluno");
        btnExcluir = new JButton("Excluir Aluno");
        btnHistorico = new JButton("📄 Ver Perfil/Histórico");
        painelBotoes.add(btnHistorico);
        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    // --- Métodos de Acesso (para o Controller) ---
    // O Controller usará estes métodos para adicionar listeners e pegar dados
    public JButton getBtnHistorico() { return btnHistorico; }
    public JTable getTabelaAlunos() {
        return tabelaAlunos;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtBusca() {
        return txtBusca;
    }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }

    public JButton getBtnNovo() {
        return btnNovo;
    }

    public JButton getBtnEditar() {
        return btnEditar;
    }

    public JButton getBtnExcluir() {
        return btnExcluir;
    }
}