package controllers;

import models.Disciplina;
import repositories.DisciplinaRepository;
import views.DisciplinaFormView;
import views.DisciplinaListView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Optional;

public class DisciplinaController {

    private final DisciplinaRepository repository;
    private final DisciplinaListView listView;
    private final DisciplinaFormView formView;

    public DisciplinaController(DisciplinaListView listView) {
        this.repository = new DisciplinaRepository();
        this.listView = listView;
        this.formView = new DisciplinaFormView(listView);

        initController();
    }

    private void initController() {
        atualizarTabela();

        // Listeners da ListView
        this.listView.getBtnNovo().addActionListener(e -> abrirFormularioNovo());
        this.listView.getBtnEditar().addActionListener(e -> abrirFormularioEdicao());
        this.listView.getBtnExcluir().addActionListener(e -> excluirDisciplina());
        this.listView.getBtnBuscar().addActionListener(e -> buscarDisciplina());

        // Listeners da FormView
        this.formView.getBtnSalvar().addActionListener(e -> salvarDisciplina());
        this.formView.getBtnCancelar().addActionListener(e -> this.formView.dispose());
    }

    private void atualizarTabela() {
        DefaultTableModel model = listView.getTableModel();
        model.setRowCount(0); // Limpa tabela

        List<Disciplina> lista = repository.listarTodas();

        for (Disciplina d : lista) {
            model.addRow(new Object[]{
                    d.getId(),
                    d.getCodigo(),
                    d.getNome(),
                    d.getCargaHoraria() + "h"
            });
        }
    }

    private void abrirFormularioNovo() {
        formView.limparFormulario();
        formView.setTitle("Nova Disciplina");
        formView.setVisible(true);
    }

    private void abrirFormularioEdicao() {
        int selectedRow = listView.getTabelaDisciplinas().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(listView, "Selecione uma disciplina para editar.");
            return;
        }

        int id = (int) listView.getTableModel().getValueAt(selectedRow, 0);
        Optional<Disciplina> opt = repository.buscarPorId(id);

        if (opt.isPresent()) {
            Disciplina d = opt.get();
            formView.setDisciplinaIdParaEdicao(d.getId());
            formView.setNome(d.getNome());
            formView.setCodigo(d.getCodigo());
            formView.setCargaHoraria(String.valueOf(d.getCargaHoraria()));
            formView.setDescricao(d.getDescricao());
            formView.setConteudo(d.getConteudoProgramatico());

            formView.setTitle("Editar Disciplina");
            formView.setVisible(true);
        }
    }

    private void salvarDisciplina() {
        try {
            // Validação simples
            String nome = formView.getNome();
            String codigo = formView.getCodigo();
            String chStr = formView.getCargaHoraria();

            if (nome.isEmpty() || codigo.isEmpty() || chStr.isEmpty()) {
                JOptionPane.showMessageDialog(formView, "Preencha os campos obrigatórios.");
                return;
            }

            int cargaHoraria = Integer.parseInt(chStr); // Pode lançar exceção se não for número

            Disciplina d = new Disciplina();
            d.setNome(nome);
            d.setCodigo(codigo);
            d.setCargaHoraria(cargaHoraria);
            d.setDescricao(formView.getDescricao());
            d.setConteudoProgramatico(formView.getConteudo());

            if (formView.getDisciplinaIdParaEdicao() > 0) {
                // Edição
                d.setId(formView.getDisciplinaIdParaEdicao());
                repository.editar(d);
            } else {
                // Novo
                repository.salvar(d);
            }

            formView.dispose();
            atualizarTabela();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(formView, "Carga horária deve ser um número inteiro.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirDisciplina() {
        int selectedRow = listView.getTabelaDisciplinas().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(listView, "Selecione uma disciplina para excluir.");
            return;
        }

        int id = (int) listView.getTableModel().getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(listView, "Tem certeza que deseja excluir esta disciplina?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            repository.excluir(id);
            atualizarTabela();
        }
    }

    private void buscarDisciplina() {
        String termo = listView.getTermoBusca().toLowerCase();
        if (termo.isEmpty()) {
            atualizarTabela(); // Se vazio, mostra tudo
            return;
        }

        // Filtro em memória (poderia estar no Repository também)
        DefaultTableModel model = listView.getTableModel();
        model.setRowCount(0);

        List<Disciplina> todas = repository.listarTodas();
        for (Disciplina d : todas) {
            if (d.getNome().toLowerCase().contains(termo) || d.getCodigo().toLowerCase().contains(termo)) {
                model.addRow(new Object[]{
                        d.getId(),
                        d.getCodigo(),
                        d.getNome(),
                        d.getCargaHoraria() + "h"
                });
            }
        }
    }
}