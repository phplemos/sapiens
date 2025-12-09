package controllers;

import models.Usuario;
import repositories.UsuarioRepository;
import views.*;

import javax.swing.*;

public class DashboardAlunoController {
    private final UsuarioRepository usuarioRepo;
    private final DashboardAlunoView dashboardView;
    private final Usuario usuario;

    public DashboardAlunoController(DashboardAlunoView view, Usuario usuario) {
        this.usuarioRepo = new UsuarioRepository();
        this.dashboardView = view;
        this.usuario = usuario;
        open();
    }

    private void open() {
        dashboardView.setUsuarioLogado(usuarioRepo.getNomeUsuario(usuario));

        // 1. Alunos
        dashboardView.btnHistorico.addActionListener(e -> {
        });

        // 2. Professores
        dashboardView.btnBoletim.addActionListener(e -> {
        });

        // 3. Turmas
        dashboardView.btnPerfil.addActionListener(e -> {
        });

        // Notificações
        dashboardView.btnNotificacao.addActionListener(e -> {

        });

        // 8. Sair
        dashboardView.btnSair.addActionListener(e -> {
            dashboardView.dispose();
            new LoginController().start();
        });
        dashboardView.setVisible(true);
    }
}
