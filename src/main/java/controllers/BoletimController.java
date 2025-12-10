package controllers;

import models.*;
import repositories.*;
import views.BoletimView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BoletimController {

    private final BoletimView view;
    private final int alunoPessoaId;

    private final ParametrosEscolaRepository parametrosRepo;
    private final AlunoRepository alunoRepo;
    private final PessoaRepository pessoaRepo; // Necessário para pegar o nome
    private final MatriculaRepository matriculaRepo;
    private final TurmaRepository turmaRepo;
    private final MatriculaDisciplinaRepository matDiscRepo;
    private final NotaRepository notaRepo;
    private final BoletimStatusRepository boletimStatusRepo;
    private final PeriodoLetivoRepository periodoRepo;
    private final TurmaDisciplinaRepository turmaDiscRepo;
    private final DisciplinaRepository disciplinaRepo;

    public BoletimController(BoletimView view, int alunoPessoaId) {
        this.view = view;
        this.alunoPessoaId = alunoPessoaId;

        this.parametrosRepo = new ParametrosEscolaRepository();
        this.alunoRepo = new AlunoRepository();
        this.pessoaRepo = new PessoaRepository();
        this.matriculaRepo = new MatriculaRepository();
        this.turmaRepo = new TurmaRepository();
        this.matDiscRepo = new MatriculaDisciplinaRepository();
        this.notaRepo = new NotaRepository();
        this.boletimStatusRepo = new BoletimStatusRepository();
        this.periodoRepo = new PeriodoLetivoRepository();
        this.turmaDiscRepo = new TurmaDisciplinaRepository();
        this.disciplinaRepo = new DisciplinaRepository();

        initController();
    }

    private void initController() {
        view.getBtnFechar().addActionListener(e -> view.dispose());
        carregarBoletim();
    }

    private void carregarBoletim() {
        Optional<Aluno> alunoOpt = alunoRepo.buscarPorPessoaId(alunoPessoaId);
        Optional<Pessoa> pessoaOpt = pessoaRepo.buscarPorId(alunoPessoaId);

        if (alunoOpt.isEmpty() || pessoaOpt.isEmpty()) {
            view.setInfo("Aluno não encontrado", "-");
            return;
        }

        Optional<Matricula> matriculaOpt = matriculaRepo.listarTodos().stream()
                .filter(m -> m.getAlunoPessoaId() == alunoPessoaId)
                .max(Comparator.comparingInt(Matricula::getId));

        if (matriculaOpt.isEmpty()) {
            view.setInfo(pessoaOpt.get().getNomeCompleto(), "Sem matrícula ativa");
            return;
        }

        Matricula matricula = matriculaOpt.get();
        Turma turma = turmaRepo.buscarPorId(matricula.getTurmaId()).orElse(new Turma());

        view.setInfo(pessoaOpt.get().getNomeCompleto(), turma.getNome());

        List<PeriodoLetivo> periodos = periodoRepo.buscarPorAnoEscolarId(turma.getAnoEscolarId());

        if (periodos.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhum período letivo configurado para este ano.");
            return;
        }

        List<String> nomesPeriodos = periodos.stream()
                .map(PeriodoLetivo::getNome)
                .collect(Collectors.toList());

        view.configurarTabela(nomesPeriodos);

        List<MatriculaDisciplina> disciplinasMatriculadas = matDiscRepo.buscarPorMatriculaId(matricula.getId());

        for (MatriculaDisciplina matriculaDisciplina : disciplinasMatriculadas) {
            String nomeDisciplina = "Desconhecida";
            Optional<TurmaDisciplina> turmaDisciplinaOpt = turmaDiscRepo.buscarPorId(matriculaDisciplina.getTurmaDisciplinaId());
            if (turmaDisciplinaOpt.isPresent()) {
                Optional<Disciplina> dOpt = disciplinaRepo.buscarPorId(turmaDisciplinaOpt.get().getDisciplinaId());
                if (dOpt.isPresent()) nomeDisciplina = dOpt.get().getNome();
            }

            Object[] linha = new Object[1 + periodos.size() + 2];

            linha[0] = nomeDisciplina;

            int colIndex = 1;

            for (PeriodoLetivo p : periodos) {
                boolean publicado = boletimStatusRepo.isPublicado(turma.getId(), p.getId());

                if (publicado) {
                    Optional<Nota> notaOpt = notaRepo.buscarNota(matriculaDisciplina.getId(), p.getId());
                    if (notaOpt.isPresent()) {
                        linha[colIndex] = notaOpt.get().getValorNota();
                    } else {
                        linha[colIndex] = "-";
                    }
                } else {
                    linha[colIndex] = "*";
                }
                colIndex++;
            }
            linha[colIndex] = matriculaDisciplina.getTotalFaltas();
            linha[colIndex + 1] = calcularSituacao(matriculaDisciplina, periodos);

            view.getTableModel().addRow(linha);
        }
    }

    private String calcularSituacao(MatriculaDisciplina matriculaDisciplina, List<PeriodoLetivo> periodos) {
        ParametrosEscola regras = parametrosRepo.buscarParametrosGerais();

        double MEDIA_APROVACAO = regras.getMediaAprovacao();
        double MEDIA_RECUPERACAO = regras.getMediaRecuperacao();
        int LIMITE_FALTAS = regras.getLimiteFaltas();

        if (matriculaDisciplina.getTotalFaltas() > LIMITE_FALTAS) {
            return "Reprovado (Faltas)";
        }

        double somaNotas = 0.0;
        int notasLancadas = 0;
        int totalPeriodos = periodos.size();

        for (PeriodoLetivo periodo : periodos) {
            Optional<Nota> notaOpt = notaRepo.buscarNota(matriculaDisciplina.getId(), periodo.getId());

            if (notaOpt.isPresent()) {
                somaNotas += notaOpt.get().getValorNota();
                notasLancadas++;
            }
        }

        if (notasLancadas < totalPeriodos) {
            if (notasLancadas > 0) {
                double mediaParcial = somaNotas / notasLancadas;
                return String.format("Cursando (Média atual: %.1f)", mediaParcial);
            }
            return "Cursando";
        }
        double mediaFinal = somaNotas / totalPeriodos;

        if (mediaFinal >= MEDIA_APROVACAO) {
            return "Aprovado";
        } else if (mediaFinal >= MEDIA_RECUPERACAO) {
            return "Recuperação";
        } else {
            return "Reprovado (Nota)";
        }
    }
}