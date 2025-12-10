package controllers;

import models.*;
import repositories.*;
import views.components.ComboItem;
import views.TurmaDisciplinaView;
import views.components.SearchComboBox;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TurmaDisciplinaController {

    private final TurmaDisciplinaRepository tdRepo;
    private final DisciplinaRepository disciplinaRepo;
    private final ProfessorRepository professorRepo;
    private final PessoaRepository pessoaRepo;

    private final TurmaDisciplinaView view;
    private final int turmaId;

    public TurmaDisciplinaController(TurmaDisciplinaView view, Turma turma) {
        this.view = view;
        this.turmaId = turma.getId();

        this.tdRepo = new TurmaDisciplinaRepository();
        this.disciplinaRepo = new DisciplinaRepository();
        this.professorRepo = new ProfessorRepository();
        this.pessoaRepo = new PessoaRepository();

        this.view.setTituloTurma(turma.getNome());

        carregarCombos();
        initController();
    }

    private void carregarCombos() {

        List<Disciplina> disciplinas = disciplinaRepo.listarTodas();
        List<ComboItem> listaDisciplinas = new ArrayList<>();

        for (Disciplina d : disciplinas) {
            listaDisciplinas.add(new ComboItem(d.getId(), d.getNome() + " (" + d.getCodigo() + ")"));
        }

        JComboBox<ComboItem> cbDisciplina = view.getCbDisciplina();
        cbDisciplina.removeAllItems();
        for (ComboItem item : listaDisciplinas) {
            cbDisciplina.addItem(item);
        }

        SearchComboBox.use(cbDisciplina, listaDisciplinas);

        List<Professor> professores = professorRepo.listarTodos();
        List<ComboItem> listaProfessores = new ArrayList<>();

        for (Professor prof : professores) {
            Pessoa p = pessoaRepo.buscarPorId(prof.getPessoaId()).orElse(null);
            if (p != null) {
                listaProfessores.add(new ComboItem(p.getId(), p.getNomeCompleto()));
            }
        }

        JComboBox<ComboItem> cbProfessor = view.getCbProfessor();
        cbProfessor.removeAllItems();
        for (ComboItem item : listaProfessores) {
            cbProfessor.addItem(item);
        }

        SearchComboBox.use(cbProfessor, listaProfessores);
    }

    private void initController() {
        atualizarTabela();

        view.getBtnAdicionar().addActionListener(e -> adicionarVinculo());
        view.getBtnRemover().addActionListener(e -> removerVinculo());
    }

    private void atualizarTabela() {
        view.getTableModel().setRowCount(0);

        List<TurmaDisciplina> lista = tdRepo.buscarPorTurmaId(this.turmaId);

        for (TurmaDisciplina td : lista) {
            String nomeDisciplina = "Desconhecida";
            Optional<Disciplina> d = disciplinaRepo.buscarPorId(td.getDisciplinaId());
            if (d.isPresent()) nomeDisciplina = d.get().getNome();

            String nomeProfessor = "Desconhecido";
            Optional<Pessoa> p = pessoaRepo.buscarPorId(td.getProfessorPessoaId());
            if (p.isPresent()) nomeProfessor = p.get().getNomeCompleto();

            view.getTableModel().addRow(new Object[]{
                    td.getId(),
                    nomeDisciplina,
                    nomeProfessor
            });
        }
    }

    private void adicionarVinculo() {
        int disciplinaId = view.getDisciplinaSelecionadaId();
        int professorPessoaId = view.getProfessorSelecionadoPessoaId();

        if (disciplinaId == 0 || professorPessoaId == 0) {
            JOptionPane.showMessageDialog(view, "Selecione uma disciplina e um professor.");
            return;
        }

        TurmaDisciplina td = new TurmaDisciplina();
        td.setTurmaId(this.turmaId);
        td.setDisciplinaId(disciplinaId);
        td.setProfessorPessoaId(professorPessoaId);

        tdRepo.salvar(td);
        atualizarTabela();
    }

    private void removerVinculo() {
        int row = view.getTabela().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione um item para remover.");
            return;
        }

        int idVinculo = (int) view.getTabela().getValueAt(row, 0);
        tdRepo.excluir(idVinculo);
        atualizarTabela();
    }
}