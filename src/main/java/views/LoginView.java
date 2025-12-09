package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnSair;

    public LoginView() {
        setTitle("Login - Sistema Escolar");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Cabeçalho
        JLabel lblTitulo = new JLabel("Acesso ao Sistema", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Campos
        JPanel painelCampos = new JPanel(new GridLayout(4, 1, 5, 5));
        painelCampos.setBorder(new EmptyBorder(0, 20, 0, 20));

        painelCampos.add(new JLabel("Usuário:"));
        txtLogin = new JTextField();
        painelCampos.add(txtLogin);

        painelCampos.add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        painelCampos.add(txtSenha);

        add(painelCampos, BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnEntrar = new JButton("Entrar");
        btnSair = new JButton("Sair");

        painelBotoes.add(btnEntrar);
        painelBotoes.add(btnSair);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    public String getLogin() { return txtLogin.getText(); }
    public String getSenha() { return new String(txtSenha.getPassword()); }
    public JButton getBtnEntrar() { return btnEntrar; }
    public JButton getBtnSair() { return btnSair; }
}