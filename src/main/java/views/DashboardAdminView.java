package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardAdminView extends JFrame {

    public JButton btnAlunos = new JButton("👨‍🎓 Alunos");
    public JButton btnProfessores = new JButton("🧑‍🏫 Professores");
    public JButton btnTurmas = new JButton("🏫 Turmas");
    public JButton btnDisciplinas = new JButton("📚 Disciplinas");
    public JButton btnMatriculas = new JButton("📝 Matrículas");
    public JButton btnNotas = new JButton("📊 Notas");
    public JButton btnNotificacao = new JButton("Notificacoes");
    public JButton btnFinanceiro = new JButton("💰 Financeiro");
    public JButton btnResponsavel = new JButton("Responsaveis");
    public JButton btnConfig = new JButton("⚙️ Configurações");
    public JButton btnPerfil = new JButton("Perfil");
    public JButton btnSair = new JButton("❌ Sair");

    private final JLabel lblUsuarioLogado;

    public DashboardAdminView() {
        setTitle("Sapiens - Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Topo
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(60, 60, 60));
        painelTopo.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Sistema de Gestão Escolar");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));

        lblUsuarioLogado = new JLabel("Usuário: ---");
        lblUsuarioLogado.setForeground(Color.LIGHT_GRAY);

        painelTopo.add(lblTitulo, BorderLayout.WEST);
        painelTopo.add(lblUsuarioLogado, BorderLayout.EAST);
        add(painelTopo, BorderLayout.NORTH);

        JPanel painelGrid = new JPanel(new GridLayout(2, 4, 20, 20));
        painelGrid.setBorder(new EmptyBorder(30, 30, 30, 30));

        configurarBotao(btnAlunos);
        configurarBotao(btnProfessores);
        configurarBotao(btnTurmas);
        configurarBotao(btnDisciplinas);
        configurarBotao(btnMatriculas);
        configurarBotao(btnNotas);
        configurarBotao(btnFinanceiro);
        configurarBotao(btnConfig);
        configurarBotao(btnPerfil);
        configurarBotao(btnSair);
        configurarBotao(btnResponsavel);
        configurarBotao(btnNotas);

        painelGrid.add(btnAlunos);
        painelGrid.add(btnResponsavel);
        painelGrid.add(btnProfessores);
        painelGrid.add(btnTurmas);
        painelGrid.add(btnDisciplinas);
        painelGrid.add(btnMatriculas);
        painelGrid.add(btnNotas);
        painelGrid.add(btnFinanceiro);
        painelGrid.add(btnConfig);
        painelGrid.add(btnPerfil);
        painelGrid.add(btnNotificacao);
        painelGrid.add(btnSair);

        add(painelGrid, BorderLayout.CENTER);
    }

    private void configurarBotao(JButton btn) {
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setFocusPainted(false);
    }

    public void setUsuarioLogado(String nome) {
        lblUsuarioLogado.setText("Usuário: " + nome);
    }
}