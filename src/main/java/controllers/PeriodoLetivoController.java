package controllers;

import models.AnoEscolar;
import models.PeriodoLetivo;
import repositories.AnoEscolarRepository;
import repositories.PeriodoLetivoRepository;
import views.components.ComboItem;
import views.PeriodoLetivoFormView;
import views.PeriodoLetivoListView;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class PeriodoLetivoController {

    private final PeriodoLetivoRepository repo;
    private final AnoEscolarRepository anoRepo; // Precisa do repo de anos!
    private final PeriodoLetivoListView listView;
    private final PeriodoLetivoFormView formView;

    public PeriodoLetivoController(PeriodoLetivoListView listView) {
        this.repo = new PeriodoLetivoRepository();
        this.anoRepo = new AnoEscolarRepository();
        this.listView = listView;
        this.formView = new PeriodoLetivoFormView(listView);

        carregarComboAnos();
        initController();
    }

    private void carregarComboAnos() {
        List<AnoEscolar> anos = anoRepo.listarTodos();
        for (AnoEscolar ano : anos) {
            String label = ano.getAno() + " (" + ano.getStatus() + ")";
            formView.adicionarAnoCombo(new ComboItem(ano.getId(), label));
        }
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
        List<PeriodoLetivo> lista = repo.listarTodos();
        for (PeriodoLetivo p : lista) {
            listView.getTableModel().addRow(new Object[]{
                    p.getId(),
                    p.getNome(),
                    p.getAnoEscolarId(),
                    p.getDataInicio(),
                    p.getDataFim()
            });
        }
    }

    private void abrirFormNovo() {
        formView.limparFormulario();
        formView.setTitle("Novo Período");
        formView.setVisible(true);
    }

    private void abrirFormEditar() {
        int row = listView.getTabela().getSelectedRow();
        if (row == -1) return;
        int id = (int) listView.getTabela().getValueAt(row, 0);
        Optional<PeriodoLetivo> opt = repo.buscarPorId(id);
        if (opt.isPresent()) {
            PeriodoLetivo p = opt.get();
            formView.setIdParaEdicao(p.getId());
            formView.setNome(p.getNome());
            formView.setAnoSelecionado(p.getAnoEscolarId());
            formView.setDataInicio(p.getDataInicio().toString());
            formView.setDataFim(p.getDataFim().toString());
            formView.setTitle("Editar Período");
            formView.setVisible(true);
        }
    }

    private void salvar() {
        try {
            String nome = formView.getNome();
            int anoId = formView.getAnoSelecionadoId();
            LocalDate inicio = LocalDate.parse(formView.getDataInicio());
            LocalDate fim = LocalDate.parse(formView.getDataFim());

            if (anoId == 0) {
                JOptionPane.showMessageDialog(formView, "Selecione um Ano Escolar.");
                return;
            }

            PeriodoLetivo p = new PeriodoLetivo();
            p.setNome(nome);
            p.setAnoEscolarId(anoId);
            p.setDataInicio(inicio);
            p.setDataFim(fim);

            if (formView.getIdParaEdicao() > 0) {
                p.setId(formView.getIdParaEdicao());
                repo.editar(p);
            } else {
                repo.salvar(p);
            }
            formView.dispose();
            atualizarTabela();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(formView, "Datas inválidas. Use o formato AAAA-MM-DD (ex: 2025-02-01).");
        }
    }

    private void excluir() {
        int row = listView.getTabela().getSelectedRow();
        if (row != -1) {
            int id = (int) listView.getTabela().getValueAt(row, 0);
            repo.excluir(id);
            atualizarTabela();
        }
    }
}