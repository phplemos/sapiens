package views;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UsuarioPerfilView extends JDialog {

    private final JLabel lblNome, lblCpf, lblEmail, lblTelefone;
    private final JLabel lblTipoUsuario;

    private final JPanel painelAcademico;

    private JLabel lblTurmaAtual;

    private JTable tabelaTurmasProf;
    private DefaultTableModel modelTurmasProf;

    private JTable tabelaDisciplinasAluno;
    private DefaultTableModel modelDisciplinasAluno;
    private JButton btnVerConteudo;

    private JTable tabelaFilhosResponsavel;
    private DefaultTableModel modelFilhosResponsavel;

    private final JButton btnSair;
    private JButton btnAlterarSenha;

    public UsuarioPerfilView(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Meu Perfil - Sapiens");
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel painelDados = new JPanel(new GridLayout(6, 1, 5, 5));
        painelDados.setBorder(new TitledBorder("Meus Dados"));
        painelDados.setBackground(new Color(245, 245, 250));

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
        JPanel pSenha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pSenha.setOpaque(false);
        btnAlterarSenha = new JButton("🔒 Alterar Minha Senha");
        pSenha.add(btnAlterarSenha);
        painelDados.add(pSenha);

        add(painelDados, BorderLayout.NORTH);

        painelAcademico = new JPanel(new BorderLayout());
        painelAcademico.setBorder(new TitledBorder("Informações do Perfil"));
        add(painelAcademico, BorderLayout.CENTER);


        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSair = new JButton("Fechar Janela");
        painelSul.add(btnSair);
        add(painelSul, BorderLayout.SOUTH);
    }
    public void configurarLayoutResponsavel() {
        painelAcademico.removeAll();
        painelAcademico.setBorder(new TitledBorder("Meus Dependentes (Filhos)"));

        String[] colunas = {"Nome do Aluno", "Matrícula", "Turma Atual"};
        modelFilhosResponsavel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaFilhosResponsavel = new JTable(modelFilhosResponsavel);
        tabelaFilhosResponsavel.setRowHeight(25);

        painelAcademico.add(new JScrollPane(tabelaFilhosResponsavel), BorderLayout.CENTER);

        painelAcademico.revalidate();
        painelAcademico.repaint();
    }
    public void configurarLayoutAluno() {
        painelAcademico.removeAll();
        painelAcademico.setLayout(new BorderLayout(5, 5));

        JPanel pTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTurmaAtual = new JLabel("Minha Turma: Buscando...");
        lblTurmaAtual.setFont(new Font("Arial", Font.BOLD, 16));
        lblTurmaAtual.setForeground(new Color(0, 100, 0));
        pTopo.add(lblTurmaAtual);

        painelAcademico.add(pTopo, BorderLayout.NORTH);

        String[] colunas = {"ID_Disc", "Disciplina", "Carga Horária"};
        modelDisciplinasAluno = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaDisciplinasAluno = new JTable(modelDisciplinasAluno);
        tabelaDisciplinasAluno.setRowHeight(25);

        tabelaDisciplinasAluno.getColumnModel().getColumn(0).setMinWidth(0);
        tabelaDisciplinasAluno.getColumnModel().getColumn(0).setMaxWidth(0);

        painelAcademico.add(new JScrollPane(tabelaDisciplinasAluno), BorderLayout.CENTER);

        JPanel pSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnVerConteudo = new JButton("📖 Ver Conteúdo Programático (RF027)");
        pSul.add(btnVerConteudo);

        painelAcademico.add(pSul, BorderLayout.SOUTH);

        painelAcademico.revalidate();
        painelAcademico.repaint();
    }

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

    public void configurarLayoutAdmin() {
        painelAcademico.removeAll();
        JLabel lbl = new JLabel("Administradores têm acesso total pelo Dashboard.", SwingConstants.CENTER);
        painelAcademico.add(lbl, BorderLayout.CENTER);
        painelAcademico.revalidate();
    }
    public DefaultTableModel getModelFilhosResponsavel() { return modelFilhosResponsavel; }
    public JButton getBtnAlterarSenha() { return btnAlterarSenha; }

    public JTable getTabelaDisciplinasAluno() { return tabelaDisciplinasAluno; }
    public DefaultTableModel getModelDisciplinasAluno() { return modelDisciplinasAluno; }
    public JButton getBtnVerConteudo() { return btnVerConteudo; }

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