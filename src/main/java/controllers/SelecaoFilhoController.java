package controllers;

import models.*;
import repositories.*;
import views.*;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public class SelecaoFilhoController {

    private final SelecaoFilhoView view;
    private final int responsavelId;

    private final AlunoResponsavelRepository alunoRespRepo;
    private final PessoaRepository pessoaRepo;
    private final MatriculaRepository matriculaRepo;
    private final TurmaRepository turmaRepo;

    public SelecaoFilhoController(SelecaoFilhoView view, int responsavelId) {
        this.view = view;
        this.responsavelId = responsavelId;

        this.alunoRespRepo = new AlunoResponsavelRepository();
        this.pessoaRepo = new PessoaRepository();
        this.matriculaRepo = new MatriculaRepository();
        this.turmaRepo = new TurmaRepository();

        initController();
    }

    private void initController() {
        carregarListaDeFilhos();

        view.getBtnVerBoletim().addActionListener(e -> abrirFuncionalidade("BOLETIM"));

        view.getBtnVerHistorico().addActionListener(e -> abrirFuncionalidade("HISTORICO"));

        view.getBtnCancelar().addActionListener(e -> view.dispose());

    }

    private void carregarListaDeFilhos() {
        view.getTableModel().setRowCount(0);

        List<Integer> idsAlunos = alunoRespRepo.buscarIdsAlunosDoResponsavel(responsavelId);

        if (idsAlunos.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhum aluno vinculado a este responsável.");
            return;
        }

        for (Integer alunoId : idsAlunos) {
            Optional<Pessoa> pessoaOpt = pessoaRepo.buscarPorId(alunoId);

            if (pessoaOpt.isPresent()) {
                String nome = pessoaOpt.get().getNomeCompleto();
                String turma = "Sem matrícula";

                Optional<Matricula> matOpt = matriculaRepo.listarTodos().stream()
                        .filter(m -> m.getAlunoPessoaId() == alunoId)
                        .max((m1, m2) -> Integer.compare(m1.getId(), m2.getId()));

                if (matOpt.isPresent()) {
                    Optional<Turma> tOpt = turmaRepo.buscarPorId(matOpt.get().getTurmaId());
                    if (tOpt.isPresent()) {
                        turma = tOpt.get().getNome();
                    }
                }

                view.getTableModel().addRow(new Object[]{alunoId, nome, turma});
            }
        }
    }

    private void abrirFuncionalidade(String tipo) {
        int row = view.getTabelaFilhos().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione um aluno na lista.");
            return;
        }

        int alunoId = (int) view.getTabelaFilhos().getValueAt(row, 0);

        if (tipo.equals("BOLETIM")) {
            BoletimView boletimView = new BoletimView(view);
            new BoletimController(boletimView, alunoId);

        } else if (tipo.equals("HISTORICO")) {
            AlunoHistoricoView historicoView = new AlunoHistoricoView(view);
            new AlunoHistoricoController(historicoView, alunoId, false);
            historicoView.setVisible(true);
        }
    }
}