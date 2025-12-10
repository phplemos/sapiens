package controllers;

import models.Usuario;
import repositories.UsuarioRepository;
import views.*;

import javax.swing.*;
import java.util.List;

public class DashboardResponsavelController {

    private final UsuarioRepository usuarioRepo;
    private final DashboardResponsavelView dashboardView;

    private final Usuario usuario; // Este é o objeto do Responsável logado

    public DashboardResponsavelController(DashboardResponsavelView view, Usuario usuario) {
        this.usuarioRepo = new UsuarioRepository();
        this.dashboardView = view;
        this.usuario = usuario;

        open();
    }

    private void open() {
        // Define o nome no topo
        dashboardView.setUsuarioLogado(usuarioRepo.getNomeUsuario(usuario));

        // 1. Boletim dos Filhos (RF044)
        dashboardView.btnBoletimFilhos.addActionListener(e -> {
            SelecaoFilhoView selecaoView = new SelecaoFilhoView(dashboardView);
            new SelecaoFilhoController(selecaoView, usuario.getPessoaId());
            selecaoView.setVisible(true);
        });

        // 2. Financeiro (RF033, RF034, RF029)
        dashboardView.btnFinanceiro.addActionListener(e -> {
            JOptionPane.showMessageDialog(dashboardView,
                    "Acesso ao Módulo Financeiro (Mensalidades e Boletos).",
                    "Financeiro", JOptionPane.INFORMATION_MESSAGE);
        });

        // 3. Notificações (RF011)
        dashboardView.btnNotificacao.addActionListener(e -> {
            ComunicadoView comunicadoView = new ComunicadoView();
            new ComunicadoController(comunicadoView, usuario);
            comunicadoView.setVisible(true);
        });

        // 4. Perfil (RF002 - Edição de dados)
        dashboardView.btnPerfil.addActionListener(e -> {
            UsuarioPerfilView usuarioPerfilView = new UsuarioPerfilView();
            new UsuarioPerfilController(usuarioPerfilView, usuario);
            usuarioPerfilView.setVisible(true);
        });

        // 5. Sair
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