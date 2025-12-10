package controllers;

import models.*;
import repositories.*;
import views.DiarioClasseView;
import views.components.ComboItem;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public class DiarioClasseController {

    private final DiarioClasseView view;
    private final int turmaDisciplinaId;

    private final TurmaDisciplinaRepository tdRepo;
    private final TurmaRepository turmaRepo;
    private final PeriodoLetivoRepository periodoRepo;
    private final MatriculaDisciplinaRepository matDiscRepo;
    private final MatriculaRepository matriculaRepo;
    private final PessoaRepository pessoaRepo;
    private final NotaRepository notaRepo;

    public DiarioClasseController(DiarioClasseView view, int turmaDisciplinaId) {
        this.view = view;
        this.turmaDisciplinaId = turmaDisciplinaId;
        this.matDiscRepo = new MatriculaDisciplinaRepository();
        this.matriculaRepo = new MatriculaRepository();
        this.pessoaRepo = new PessoaRepository();
        this.notaRepo = new NotaRepository();
        this.periodoRepo = new PeriodoLetivoRepository();
        this.turmaRepo = new TurmaRepository();
        this.tdRepo = new TurmaDisciplinaRepository();
        carregarInfoInicial();
        initController();
    }
    private void initController() {
        view.getBtnBuscar().addActionListener(e -> carregarDiario());

        view.getBtnSalvar().addActionListener(e -> salvarDiario());
        view.getBtnFechar().addActionListener(e -> view.dispose());
    }
    private void carregarDiario() {
        int periodoId = view.getPeriodoSelecionadoId();
        if (periodoId == 0) return;

        view.getModel().setRowCount(0);

        List<MatriculaDisciplina> listaAlunos = matDiscRepo.buscarPorTurmaDisciplinaId(turmaDisciplinaId);

        for (MatriculaDisciplina md : listaAlunos) {
            String nomeAluno = buscarNomeAluno(md.getMatriculaId());

            Optional<Nota> notaOpt = notaRepo.buscarNota(md.getId(), periodoId);
            String notaValor = notaOpt.map(n -> String.valueOf(n.getValorNota())).orElse("");

            int faltas = md.getTotalFaltas();

            view.getModel().addRow(new Object[]{ md.getId(), nomeAluno, notaValor, faltas });
        }
    }
    private String buscarNomeAluno(int matriculaId) {
        return matriculaRepo.buscarPorId(matriculaId)
                .flatMap(m -> pessoaRepo.buscarPorId(m.getAlunoPessoaId()))
                .map(Pessoa::getNomeCompleto)
                .orElse("Aluno Desconhecido");
    }
    private void carregarInfoInicial() {
        Optional<TurmaDisciplina> tdOpt = tdRepo.buscarPorId(turmaDisciplinaId);
        if (tdOpt.isPresent()) {
            int turmaId = tdOpt.get().getTurmaId();
            Optional<Turma> tOpt = turmaRepo.buscarPorId(turmaId);

            if (tOpt.isPresent()) {
                int anoId = tOpt.get().getAnoEscolarId();
                view.getCbPeriodo().removeAllItems();
                List<PeriodoLetivo> periodos = periodoRepo.buscarPorAnoEscolarId(anoId);
                for (PeriodoLetivo p : periodos) {
                    view.getCbPeriodo().addItem(new ComboItem(p.getId(),p.getNome()));
                }
            }
        }
    }


    private void carregarNotasEFaltas() {
        String selecionado = (String) view.getCbPeriodo().getSelectedItem();
        if (selecionado == null) return;

        int periodoId = Integer.parseInt(selecionado.split(" - ")[0]);
        view.getModel().setRowCount(0);

        List<MatriculaDisciplina> alunosNaMateria = matDiscRepo.listarTodos().stream()
                .filter(md -> md.getTurmaDisciplinaId() == turmaDisciplinaId)
                .toList();

        for (MatriculaDisciplina md : alunosNaMateria) {
            String nomeAluno = "Desconhecido";
            Optional<Matricula> mOpt = matriculaRepo.buscarPorId(md.getMatriculaId());
            if (mOpt.isPresent()) {
                Optional<Pessoa> p = pessoaRepo.buscarPorId(mOpt.get().getAlunoPessoaId());
                if (p.isPresent()) nomeAluno = p.get().getNomeCompleto();
            }

            Optional<Nota> nOpt = notaRepo.buscarNota(md.getId(), periodoId);
            String notaValor = nOpt.isPresent() ? String.valueOf(nOpt.get().getValorNota()) : "";


            int faltas = md.getTotalFaltas();

            view.getModel().addRow(new Object[]{
                    md.getId(),
                    nomeAluno,
                    notaValor,
                    faltas
            });
        }
    }

    private void salvarDiario() {
        if (view.getTabela().isEditing()) view.getTabela().getCellEditor().stopCellEditing();

        ComboItem selecionado = (ComboItem) view.getCbPeriodo().getSelectedItem();
        if (selecionado == null) return;
        int periodoId = selecionado.getId();

        int linhas = view.getModel().getRowCount();

        for (int i = 0; i < linhas; i++) {
            try {
                int mdId = (int) view.getModel().getValueAt(i, 0);
                String notaStr = view.getModel().getValueAt(i, 2).toString().replace(",", ".");
                String faltasStr = view.getModel().getValueAt(i, 3).toString();

                Optional<MatriculaDisciplina> mdOpt = matDiscRepo.buscarPorId(mdId);
                if (mdOpt.isPresent()) {
                    MatriculaDisciplina md = mdOpt.get();
                    md.setTotalFaltas(Integer.parseInt(faltasStr));
                    matDiscRepo.editar(md);
                }

                if (!notaStr.isEmpty()) {
                    double valor = Double.parseDouble(notaStr);
                    Optional<Nota> notaExistente = notaRepo.buscarNota(mdId, periodoId);

                    if (notaExistente.isPresent()) {
                        Nota n = notaExistente.get();
                        n.setValorNota(valor);
                        notaRepo.editar(n);
                    } else {
                        Nota n = new Nota();
                        n.setMatriculaDisciplinaId(mdId);
                        n.setPeriodoLetivoId(periodoId);
                        n.setValorNota(valor);
                        notaRepo.salvar(n);
                    }
                }

            } catch (Exception e) {
                System.err.println("Erro ao salvar linha " + i + ": " + e.getMessage());
            }
        }
        JOptionPane.showMessageDialog(view, "Notas e Frequências salvas!");
    }
}