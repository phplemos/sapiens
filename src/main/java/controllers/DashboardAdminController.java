package controllers;

import models.PeriodoLetivo;
import models.Usuario;
import repositories.UsuarioRepository;
import views.*;

import javax.swing.*;

public class DashboardAdminController {
    private final UsuarioRepository usuarioRepo;
    private final DashboardAdminView dashboardView;
    private final Usuario usuario;

    public DashboardAdminController(DashboardAdminView view, Usuario usuario) {
        this.usuarioRepo = new UsuarioRepository();

        this.dashboardView = view;
        this.usuario = usuario;


        open();
    }

    private void open() {
        dashboardView.setUsuarioLogado(usuarioRepo.getNomeUsuario(usuario));

        // 1. Alunos
        dashboardView.btnAlunos.addActionListener(e -> {
            AlunoListView alunoListView = new AlunoListView(dashboardView);
            new AlunoController(alunoListView);
            alunoListView.setVisible(true);
        });

        // 1. Responsaveis
        dashboardView.btnResponsavel.addActionListener(e -> {
            ResponsavelListView responsavelListView = new ResponsavelListView(dashboardView);
            new ResponsavelController(responsavelListView);
            responsavelListView.setVisible(true);
        });

        // 2. Professores
        dashboardView.btnProfessores.addActionListener(e -> {
            ProfessorListView professorListView = new ProfessorListView(dashboardView);
            new ProfessorController(professorListView);
            professorListView.setVisible(true);
        });

        // 3. Turmas
        dashboardView.btnTurmas.addActionListener(e -> {
            TurmaListView turmaListView = new TurmaListView(dashboardView);
            new TurmaController(turmaListView);
            turmaListView.setVisible(true);
        });

        // 3. Disciplinas
        dashboardView.btnDisciplinas.addActionListener(e -> {
            DisciplinaListView disciplinaListView = new DisciplinaListView(dashboardView);
            new DisciplinaController(disciplinaListView);
            disciplinaListView.setVisible(true);
        });

        // 4. Matrículas
        dashboardView.btnMatriculas.addActionListener(e -> {
            MatriculaListView matriculaListView = new MatriculaListView(dashboardView);
            new MatriculaController(matriculaListView);
            matriculaListView.setVisible(true);
        });

        // 5. Notas
        dashboardView.btnNotas.addActionListener(e -> {
            NotaLancamentoView notaLancamentoView = new NotaLancamentoView(dashboardView);
            new GestaoNotasAdminController(notaLancamentoView);
            notaLancamentoView.setVisible(true);
        });

        // 6. Financeiro
        dashboardView.btnFinanceiro.addActionListener(e -> {
            ModuloFinanceiroView moduloFinanceiroView = new ModuloFinanceiroView(dashboardView);
            moduloFinanceiroView.setVisible(true);
        });

        dashboardView.btnNotificacao.addActionListener(e -> {
            ComunicadoView comunicadoView = new ComunicadoView(dashboardView);
            new ComunicadoController(comunicadoView, usuario); // Passa o usuário logado!
            comunicadoView.setVisible(true);
        });

        // 7. Configurações (Sub-menu)
        dashboardView.btnConfig.addActionListener(e -> {
            String[] options = {"Anos Escolares", "Séries", "Períodos Letivos","Parâmetros Gerais"};
            int choice = JOptionPane.showOptionDialog(dashboardView, "Selecione uma configuração:", "Configurações",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (choice == 0) {
                AnoEscolarListView anoEscolarListView = new AnoEscolarListView(dashboardView);
                new AnoEscolarController(anoEscolarListView);
                anoEscolarListView.setVisible(true);
            } else if (choice == 1) {
                SerieListView serieListView = new SerieListView(dashboardView);
                new SerieController(serieListView);
                serieListView.setVisible(true);
            } else if (choice == 2) {
                PeriodoLetivoListView  periodoLetivoListView = new PeriodoLetivoListView(dashboardView);
                new PeriodoLetivoController(periodoLetivoListView);
                periodoLetivoListView.setVisible(true);
            } else if (choice == 3) {
                ParametrosEscolaView paramView = new ParametrosEscolaView(dashboardView);
                new ParametrosEscolaController(paramView);
                paramView.setVisible(true);
            }
        });
        dashboardView.btnPerfil.addActionListener(e -> {
            UsuarioPerfilView usuarioPerfilView = new UsuarioPerfilView(dashboardView);
            new UsuarioPerfilController(usuarioPerfilView, usuario);
            usuarioPerfilView.setVisible(true);
        });

        // 8. Sair
        dashboardView.btnSair.addActionListener(e -> {
            dashboardView.dispose();
            new LoginController().start();
        });
        dashboardView.setVisible(true);
    }
}
