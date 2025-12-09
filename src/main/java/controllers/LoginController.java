package controllers;

import enums.TipoPerfilUsuario;
import models.Usuario;
import repositories.UsuarioRepository;
import views.*;

import javax.swing.*;
import java.util.Optional;

public class LoginController {
    private final UsuarioRepository usuarioRepo;

    private final LoginView view;
    private final DashboardAdminView dashboardAdminView;
    private final DashboardAlunoView dashboardAlunoView;
    private final DashboardProfessorView dashboardProfessorView;
    private final DashboardResponsavelView  dashboardResponsavelView;

    public LoginController() {
        this.view = new LoginView();
        this.usuarioRepo = new UsuarioRepository();
        this.dashboardAdminView = new DashboardAdminView();
        this.dashboardAlunoView = new DashboardAlunoView();
        this.dashboardProfessorView = new DashboardProfessorView();
        this.dashboardResponsavelView = new DashboardResponsavelView();
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
                switch (usuarioOpt.get().getTipoPerfil()) {
                    case ADMIN:{
                        new DashboardAdminController(dashboardAdminView, usuarioOpt.get());
                        dashboardAdminView.setVisible(true);
                        view.dispose();
                        break;
                    }
                    case PROFESSOR: {
                        new DashboardProfessorController(dashboardProfessorView, usuarioOpt.get());
                        dashboardProfessorView.setVisible(true);
                        view.dispose();
                        break;
                    }
                    case ALUNO:{
                        new DashboardAlunoController(dashboardAlunoView, usuarioOpt.get());
                        dashboardAlunoView.setVisible(true);
                        view.dispose();
                        break;
                    }
                    case RESPONSAVEL:{
                        new DashboardResponsavelController(dashboardResponsavelView, usuarioOpt.get());
                        dashboardResponsavelView.setVisible(true);
                        view.dispose();
                        break;
                    }

                }
            } else {
                JOptionPane.showMessageDialog(view, "Login ou Senha inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        view.getBtnSair().addActionListener(e -> System.exit(0));

        view.setVisible(true);
    }
}
