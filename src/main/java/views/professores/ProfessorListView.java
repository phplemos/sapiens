package views.professores;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProfessorListView extends JFrame {

    private JTable tabelaProfessores;
    private DefaultTableModel tableModel;
    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnExcluir;

    public ProfessorListView() {
        // --- Configuração básica da Janela ---
        setTitle("Sapiens - Gestão de Alunos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centralizar na tela
        setLayout(new BorderLayout(10, 10));

        // --- 1. Tabela (Centro) ---
        String[] colunas = {"ID", "Nome Completo", "CPF", "Email"};

        // O 'tableModel' permite adicionar e remover linhas da tabela dinamicamente
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Trava a edição direta na tabela
            }
        };
        tabelaProfessores = new JTable(tableModel);
        tabelaProfessores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Só pode selecionar 1 por vez

        // Adiciona a tabela a um painel com barra de rolagem
        JScrollPane scrollPane = new JScrollPane(tabelaProfessores);
        add(scrollPane, BorderLayout.CENTER);

        // --- 2. Painel de Botões (Sul) ---
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        btnNovo = new JButton("Novo Professor");
        btnEditar = new JButton("Editar Professor");
        btnExcluir = new JButton("Excluir Professor");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    // --- Métodos de Acesso (para o Controller) ---
    // O Controller usará estes métodos para adicionar listeners e pegar dados

    public JTable getTabelaProfessores() {
        return tabelaProfessores;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
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