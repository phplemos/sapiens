package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardResponsavelView extends JFrame {

    // Botões específicos para o Responsável
    public JButton btnFinanceiro = new JButton("💰 Financeiro");
    public JButton btnBoletimFilhos = new JButton("📊 Boletim dos Filhos");
    public JButton btnNotificacao = new JButton("🔔 Notificações");
    public JButton btnPerfil = new JButton("👤 Meu Perfil");
    public JButton btnSair = new JButton("❌ Sair");

    private final JLabel lblUsuarioLogado;

    public DashboardResponsavelView() {
        setTitle("Sapiens - Área do Responsável");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Topo (Cabeçalho) ---
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(60, 60, 60)); // Mesmo tom do Aluno para consistência
        painelTopo.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Portal do Responsável");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));

        lblUsuarioLogado = new JLabel("Responsável: ---");
        lblUsuarioLogado.setForeground(Color.LIGHT_GRAY);

        painelTopo.add(lblTitulo, BorderLayout.WEST);
        painelTopo.add(lblUsuarioLogado, BorderLayout.EAST);
        add(painelTopo, BorderLayout.NORTH);

        // --- Centro (Grid de Botões) ---
        // Ajustei para 2 linhas e 3 colunas para acomodar os botões centralizados
        JPanel painelGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        painelGrid.setBorder(new EmptyBorder(40, 40, 40, 40));

        configurarBotao(btnFinanceiro);
        configurarBotao(btnBoletimFilhos);
        configurarBotao(btnNotificacao);
        configurarBotao(btnPerfil);
        configurarBotao(btnSair);

        // Adicionando na ordem lógica
        painelGrid.add(btnBoletimFilhos); // Foco acadêmico
        painelGrid.add(btnFinanceiro);    // Foco financeiro
        painelGrid.add(btnNotificacao);   // Comunicação
        painelGrid.add(btnPerfil);        // Dados cadastrais
        painelGrid.add(btnSair);          // Logout

        add(painelGrid, BorderLayout.CENTER);
    }

    private void configurarBotao(JButton btn) {
        btn.setFont(new Font("Arial", Font.PLAIN, 18)); // Fonte levemente maior para destaque
        btn.setFocusPainted(false);
        btn.setBackground(new Color(245, 245, 245));
    }

    public void setUsuarioLogado(String nome) {
        lblUsuarioLogado.setText("Olá, " + nome);
    }
}