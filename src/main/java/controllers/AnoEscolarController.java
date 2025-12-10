package controllers;

import models.AnoEscolar;
import repositories.AnoEscolarRepository;
import views.AnoEscolarFormView;
import views.AnoEscolarListView;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public class AnoEscolarController {

    private final AnoEscolarRepository repository;
    private final AnoEscolarListView listView;
    private final AnoEscolarFormView formView;

    public AnoEscolarController(AnoEscolarListView listView) {
        this.repository = new AnoEscolarRepository();
        this.listView = listView;
        this.formView = new AnoEscolarFormView(listView);
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
        List<AnoEscolar> lista = repository.listarTodos();
        for (AnoEscolar a : lista) {
            listView.getTableModel().addRow(new Object[]{a.getId(), a.getAno(), a.getStatus()});
        }
    }

    private void abrirFormNovo() {
        formView.limparFormulario();
        formView.setTitle("Novo Ano Escolar");
        formView.setVisible(true);
    }

    private void abrirFormEditar() {
        int row = listView.getTabela().getSelectedRow();
        if (row == -1) return;

        int id = (int) listView.getTabela().getValueAt(row, 0);
        Optional<AnoEscolar> opt = repository.buscarPorId(id);

        if (opt.isPresent()) {
            AnoEscolar a = opt.get();
            formView.setIdParaEdicao(a.getId());
            formView.setAno(String.valueOf(a.getAno()));
            formView.setStatus(a.getStatus());
            formView.setTitle("Editar Ano");
            formView.setVisible(true);
        }
    }

    private void salvar() {
        try {
            int ano = Integer.parseInt(formView.getAno());
            String status = formView.getStatus();

            AnoEscolar a = new AnoEscolar();
            a.setAno(ano);
            a.setStatus(status);

            if (formView.getIdParaEdicao() > 0) {
                a.setId(formView.getIdParaEdicao());
                repository.editar(a);
            } else {
                repository.salvar(a);
            }
            formView.dispose();
            atualizarTabela();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(formView, "O Ano deve ser um número válido.");
        }
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