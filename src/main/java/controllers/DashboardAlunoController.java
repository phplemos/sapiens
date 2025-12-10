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

        // 1. Histórico
        dashboardView.btnHistorico.addActionListener(e -> {
            AlunoHistoricoView alunoHistoricoView = new AlunoHistoricoView(dashboardView);
            new AlunoHistoricoController(alunoHistoricoView, usuario.getPessoaId(), false);
            alunoHistoricoView.setVisible(true);
        });

        // 2. Boletim
        dashboardView.btnBoletim.addActionListener(e -> {
            BoletimView boletimView = new BoletimView(dashboardView);
            new BoletimController(boletimView, usuario.getPessoaId());
            boletimView.setVisible(true);
        });

        // 3. Perfil
        dashboardView.btnPerfil.addActionListener(e -> {
            UsuarioPerfilView usuarioPerfilView = new UsuarioPerfilView();
            new UsuarioPerfilController(usuarioPerfilView, usuario);
            usuarioPerfilView.setVisible(true);
        });

        // Notificações
        dashboardView.btnNotificacao.addActionListener(e -> {
            ComunicadoView comunicadoView = new ComunicadoView();
            new ComunicadoController(comunicadoView, usuario); // Passa o usuário logado!
            comunicadoView.setVisible(true);
        });

        // 8. Sair
        dashboardView.btnSair.addActionListener(e -> {
            dashboardView.dispose();
            new LoginController().start();
        });
        dashboardView.setVisible(true);
    }
}
