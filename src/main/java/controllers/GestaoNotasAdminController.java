package controllers;

import models.*;
import repositories.*;
import views.components.ComboItem;
import views.NotaLancamentoView;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public class GestaoNotasAdminController {

    private final NotaLancamentoView view;

    private final TurmaRepository turmaRepo;
    private final TurmaDisciplinaRepository turmaDiscRepo;
    private final DisciplinaRepository disciplinaRepo;
    private final PeriodoLetivoRepository periodoRepo;
    private final MatriculaRepository matriculaRepo;
    private final MatriculaDisciplinaRepository matDiscRepo;
    private final PessoaRepository pessoaRepo;
    private final NotaRepository notaRepo;
    private final AlunoRepository alunoRepo;
    private final BoletimStatusRepository boletimRepo;

    public GestaoNotasAdminController(NotaLancamentoView view) {
        this.view = view;

        this.turmaRepo = new TurmaRepository();
        this.turmaDiscRepo = new TurmaDisciplinaRepository();
        this.disciplinaRepo = new DisciplinaRepository();
        this.periodoRepo = new PeriodoLetivoRepository();
        this.matriculaRepo = new MatriculaRepository();
        this.matDiscRepo = new MatriculaDisciplinaRepository();
        this.pessoaRepo = new PessoaRepository();
        this.notaRepo = new NotaRepository();
        this.alunoRepo = new AlunoRepository();
        this.boletimRepo = new BoletimStatusRepository();

        initController();
    }

    private void initController() {
        carregarComboTurmas();

        view.getCbTurma().addActionListener(e -> aoSelecionarTurma());

        view.getBtnBuscar().addActionListener(e -> listarAlunosENotas());
        view.getBtnSalvarCorrecao().addActionListener(e -> salvarNotas());
        view.getBtnPublicar().addActionListener(e -> publicarBoletim());
    }

    private void publicarBoletim() {
        int turmaId = view.getTurmaSelecionadaId();
        int periodoId = view.getPeriodoSelecionadoId();
        List<Matricula> matriculas = this.matriculaRepo.buscarPorTurmaId(turmaId);

        if (turmaId == 0 || periodoId == 0) {
            JOptionPane.showMessageDialog(view, "Selecione Turma e Período.");
            return;
        }

        boolean jaPublicado = boletimRepo.isPublicado(turmaId, periodoId);

        if (jaPublicado) {
            int confirm = JOptionPane.showConfirmDialog(view,
                    "Este boletim já está publicado. Deseja OCULTAR as notas novamente?",
                    "Despublicar", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boletimRepo.registrarPublicacao(turmaId, periodoId, false);
                JOptionPane.showMessageDialog(view, "Boletim ocultado.");
            }
        } else {
            int confirm = JOptionPane.showConfirmDialog(view,
                    "Isso tornará as notas visíveis para todos os alunos da turma.\nConfirma?",
                    "Publicar Boletim", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boletimRepo.registrarPublicacao(turmaId, periodoId, true);
                for(Matricula matricula : matriculas) {
                    ComunicadoController.dispararNotificacaoSistema(matricula.getAlunoPessoaId(),"Boletim publicado","Sua nota foi publicada, verifique o boletim");
                }
                JOptionPane.showMessageDialog(view, "Boletim publicado com sucesso!");
            }
        }
    }

    private void carregarComboTurmas() {
        List<Turma> turmas = turmaRepo.listarTodos();
        view.getCbTurma().removeAllItems();
        view.adicionarTurma(new ComboItem(0, "Selecione..."));

        for (Turma t : turmas) {
            view.adicionarTurma(new ComboItem(t.getId(), t.getNome()));
        }
    }

    private void aoSelecionarTurma() {
        int turmaId = view.getTurmaSelecionadaId();
        view.limparDisciplinas();
        view.limparPeriodos();

        if (turmaId == 0) return;

        List<TurmaDisciplina> ofertas = turmaDiscRepo.buscarPorTurmaId(turmaId);
        for (TurmaDisciplina td : ofertas) {
            Optional<Disciplina> d = disciplinaRepo.buscarPorId(td.getDisciplinaId());
            d.ifPresent(disciplina ->
                    view.adicionarDisciplina(new ComboItem(disciplina.getId(), disciplina.getNome()))
            );
        }

        Optional<Turma> turmaOpt = turmaRepo.buscarPorId(turmaId);
        if (turmaOpt.isPresent()) {
            int anoId = turmaOpt.get().getAnoEscolarId();
            List<PeriodoLetivo> periodos = periodoRepo.buscarPorAnoEscolarId(anoId);
            for (PeriodoLetivo p : periodos) {
                view.adicionarPeriodo(new ComboItem(p.getId(), p.getNome()));
            }
        }
    }

    private void listarAlunosENotas() {
        int turmaId = view.getTurmaSelecionadaId();
        int disciplinaId = view.getDisciplinaSelecionadaId();
        int periodoId = view.getPeriodoSelecionadoId();

        if (turmaId == 0 || disciplinaId == 0 || periodoId == 0) {
            JOptionPane.showMessageDialog(view, "Selecione Turma, Disciplina e Período.");
            return;
        }

        view.getTableModel().setRowCount(0);

        List<Matricula> matriculas = matriculaRepo.listarTodos().stream()
                .filter(m -> m.getTurmaId() == turmaId)
                .toList();

        if (matriculas.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhum aluno matriculado nesta turma.");
            return;
        }

        for (Matricula m : matriculas) {
            String nomeAluno = "Desconhecido";
            Optional<Aluno> alunoOpt = alunoRepo.buscarPorPessoaId(m.getAlunoPessoaId());
            if (alunoOpt.isPresent()) {
                Optional<Pessoa> p = pessoaRepo.buscarPorId(alunoOpt.get().getPessoaId());
                if(p.isPresent()) nomeAluno = p.get().getNomeCompleto();
            }

            Optional<TurmaDisciplina> oferta = turmaDiscRepo.buscarPorTurmaId(turmaId).stream()
                    .filter(td -> td.getDisciplinaId() == disciplinaId)
                    .findFirst();

            if (oferta.isEmpty()) continue;

            int ofertaId = oferta.get().getId();

            Optional<MatriculaDisciplina> mdOpt = matDiscRepo.buscarPorMatriculaId(m.getId()).stream()
                    .filter(md -> md.getTurmaDisciplinaId() == ofertaId)
                    .findFirst();

            if (mdOpt.isPresent()) {
                int matriculaDisciplinaId = mdOpt.get().getId();
                int totalFaltas = mdOpt.get().getTotalFaltas();
                Optional<Nota> notaOpt = notaRepo.buscarNota(matriculaDisciplinaId, periodoId);
                String valorNota = "";
                if (notaOpt.isPresent()) {
                    valorNota = String.valueOf(notaOpt.get().getValorNota());
                }

                view.getTableModel().addRow(new Object[]{
                        matriculaDisciplinaId,
                        nomeAluno,
                        valorNota,
                        totalFaltas
                });
            }
        }
    }

    private void salvarNotas() {
        if (view.getTabela().isEditing()) {
            view.getTabela().getCellEditor().stopCellEditing();
        }

        int periodoId = view.getPeriodoSelecionadoId();
        if (periodoId == 0) return;

        int linhas = view.getTableModel().getRowCount();
        int notasSalvas = 0;

        for (int i = 0; i < linhas; i++) {
            try {
                int matriculaDisciplinaId = (int) view.getTableModel().getValueAt(i, 0);
                Object valorObj = view.getTableModel().getValueAt(i, 2);

                String valorStr = (valorObj == null) ? "" : valorObj.toString().trim();

                if (valorStr.isEmpty()) continue;

                double valor = Double.parseDouble(valorStr.replace(",", "."));

                if (valor < 0 || valor > 10) {
                    throw new NumberFormatException("Nota inválida");
                }

                Optional<Nota> notaExistente = notaRepo.buscarNota(matriculaDisciplinaId, periodoId);

                Nota nota;
                if (notaExistente.isPresent()) {
                    nota = notaExistente.get();
                    nota.setValorNota(valor);
                    notaRepo.editar(nota);
                } else {
                    nota = new Nota();
                    nota.setMatriculaDisciplinaId(matriculaDisciplinaId);
                    nota.setPeriodoLetivoId(periodoId);
                    nota.setValorNota(valor);
                    notaRepo.salvar(nota);
                }
                notasSalvas++;

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view,
                        "Erro na linha " + (i + 1) + ": Nota inválida.\nUse números entre 0 e 10.",
                        "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        JOptionPane.showMessageDialog(view, notasSalvas + " notas salvas com sucesso!");
    }
}
