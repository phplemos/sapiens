package controllers;

import models.AnoEscolar;
import models.Serie;
import models.Turma;
import repositories.AnoEscolarRepository;
import repositories.SerieRepository;
import repositories.TurmaRepository;
import views.TurmaDisciplinaView;
import views.components.ComboItem;
import views.TurmaFormView;
import views.TurmaListView;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public class TurmaController {

    private final TurmaRepository turmaRepo;
    private final SerieRepository serieRepo;
    private final AnoEscolarRepository anoRepo;

    private final TurmaListView listView;
    private final TurmaFormView formView;

    public TurmaController(TurmaListView listView) {
        this.turmaRepo = new TurmaRepository();
        this.serieRepo = new SerieRepository();
        this.anoRepo = new AnoEscolarRepository();

        this.listView = listView;
        this.formView = new TurmaFormView(listView);


        carregarCombos();

        initController();
    }

    private void carregarCombos() {
        List<Serie> series = serieRepo.listarTodos();

        for (Serie s : series) {
            formView.adicionarSerie(new ComboItem(s.getId(), s.getNome()));
        }

        List<AnoEscolar> anos = anoRepo.listarTodos();
        for (AnoEscolar a : anos) {
            String label = a.getAno() + " (" + a.getStatus() + ")";
            formView.adicionarAno(new ComboItem(a.getId(), label));
        }
    }

    private void initController() {
        atualizarTabela();

        listView.getBtnNovo().addActionListener(e -> abrirFormNovo());
        listView.getBtnEditar().addActionListener(e -> abrirFormEditar());
        listView.getBtnExcluir().addActionListener(e -> excluir());
        listView.getBtnDisciplinas().addActionListener(e -> abrirGestaoDisciplinas());

        formView.getBtnSalvar().addActionListener(e -> salvar());
        formView.getBtnCancelar().addActionListener(e -> formView.dispose());
    }
    private void abrirGestaoDisciplinas() {
        int row = listView.getTabela().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(listView, "Selecione uma turma para gerenciar as disciplinas.");
            return;
        }

        int turmaId = (int) listView.getTableModel().getValueAt(row, 0);

        Turma turma = turmaRepo.buscarPorId(turmaId).orElse(null);

        if (turma != null) {
            TurmaDisciplinaView tdView = new TurmaDisciplinaView(listView);

            new TurmaDisciplinaController(tdView, turma);

            tdView.setVisible(true);
        }
    }
    private void atualizarTabela() {
        listView.getTableModel().setRowCount(0);
        List<Turma> turmas = turmaRepo.listarTodos();

        for (Turma t : turmas) {
            String nomeSerie = "N/A";
            String nomeAno = "N/A";

            Optional<Serie> optSerie = serieRepo.buscarPorId(t.getSerieId());
            if (optSerie.isPresent()) nomeSerie = optSerie.get().getNome();

            Optional<AnoEscolar> optAno = anoRepo.buscarPorId(t.getAnoEscolarId());
            if (optAno.isPresent()) nomeAno = String.valueOf(optAno.get().getAno());

            listView.getTableModel().addRow(new Object[]{
                    t.getId(),
                    t.getNome(),
                    t.getTurno(),
                    nomeSerie,
                    nomeAno
            });
        }
    }

    private void abrirFormNovo() {
        formView.limparFormulario();
        formView.setTitle("Nova Turma");
        formView.setVisible(true);
    }

    private void abrirFormEditar() {
        int row = listView.getTabela().getSelectedRow();
        if (row == -1) return;

        int id = (int) listView.getTabela().getValueAt(row, 0);
        Optional<Turma> opt = turmaRepo.buscarPorId(id);

        if (opt.isPresent()) {
            Turma t = opt.get();
            formView.setIdParaEdicao(t.getId());
            formView.setNome(t.getNome());
            formView.setTurno(t.getTurno());
            formView.setSerieId(t.getSerieId());
            formView.setAnoId(t.getAnoEscolarId());

            formView.setTitle("Editar Turma");
            formView.setVisible(true);
        }
    }

    private void salvar() {
        String nome = formView.getNome();
        int serieId = formView.getSerieId();
        int anoId = formView.getAnoId();

        if (nome.isEmpty() || serieId == 0 || anoId == 0) {
            JOptionPane.showMessageDialog(formView, "Preencha Nome, Série e Ano.");
            return;
        }

        Turma t = new Turma();
        t.setNome(nome);
        t.setTurno(formView.getTurno());
        t.setSerieId(serieId);
        t.setAnoEscolarId(anoId);

        if (formView.getIdParaEdicao() > 0) {
            t.setId(formView.getIdParaEdicao());
            turmaRepo.editar(t);
        } else {
            turmaRepo.salvar(t);
        }
        formView.dispose();
        atualizarTabela();
    }

    private void excluir() {
        int row = listView.getTabela().getSelectedRow();
        if (row != -1) {
            int id = (int) listView.getTabela().getValueAt(row, 0);
            turmaRepo.excluir(id);
            atualizarTabela();
        }
    }
}