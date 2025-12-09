package controllers;

import models.Usuario;
import repositories.UsuarioRepository;
import views.*;

import javax.swing.*;

public class DashboardAlunoController {
    private final UsuarioRepository usuarioRepo;
    private final DashboardAlunoView dashboardView;
    private final UsuarioPerfilView usuarioPerfilView;
    private final ComunicadoView comunicadoView;
    private final AlunoHistoricoView alunoHistoricoView;
    private final Usuario usuario;

    public DashboardAlunoController(DashboardAlunoView view, Usuario usuario) {
        this.usuarioRepo = new UsuarioRepository();
        this.dashboardView = view;
        this.usuarioPerfilView = new UsuarioPerfilView();
        this.comunicadoView = new ComunicadoView();
        this.alunoHistoricoView = new AlunoHistoricoView(view);
        this.usuario = usuario;
        open();
    }

    private void open() {
        dashboardView.setUsuarioLogado(usuarioRepo.getNomeUsuario(usuario));

        // 1. Histórico
        dashboardView.btnHistorico.addActionListener(e -> {
            new AlunoHistoricoController(this.alunoHistoricoView, usuario.getPessoaId(), false);
            this.alunoHistoricoView.setVisible(true);
        });

        // 2. Boletim
        dashboardView.btnBoletim.addActionListener(e -> {
        });

        // 3. Perfil
        dashboardView.btnPerfil.addActionListener(e -> {
            new UsuarioPerfilController(this.usuarioPerfilView, usuario);
            usuarioPerfilView.setVisible(true);
        });

        // Notificações
        dashboardView.btnNotificacao.addActionListener(e -> {
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
