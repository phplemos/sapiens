package controllers;

import models.Usuario;
import repositories.UsuarioRepository;
import views.*;

import javax.swing.*;
import java.util.List;

public class DashboardResponsavelController {

    private final UsuarioRepository usuarioRepo;
    private final DashboardResponsavelView dashboardView;

    private final Usuario usuario;

    public DashboardResponsavelController(DashboardResponsavelView view, Usuario usuario) {
        this.usuarioRepo = new UsuarioRepository();
        this.dashboardView = view;
        this.usuario = usuario;

        open();
    }

    private void open() {
        dashboardView.setUsuarioLogado(usuarioRepo.getNomeUsuario(usuario));

        dashboardView.btnBoletimFilhos.addActionListener(e -> {
            SelecaoFilhoView selecaoView = new SelecaoFilhoView(dashboardView);
            new SelecaoFilhoController(selecaoView, usuario.getPessoaId());
            selecaoView.setVisible(true);
        });

        dashboardView.btnFinanceiro.addActionListener(e -> {
            FinanceiroResponsavelView financeiroResponsavelView = new FinanceiroResponsavelView(dashboardView);
            new FinanceiroResponsavelController(financeiroResponsavelView, usuario.getPessoaId());
            financeiroResponsavelView.setVisible(true);
        });

        dashboardView.btnNotificacao.addActionListener(e -> {
            ComunicadoView comunicadoView = new ComunicadoView(dashboardView);
            new ComunicadoController(comunicadoView, usuario);
            comunicadoView.setVisible(true);
        });

        dashboardView.btnPerfil.addActionListener(e -> {
            UsuarioPerfilView usuarioPerfilView = new UsuarioPerfilView(dashboardView);
            new UsuarioPerfilController(usuarioPerfilView, usuario);
            usuarioPerfilView.setVisible(true);
        });

        dashboardView.btnSair.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dashboardView,
                    "Deseja realmente sair?", "Logout", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                dashboardView.dispose();
                new LoginController().start();
            }
        });

        dashboardView.setVisible(true);
    }
}