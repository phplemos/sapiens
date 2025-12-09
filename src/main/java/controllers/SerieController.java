package controllers;

import models.Serie;
import repositories.SerieRepository;
import views.SerieFormView;
import views.SerieListView;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public class SerieController {

    private final SerieRepository repository;
    private final SerieListView listView;
    private final SerieFormView formView;

    public SerieController(SerieListView listView) {
        this.repository = new SerieRepository();

        this.listView = listView;
        this.formView = new SerieFormView(listView);

        initController();
    }

    private void initController() {
        atualizarTabela();

        listView.getBtnNovo().addActionListener(e -> abrirFormNovo());
        listView.getBtnEditar().addActionListener(e -> abrirFormEditar());
        listView.getBtnExcluir().addActionListener(e -> excluir());

        formView.getBtnSalvar().addActionListener(e -> salvar());
        formView.getBtnCancelar().addActionListener(e -> formView.dispose());
    }

    private void atualizarTabela() {
        listView.getTableModel().setRowCount(0);
        List<Serie> lista = repository.listarTodos();
        for (Serie s : lista) {
            listView.getTableModel().addRow(new Object[]{s.getId(), s.getNome()});
        }
    }

    private void abrirFormNovo() {
        formView.limparFormulario();
        formView.setTitle("Nova Série");
        formView.setVisible(true);
    }

    private void abrirFormEditar() {
        int row = listView.getTabela().getSelectedRow();
        if (row == -1) return;

        int id = (int) listView.getTabela().getValueAt(row, 0);
        Optional<Serie> opt = repository.buscarPorId(id);

        if (opt.isPresent()) {
            Serie s = opt.get();
            formView.setIdParaEdicao(s.getId());
            formView.setNome(s.getNome());
            formView.setTitle("Editar Série");
            formView.setVisible(true);
        }
    }

    private void salvar() {
        String nome = formView.getNome();
        if(nome.isEmpty()) {
            JOptionPane.showMessageDialog(formView, "O nome é obrigatório.");
            return;
        }

        Serie s = new Serie();
        s.setNome(nome);

        if (formView.getIdParaEdicao() > 0) {
            s.setId(formView.getIdParaEdicao());
            repository.editar(s);
        } else {
            repository.salvar(s);
        }
        formView.dispose();
        atualizarTabela();
    }

    private void excluir() {
        int row = listView.getTabela().getSelectedRow();
        if (row != -1) {
            int id = (int) listView.getTabela().getValueAt(row, 0);
            repository.excluir(id);
            atualizarTabela();
        }
    }
}