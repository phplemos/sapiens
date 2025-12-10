package controllers;

import models.*;
import repositories.*;
import views.AlunoHistoricoView;

import java.util.List;
import java.util.Optional;

public class AlunoHistoricoController {

    private final AlunoHistoricoView view;

    private final AlunoRepository alunoRepo;
    private final PessoaRepository pessoaRepo;
    private final EnderecoRepository enderecoRepo;
    private final MatriculaRepository matriculaRepo;
    private final TurmaRepository turmaRepo;
    private final AnoEscolarRepository anoRepo;
    private final MatriculaDisciplinaRepository matDiscRepo;
    private final DisciplinaRepository disciplinaRepo;
    private final NotaRepository notaRepo;
    private final PeriodoLetivoRepository periodoRepo;
    private final BoletimStatusRepository  boletimRepo;

    public AlunoHistoricoController(AlunoHistoricoView view, int alunoPessoaId, boolean isAdmin) {
        this.view = view;

        this.alunoRepo = new AlunoRepository();
        this.pessoaRepo = new PessoaRepository();
        this.enderecoRepo = new EnderecoRepository();
        this.matriculaRepo = new MatriculaRepository();
        this.turmaRepo = new TurmaRepository();
        this.anoRepo = new AnoEscolarRepository();
        this.matDiscRepo = new MatriculaDisciplinaRepository();
        this.disciplinaRepo = new DisciplinaRepository();
        this.notaRepo = new NotaRepository();
        this.periodoRepo = new PeriodoLetivoRepository();
        this.boletimRepo = new BoletimStatusRepository();
        carregarDados(alunoPessoaId);

        this.view.getBtnFechar().addActionListener(e -> {
            if (isAdmin) view.dispose();
        });
    }

    private void carregarDados(int alunoPessoaId) {
        Optional<Aluno> alunoOpt = alunoRepo.buscarPorPessoaId(alunoPessoaId);
        Optional<Pessoa> pessoaOpt = pessoaRepo.buscarPorId(alunoPessoaId);

        if (alunoOpt.isEmpty() || pessoaOpt.isEmpty()) return;

        Pessoa p = pessoaOpt.get();
        String enderecoStr = "Não informado";

        Optional<Endereco> endOpt = enderecoRepo.buscarPorId(p.getEnderecoId());
        if (endOpt.isPresent()) {
            Endereco e = endOpt.get();
            enderecoStr = e.getCidade() + " - " + e.getBairro() + ", " + e.getLogradouro();
        }

        view.setDadosPerfil(p.getNomeCompleto(), p.getCpf(), p.getEmailContato(), p.getTelefone(), enderecoStr);

        view.getTableModel().setRowCount(0);

        List<Matricula> matriculas = matriculaRepo.listarTodos().stream()
                .filter(m -> m.getAlunoPessoaId() == alunoPessoaId)
                .toList();

        for (Matricula mat : matriculas) {
            String nomeTurma = "N/A";
            String anoLetivo = "N/A";

            Optional<Turma> tOpt = turmaRepo.buscarPorId(mat.getTurmaId());
            if (tOpt.isPresent()) {
                Turma t = tOpt.get();
                nomeTurma = t.getNome();
                Optional<AnoEscolar> anoOpt = anoRepo.buscarPorId(t.getAnoEscolarId());
                if(anoOpt.isPresent()) anoLetivo = String.valueOf(anoOpt.get().getAno());
            }

            List<MatriculaDisciplina> disciplinasCursadas = matDiscRepo.buscarPorMatriculaId(mat.getId());

            for (MatriculaDisciplina md : disciplinasCursadas) {
                String nomeDisciplina = "N/A";

                TurmaDisciplinaRepository tdRepo = new TurmaDisciplinaRepository();
                Optional<TurmaDisciplina> tdOpt = tdRepo.buscarPorId(md.getTurmaDisciplinaId());

                if (tdOpt.isPresent()) {
                    Optional<Disciplina> dOpt = disciplinaRepo.buscarPorId(tdOpt.get().getDisciplinaId());
                    if (dOpt.isPresent()) nomeDisciplina = dOpt.get().getNome();
                }
                List<Nota> notas = notaRepo.listarTodos().stream()
                        .filter(n -> n.getMatriculaDisciplinaId() == md.getId())
                        .toList();

                for (Nota n : notas) {
                    String nomePeriodo = "-";
                    Optional<PeriodoLetivo> perOpt = periodoRepo.buscarPorId(n.getPeriodoLetivoId());
                    if(perOpt.isPresent()) nomePeriodo = perOpt.get().getNome();

                    int periodoId = n.getPeriodoLetivoId();
                    int turmaId = mat.getTurmaId();

                    boolean isPublicado = boletimRepo.isPublicado(turmaId, periodoId);

                    Object valorExibido;

                    if (isPublicado) {
                        valorExibido = n.getValorNota();
                    } else {
                        valorExibido = "Em fecham.";
                    }

                    view.getTableModel().addRow(new Object[]{
                            anoLetivo,
                            nomeTurma,
                            nomeDisciplina,
                            nomePeriodo,
                            valorExibido,
                            md.getTotalFaltas()
                    });
                }
            }
        }
    }
}