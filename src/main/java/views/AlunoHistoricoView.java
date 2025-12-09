package views;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AlunoHistoricoView extends JDialog {

    // Labels para dados do Perfil
    private final JLabel lblNome, lblCpf, lblEmail, lblTelefone;
    private final JLabel lblEndereco;

    // Tabela para o Histórico
    private final JTable tabelaHistorico;
    private final DefaultTableModel tableModel;

    private final JButton btnFechar;

    public AlunoHistoricoView(JDialog parent) {
        super(parent, "Perfil e Histórico do Aluno", ModalityType.APPLICATION_MODAL);
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // --- 1. PAINEL DE PERFIL (Topo) ---
        JPanel painelPerfil = new JPanel(new GridLayout(3, 2, 10, 5));
        painelPerfil.setBorder(new TitledBorder("Dados do Perfil"));
        painelPerfil.setBackground(new Color(245, 245, 245)); // Cinza claro

        lblNome = new JLabel("Nome: -");
        lblNome.setFont(new Font("Arial", Font.BOLD, 14));

        lblCpf = new JLabel("CPF: -");
        lblEmail = new JLabel("Email: -");
        lblTelefone = new JLabel("Tel: -");
        lblEndereco = new JLabel("Endereço: -");

        painelPerfil.add(lblNome);
        painelPerfil.add(lblCpf);
        painelPerfil.add(lblEmail);
        painelPerfil.add(lblTelefone);
        painelPerfil.add(lblEndereco);

        add(painelPerfil, BorderLayout.NORTH);

        // --- 2. PAINEL DE HISTÓRICO (Centro) ---
        // Colunas: Ano Letivo | Turma | Disciplina | Período (Bimestre) | Nota | Faltas
        String[] colunas = {"Ano", "Turma", "Disciplina", "Período", "Nota", "Faltas"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; } // Read-only
        };

        tabelaHistorico = new JTable(tableModel);
        tabelaHistorico.setRowHeight(25);
        tabelaHistorico.getColumnModel().getColumn(0).setPreferredWidth(60); // Ano menor
        tabelaHistorico.getColumnModel().getColumn(4).setPreferredWidth(50); // Nota menor

        JScrollPane scroll = new JScrollPane(tabelaHistorico);
        scroll.setBorder(new TitledBorder("Histórico Acadêmico"));

        add(scroll, BorderLayout.CENTER);

        // --- 3. RODAPÉ ---
        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnFechar = new JButton("Fechar");
        painelSul.add(btnFechar);
        add(painelSul, BorderLayout.SOUTH);
    }

    // Setters para preencher os dados
    public void setDadosPerfil(String nome, String cpf, String email, String tel, String end) {
        lblNome.setText("Nome: " + nome);
        lblCpf.setText("CPF: " + cpf);
        lblEmail.setText("Email: " + email);
        lblTelefone.setText("Tel: " + tel);
        lblEndereco.setText("Endereço: " + end);
    }

    public DefaultTableModel getTableModel() { return tableModel; }
    public JButton getBtnFechar() { return btnFechar; }
}