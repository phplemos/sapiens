package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardAlunoView extends JFrame {

    public JButton btnHistorico = new JButton("👨‍🎓 Histórico Escolar");
    public JButton btnBoletim = new JButton("Boletim");
    public JButton btnNotificacao = new JButton("Notificacoes");
    public JButton btnPerfil = new JButton("Perfil");
    public JButton btnSair = new JButton("❌ Sair");

    private final JLabel lblUsuarioLogado;

    public DashboardAlunoView() {
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

        lblUsuarioLogado = new JLabel("Aluno: ---");
        lblUsuarioLogado.setForeground(Color.LIGHT_GRAY);

        painelTopo.add(lblTitulo, BorderLayout.WEST);
        painelTopo.add(lblUsuarioLogado, BorderLayout.EAST);
        add(painelTopo, BorderLayout.NORTH);

        // Centro (Grid de Botões)
        JPanel painelGrid = new JPanel(new GridLayout(2, 4, 20, 20));
        painelGrid.setBorder(new EmptyBorder(30, 30, 30, 30));

        configurarBotao(btnHistorico);
        configurarBotao(btnBoletim);
        configurarBotao(btnPerfil);
        configurarBotao(btnSair);
        configurarBotao(btnNotificacao);

        painelGrid.add(btnHistorico);
        painelGrid.add(btnBoletim);
        painelGrid.add(btnNotificacao);
        painelGrid.add(btnPerfil);
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