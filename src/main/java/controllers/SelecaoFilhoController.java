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

    // Repositórios necessários
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

        // Ação: Ver Boletim
        view.getBtnVerBoletim().addActionListener(e -> abrirFuncionalidade("BOLETIM"));

        // Ação: Ver Histórico
        view.getBtnVerHistorico().addActionListener(e -> abrirFuncionalidade("HISTORICO"));

        view.getBtnCancelar().addActionListener(e -> view.dispose());

        view.setVisible(true);
    }

    private void carregarListaDeFilhos() {
        view.getTableModel().setRowCount(0);

        // 1. Busca IDs dos alunos vinculados ao responsável
        List<Integer> idsAlunos = alunoRespRepo.buscarIdsAlunosDoResponsavel(responsavelId);

        if (idsAlunos.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhum aluno vinculado a este responsável.");
            return;
        }

        // 2. Para cada ID, busca Nome e Turma
        for (Integer alunoId : idsAlunos) {
            Optional<Pessoa> pessoaOpt = pessoaRepo.buscarPorId(alunoId);

            if (pessoaOpt.isPresent()) {
                String nome = pessoaOpt.get().getNomeCompleto();
                String turma = "Sem matrícula";

                // Busca matrícula mais recente para saber a turma atual
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

        // Pega o ID do aluno da coluna 0
        int alunoId = (int) view.getTabelaFilhos().getValueAt(row, 0);

        if (tipo.equals("BOLETIM")) {
            // REUTILIZAÇÃO: Chama o Controller do Boletim que criamos antes
            BoletimView boletimView = new BoletimView(view);
            new BoletimController(boletimView, alunoId);

        } else if (tipo.equals("HISTORICO")) {
            // REUTILIZAÇÃO: Chama o Controller do Histórico que criamos antes
            AlunoHistoricoView historicoView = new AlunoHistoricoView(view);
            // boolean isAdmin = false (Responsável vê como user comum)
            new AlunoHistoricoController(historicoView, alunoId, false);
            historicoView.setVisible(true);
        }
    }
}