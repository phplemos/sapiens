package controllers;

import enums.TipoPerfilUsuario;
import models.Usuario;
import repositories.UsuarioRepository;
import views.DashboardAdminView;
import views.DashboardAlunoView;
import views.LoginView;

import javax.swing.*;
import java.util.Optional;

public class LoginController {
    private final UsuarioRepository usuarioRepo;

    private final LoginView view;
    private final DashboardAdminView dashboardAdminView;
    private final DashboardAlunoView dashboardAlunoView;



    public LoginController() {
        this.view = new LoginView();
        this.usuarioRepo = new UsuarioRepository();
        this.dashboardAdminView = new DashboardAdminView();
        this.dashboardAlunoView = new DashboardAlunoView();

    }
    public void start() {
        abrirTelaLogin();
    }

    private void abrirTelaLogin() {
        view.getBtnEntrar().addActionListener(e -> {
            String login = view.getLogin();
            String senha = view.getSenha();

            Optional<Usuario> usuarioOpt = usuarioRepo.autenticar(login, senha);

            if (usuarioOpt.isPresent()) {
                if(usuarioOpt.get().getTipoPerfil().equals(TipoPerfilUsuario.ADMIN)){
                    new DashboardAdminController(dashboardAdminView, usuarioOpt.get());
                    dashboardAdminView.setVisible(true);
                    view.dispose();
                }
                if(usuarioOpt.get().getTipoPerfil().equals(TipoPerfilUsuario.ALUNO)){
                    new DashboardAlunoController(dashboardAlunoView, usuarioOpt.get());
                    dashboardAlunoView.setVisible(true);
                    view.dispose();
                }
                if(usuarioOpt.get().getTipoPerfil().equals(TipoPerfilUsuario.PROFESSOR)){
                   System.out.println("Usuario professor!");
                }

            } else {
                JOptionPane.showMessageDialog(view, "Login ou Senha inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        view.getBtnSair().addActionListener(e -> System.exit(0));

        view.setVisible(true);
    }
}
