package controllers;

import models.*;
import repositories.*;
import views.components.ComboItem;
import views.DashboardProfessorView;
import views.DiarioClasseView;
import views.AlunoHistoricoView;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public class DashboardProfessorController extends JDialog {

    private final DashboardProfessorView view;
    private final Usuario usuarioLogado;

    // Repositórios
    private final ProfessorRepository profRepo = new ProfessorRepository();
    private final PessoaRepository pessoaRepo = new PessoaRepository();
    private final TurmaDisciplinaRepository tdRepo = new TurmaDisciplinaRepository();
    private final TurmaRepository turmaRepo = new TurmaRepository();
    private final DisciplinaRepository disciplinaRepo = new DisciplinaRepository();
    private final MatriculaRepository matriculaRepo = new MatriculaRepository();
    private final MatriculaDisciplinaRepository matDiscRepo = new MatriculaDisciplinaRepository();
    private final AlunoRepository alunoRepo = new AlunoRepository();

    public DashboardProfessorController(DashboardProfessorView view, Usuario usuario) {
        this.view = view;
        this.usuarioLogado = usuario;

        initController();
    }

    private void initController() {
        carregarDadosProfessor(); // RF018
        carregarMinhasTurmas();

        // Listeners
        view.getBtnCarregar().addActionListener(e -> carregarListaAlunos()); // RF021
        view.getBtnLancar().addActionListener(e -> abrirDiarioClasse());     // RF019
        view.getBtnHistorico().addActionListener(e -> abrirHistoricoAluno()); // RF020
        view.getBtnSair().addActionListener(e -> System.exit(0));
    }

    // --- RF018: Calcular Carga Horária Total ---
    private void carregarDadosProfessor() {
        int pessoaId = usuarioLogado.getPessoaId();
        Optional<Pessoa> pOpt = pessoaRepo.buscarPorId(pessoaId);

        if (pOpt.isPresent()) {
            // 1. Calcular Carga Horária
            // Busca todas as TurmaDisciplina onde este professor dá aula
            List<TurmaDisciplina> minhasAulas = tdRepo.listarTodos().stream()
                    .filter(td -> td.getProfessorPessoaId() == pessoaId)
                    .toList();

            int cargaTotal = 0;
            for (TurmaDisciplina td : minhasAulas) {
                Optional<Disciplina> d = disciplinaRepo.buscarPorId(td.getDisciplinaId());
                if (d.isPresent()) {
                    cargaTotal += d.get().getCargaHoraria();
                }
            }

            // 2. Atualizar View
            view.setDadosProfessor(pOpt.get().getNomeCompleto(), cargaTotal);
        }
    }

    private void carregarMinhasTurmas() {
        int pessoaId = usuarioLogado.getPessoaId();
        List<TurmaDisciplina> minhasAulas = tdRepo.listarTodos().stream()
                .filter(td -> td.getProfessorPessoaId() == pessoaId)
                .toList();

        view.adicionarTurmaCombo(new ComboItem(0, "Selecione..."));

        for (TurmaDisciplina td : minhasAulas) {
            String nomeTurma = "T-" + td.getTurmaId();
            String nomeDisc = "D-" + td.getDisciplinaId();

            Optional<Turma> t = turmaRepo.buscarPorId(td.getTurmaId());
            if (t.isPresent()) nomeTurma = t.get().getNome();

            Optional<Disciplina> d = disciplinaRepo.buscarPorId(td.getDisciplinaId());
            if (d.isPresent()) nomeDisc = d.get().getNome();

            // O ID do ComboItem será o ID da TURMA_DISCIPLINA (O vínculo específico)
            // Ex: "1º Ano A - Matemática"
            view.adicionarTurmaCombo(new ComboItem(td.getId(), nomeTurma + " - " + nomeDisc));
        }
    }

    private void carregarListaAlunos() {
        int tdId = view.getTurmaDisciplinaSelecionadaId();
        if (tdId == 0) return;

        view.getModelAlunos().setRowCount(0);

        // 1. Descobrir qual é a Turma e a Disciplina
        Optional<TurmaDisciplina> tdOpt = tdRepo.buscarPorId(tdId);
        if (tdOpt.isEmpty()) return;

        int turmaId = tdOpt.get().getTurmaId(); // A turma física

        // 2. Buscar matrículas dessa turma
        List<Matricula> matriculas = matriculaRepo.listarTodos().stream()
                .filter(m -> m.getTurmaId() == turmaId)
                .toList();

        for (Matricula m : matriculas) {
            // Verifica se o aluno está matriculado NESSA disciplina específica (Grade)
            // (Às vezes o aluno pode ter sido dispensado da matéria)
            boolean cursaMateria = matDiscRepo.buscarPorMatriculaId(m.getId()).stream()
                    .anyMatch(md -> md.getTurmaDisciplinaId() == tdId);

            if (cursaMateria) {
                // Pega nome do aluno
                String nomeAluno = "Desconhecido";
                Optional<Pessoa> p = pessoaRepo.buscarPorId(m.getAlunoPessoaId());
                if (p.isPresent()) nomeAluno = p.get().getNomeCompleto();

                view.getModelAlunos().addRow(new Object[]{
                        m.getAlunoPessoaId(), // ID Pessoa do Aluno (para o histórico)
                        nomeAluno,
                        m.getNumeroMatricula(),
                        m.getStatus()
                });
            }
        }
    }

    // --- RF019: Abrir Diário (Lógica em outro Controller) ---
    private void abrirDiarioClasse() {
        int tdId = view.getTurmaDisciplinaSelecionadaId();
        if (tdId == 0) {
            JOptionPane.showMessageDialog(view, "Selecione uma turma/disciplina acima.");
            return;
        }

        // Abre a janela do Diário
        DiarioClasseView diarioView = new DiarioClasseView();
        new DiarioClasseController(diarioView, tdId);
        diarioView.setVisible(true);
    }

    // --- RF020: Ver Histórico (Reutilizando código) ---
    private void abrirHistoricoAluno() {
        int row = view.getTabelaAlunos().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione um aluno na tabela.");
            return;
        }

        int alunoPessoaId = (int) view.getModelAlunos().getValueAt(row, 0);

        AlunoHistoricoView histView = new AlunoHistoricoView(view);
        // Modo Admin/Prof = false (Botão Fechar)
        new AlunoHistoricoController(histView, alunoPessoaId, false);
        histView.setVisible(true);
    }
}