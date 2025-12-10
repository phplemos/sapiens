package controllers;

import models.ParametrosEscola;
import repositories.ParametrosEscolaRepository;
import views.ParametrosEscolaView;

import javax.swing.*;

public class ParametrosEscolaController {

    private final ParametrosEscolaView view;
    private final ParametrosEscolaRepository repo;
    private ParametrosEscola model;

    public ParametrosEscolaController(ParametrosEscolaView view) {
        this.view = view;
        this.repo = new ParametrosEscolaRepository();
        initController();
    }

    private void initController() {
        carregarDados();

        view.btnSalvar.addActionListener(e -> salvarDados());
        view.btnCancelar.addActionListener(e -> view.dispose());
    }

    private void carregarDados() {
        this.model = repo.buscarParametrosGerais();

        view.txtNomeEscola.setText(model.getNomeInstituicao());
        view.spnMediaAprovacao.setValue(model.getMediaAprovacao());
        view.spnMediaRecuperacao.setValue(model.getMediaRecuperacao());
        view.spnLimiteFaltas.setValue(model.getLimiteFaltas());
    }

    private void salvarDados() {
        try {
            model.setNomeInstituicao(view.txtNomeEscola.getText());
            model.setMediaAprovacao((Double) view.spnMediaAprovacao.getValue());
            model.setMediaRecuperacao((Double) view.spnMediaRecuperacao.getValue());
            model.setLimiteFaltas((Integer) view.spnLimiteFaltas.getValue());

            repo.salvar(model);

            JOptionPane.showMessageDialog(view, "Parâmetros atualizados com sucesso!");
            view.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erro ao salvar: " + ex.getMessage());
        }
    }
}