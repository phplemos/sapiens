package views;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UsuarioPerfilView extends JFrame {

    // --- Parte Fixa: Dados Pessoais ---
    private JLabel lblNome, lblCpf, lblEmail, lblTelefone;
    private JLabel lblTipoUsuario; // "Aluno", "Professor" ou "Admin"

    // --- Parte Dinâmica: Painel Acadêmico ---
    private JPanel painelAcademico;

    // Componentes para ALUNO (Exibe apenas um label com a turma)
    private JLabel lblTurmaAtual;

    // Componentes para PROFESSOR (Exibe tabela de turmas)
    private JTable tabelaTurmasProf;
    private DefaultTableModel modelTurmasProf;

    private JTable tabelaDisciplinasAluno;
    private DefaultTableModel modelDisciplinasAluno;
    private JButton btnVerConteudo;

    private JButton btnSair;

    public UsuarioPerfilView() {
        setTitle("Meu Perfil - Sapiens");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha o app ao sair daqui
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- 1. CABEÇALHO (Dados Pessoais) ---
        JPanel painelDados = new JPanel(new GridLayout(5, 1, 5, 5));
        painelDados.setBorder(new TitledBorder("Meus Dados"));
        painelDados.setBackground(new Color(245, 245, 250)); // Cor suave

        lblTipoUsuario = new JLabel("Tipo: -");
        lblTipoUsuario.setForeground(Color.BLUE);
        lblNome = new JLabel("Nome: -");
        lblNome.setFont(new Font("Arial", Font.BOLD, 14));
        lblCpf = new JLabel("CPF: -");
        lblEmail = new JLabel("Email: -");
        lblTelefone = new JLabel("Telefone: -");

        painelDados.add(lblTipoUsuario);
        painelDados.add(lblNome);
        painelDados.add(lblCpf);
        painelDados.add(lblEmail);
        painelDados.add(lblTelefone);

        add(painelDados, BorderLayout.NORTH);

        // --- 2. CENTRO (Dados Acadêmicos - Variavel) ---
        painelAcademico = new JPanel(new BorderLayout());
        painelAcademico.setBorder(new TitledBorder("Informações Acadêmicas"));

        // Inicialmente vazio, o Controller vai preencher
        add(painelAcademico, BorderLayout.CENTER);

        // --- 3. RODAPÉ ---
        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSair = new JButton("Sair do Sistema");
        painelSul.add(btnSair);
        add(painelSul, BorderLayout.SOUTH);
    }

    // --- MÉTODOS DE CONFIGURAÇÃO DE LAYOUT ---

    /**
     * Prepara a tela para exibir informações de ALUNO (Turma Atual)
     */
    public void configurarLayoutAluno() {
        painelAcademico.removeAll();
        painelAcademico.setLayout(new BorderLayout(5, 5));

        // 1. Topo: Nome da Turma
        JPanel pTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTurmaAtual = new JLabel("Minha Turma: Buscando...");
        lblTurmaAtual.setFont(new Font("Arial", Font.BOLD, 16));
        lblTurmaAtual.setForeground(new Color(0, 100, 0));
        pTopo.add(lblTurmaAtual);

        painelAcademico.add(pTopo, BorderLayout.NORTH);

        // 2. Centro: Tabela de Disciplinas
        String[] colunas = {"ID_Disc", "Disciplina", "Carga Horária"};
        modelDisciplinasAluno = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaDisciplinasAluno = new JTable(modelDisciplinasAluno);
        tabelaDisciplinasAluno.setRowHeight(25);

        // Esconde a coluna ID
        tabelaDisciplinasAluno.getColumnModel().getColumn(0).setMinWidth(0);
        tabelaDisciplinasAluno.getColumnModel().getColumn(0).setMaxWidth(0);

        painelAcademico.add(new JScrollPane(tabelaDisciplinasAluno), BorderLayout.CENTER);

        // 3. Sul: Botão de Ação
        JPanel pSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnVerConteudo = new JButton("📖 Ver Conteúdo Programático (RF027)");
        pSul.add(btnVerConteudo);

        painelAcademico.add(pSul, BorderLayout.SOUTH);

        painelAcademico.revalidate();
        painelAcademico.repaint();
    }
    /**
     * Prepara a tela para exibir informações de PROFESSOR (Tabela de Aulas)
     */
    public void configurarLayoutProfessor() {
        painelAcademico.removeAll();

        String[] colunas = {"Turma", "Disciplina", "Turno"};
        modelTurmasProf = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaTurmasProf = new JTable(modelTurmasProf);
        tabelaTurmasProf.setFillsViewportHeight(true);

        painelAcademico.add(new JScrollPane(tabelaTurmasProf), BorderLayout.CENTER);

        painelAcademico.revalidate();
        painelAcademico.repaint();
    }

    /**
     * Prepara a tela para ADMIN (Apenas mensagem)
     */
    public void configurarLayoutAdmin() {
        painelAcademico.removeAll();
        JLabel lbl = new JLabel("Administradores têm acesso total pelo Dashboard.", SwingConstants.CENTER);
        painelAcademico.add(lbl, BorderLayout.CENTER);
        painelAcademico.revalidate();
    }

    public JTable getTabelaDisciplinasAluno() { return tabelaDisciplinasAluno; }
    public DefaultTableModel getModelDisciplinasAluno() { return modelDisciplinasAluno; }
    public JButton getBtnVerConteudo() { return btnVerConteudo; }
    // --- SETTERS ---
    public void setDadosPessoais(String tipo, String nome, String cpf, String email, String tel) {
        lblTipoUsuario.setText("Perfil: " + tipo);
        lblNome.setText("Nome: " + nome);
        lblCpf.setText("CPF: " + cpf);
        lblEmail.setText("Email: " + email);
        lblTelefone.setText("Telefone: " + tel);
    }

    public void setTurmaAtualAluno(String turma) {
        if(lblTurmaAtual != null) lblTurmaAtual.setText("Turma Atual: " + turma);
    }

    public DefaultTableModel getModelProfessor() {
        return modelTurmasProf;
    }

    public JButton getBtnSair() { return btnSair; }
}