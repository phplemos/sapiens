package controllers;

import models.Usuario;
import repositories.UsuarioRepository;
import views.*;

import javax.swing.*;

public class DashboardAdminController {
    private final UsuarioRepository usuarioRepo;
    private final DashboardAdminView dashboardView;
    private final AlunoListView alunoListView;
    private final ResponsavelListView responsavelListView;
    private final ProfessorListView professorListView;
    private final TurmaListView turmaListView;
    private final DisciplinaListView disciplinaListView;
    private final MatriculaListView matriculaListView;
    private final NotaLancamentoView notaLancamentoView;
    private final FinanceiroView financeiroView;
    private final AnoEscolarListView AnoEscolarListView;
    private final SerieListView serieListView;
    private final PeriodoLetivoListView periodoLetivoListView;
    private final UsuarioPerfilView usuarioPerfilView;
    private final Usuario usuario;

    public DashboardAdminController(DashboardAdminView view, Usuario usuario) {
        this.usuarioRepo = new UsuarioRepository();

        this.dashboardView = view;
        this.usuario = usuario;

        this.alunoListView = new AlunoListView();
        this.responsavelListView = new ResponsavelListView();
        this.professorListView = new ProfessorListView();
        this.turmaListView = new TurmaListView();
        this.disciplinaListView = new DisciplinaListView();
        this.matriculaListView = new MatriculaListView();
        this.notaLancamentoView = new NotaLancamentoView();
        this.financeiroView = new FinanceiroView();
        this.AnoEscolarListView = new AnoEscolarListView();
        this.serieListView = new SerieListView();
        this.periodoLetivoListView = new PeriodoLetivoListView();
        this.usuarioPerfilView = new UsuarioPerfilView();

        new AlunoController(this.alunoListView);
        new ProfessorController(this.professorListView);
        new TurmaController(this.turmaListView);
        new DisciplinaController(this.disciplinaListView);
        new MatriculaController(this.matriculaListView);
        new GestaoNotasAdminController(this.notaLancamentoView);
        new FinanceiroController(this.financeiroView);
        new AnoEscolarController(AnoEscolarListView);
        new SerieController(this.serieListView);
        new PeriodoLetivoController(periodoLetivoListView);
        new ResponsavelController(this.responsavelListView);
        open();
    }

    private void open() {
        dashboardView.setUsuarioLogado(usuarioRepo.getNomeUsuario(usuario));

        // 1. Alunos
        dashboardView.btnAlunos.addActionListener(e -> {
            alunoListView.setVisible(true);
        });
        // 1. Responsaveis
        dashboardView.btnResponsavel.addActionListener(e -> {
            responsavelListView.setVisible(true);
        });

        // 2. Professores
        dashboardView.btnProfessores.addActionListener(e -> {
            professorListView.setVisible(true);
        });

        // 3. Turmas
        dashboardView.btnTurmas.addActionListener(e -> {
            turmaListView.setVisible(true);
        });

        // 3. Disciplinas
        dashboardView.btnDisciplinas.addActionListener(e -> {
            disciplinaListView.setVisible(true);
        });

        // 4. Matrículas
        dashboardView.btnMatriculas.addActionListener(e -> {
            matriculaListView.setVisible(true);
        });

        // 5. Notas
        dashboardView.btnNotas.addActionListener(e -> {
            notaLancamentoView.setVisible(true);
        });

        // 6. Financeiro
        dashboardView.btnFinanceiro.addActionListener(e -> {
            financeiroView.setVisible(true);
        });

        // 7. Configurações (Sub-menu)
        dashboardView.btnConfig.addActionListener(e -> {
            String[] options = {"Anos Escolares", "Séries", "Períodos Letivos","Parâmetros Gerais"};
            int choice = JOptionPane.showOptionDialog(dashboardView, "Selecione uma configuração:", "Configurações",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (choice == 0) {
                AnoEscolarListView.setVisible(true);
            } else if (choice == 1) {
                serieListView.setVisible(true);
            } else if (choice == 2) {
                periodoLetivoListView.setVisible(true);
            } else if (choice == 3) {
                ParametrosEscolaView paramView = new ParametrosEscolaView(dashboardView);
                new ParametrosEscolaController(paramView);
            }
        });
        dashboardView.btnPerfil.addActionListener(e -> {
            new UsuarioPerfilController(this.usuarioPerfilView, usuario);
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
