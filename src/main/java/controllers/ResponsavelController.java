package controllers;

import models.Endereco;
import models.Pessoa;
import models.Responsavel;
import repositories.EnderecoRepository;
import repositories.PessoaRepository;
import repositories.ResponsavelRepository;
import views.ResponsavelFormView;
import views.ResponsavelListView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.List;

public class ResponsavelController {

    private final ResponsavelRepository responsavelRepo;
    private final PessoaRepository pessoaRepo;
    private final EnderecoRepository enderecoRepo;

    private final ResponsavelListView listView;
    private final ResponsavelFormView formView; // O formulário de popup

    public ResponsavelController(ResponsavelListView listView) {
        this.responsavelRepo = new ResponsavelRepository();
        this.pessoaRepo = new PessoaRepository();
        this.enderecoRepo = new EnderecoRepository();

        this.listView = listView;
        this.formView = new ResponsavelFormView(listView);

        initController();
    }

    /**
     * Ponto de entrada do Controller.
     * Carrega os dados iniciais e configura os eventos (cliques de botão).
     */
    private void initController() {
        atualizarTabela();

        this.listView.getBtnNovo().addActionListener(e -> {
            abrirFormularioNovoResponsavel();
        });

        this.listView.getBtnEditar().addActionListener(e -> {
            abrirFormularioEditarResponsavel();
        });

        this.listView.getBtnExcluir().addActionListener(e -> {
            excluirResponsavel();
        });

        this.formView.getBtnSalvar().addActionListener(e -> {
            salvarResponsavel();
        });

        this.formView.getBtnCancelar().addActionListener(e -> {
            this.formView.dispose();
        });
    }

    private void atualizarTabela() {
        System.out.println("Atualizando tabela de professores...");

        DefaultTableModel tableModel = this.listView.getTableModel();

        tableModel.setRowCount(0);

        List<Responsavel> responsaveis = this.responsavelRepo.listarTodos();

        for (Responsavel responsavel : responsaveis) {
            Pessoa pessoa = this.pessoaRepo.buscarPorId(responsavel.getPessoaId()).orElse(null);

            if (pessoa != null) {
                Object[] rowData = {
                        pessoa.getId(),
                        pessoa.getNomeCompleto(),
                        pessoa.getCpf(),
                        pessoa.getEmailContato()
                };

                tableModel.addRow(rowData);
            }
        }
    }


    private void abrirFormularioNovoResponsavel() {
        System.out.println("Botão NOVO clicado");
        formView.limparFormulario();
        formView.setTitle("Novo Responsavel");
        formView.setVisible(true);
    }

    private void abrirFormularioEditarResponsavel() {
        System.out.println("Botão EDITAR clicado");
        int selectedRow = this.listView.getTabelaResponsaveis().getSelectedRow();

        if (!this.isLinhaSelecionada(selectedRow)) return;

        int pessoaId = (Integer) this.listView.getTableModel().getValueAt(selectedRow, 0);

        System.out.println("ID selecionado: " + pessoaId);


        Pessoa pessoa = this.pessoaRepo.buscarPorId(pessoaId).orElse(null);

        Endereco endereco = null;
        if (pessoa != null) {
            endereco = this.enderecoRepo.buscarPorId(pessoa.getEnderecoId()).orElse(null);
        }

        if (pessoa == null || endereco == null) {
            JOptionPane.showMessageDialog(
                    this.listView,
                    "Erro ao buscar os dados completos do responsavel (ID: " + pessoaId + ").",
                    "Erro de Dados",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        this.formView.limparFormulario(); // Limpa antes de preencher
        this.formView.setPessoaIdParaEdicao(pessoaId); // Salva o ID para o 'salvar'

        this.formView.setNome(pessoa.getNomeCompleto());
        this.formView.setCpf(pessoa.getCpf());
        this.formView.setDataNasc(pessoa.getDataNascimento().toString()); // Converte LocalDate para String
        this.formView.setRg(pessoa.getRg());
        this.formView.setTelefone(pessoa.getTelefone());
        this.formView.setEmail(pessoa.getEmailContato());

        this.formView.setCep(endereco.getCep());
        this.formView.setLogradouro(endereco.getLogradouro());
        this.formView.setNumero(endereco.getNumero());
        this.formView.setBairro(endereco.getBairro());
        this.formView.setCidade(endereco.getCidade());
        this.formView.setEstado(endereco.getEstado());

        this.formView.setTitle("Editando Responsavel: " + pessoa.getNomeCompleto());
        this.formView.setVisible(true); // Abre o popup
    }

    private void excluirResponsavel() {
        int selectedRow = this.listView.getTabelaResponsaveis().getSelectedRow();
        int pessoaId = (Integer) this.listView.getTableModel().getValueAt(selectedRow, 0);

        System.out.println("ID selecionado: " + pessoaId);

        if(!this.isLinhaSelecionada(selectedRow)){
            JOptionPane.showMessageDialog(
                    this.listView,
                    "Selecione um Responsavel que deseja excluir.",
                    "Erro de seleção",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(listView, "Tem certeza que deseja excluir esse Responsavel?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.responsavelRepo.excluir(pessoaId);
            formView.dispose();
        }
        atualizarTabela();
    }

    private void salvarResponsavel() {
        System.out.println("Botão SALVAR (do form) clicado");

        Endereco newEndereco = new Endereco();
        newEndereco.setCep(formView.getCep());
        newEndereco.setLogradouro(formView.getLogradouro());
        newEndereco.setNumero(formView.getNumero());
        newEndereco.setBairro(formView.getBairro());
        newEndereco.setCidade(formView.getCidade());
        newEndereco.setEstado(formView.getEstado());
        newEndereco = this.enderecoRepo.salvar(newEndereco);

        Pessoa newPessoa = new Pessoa();
        newPessoa.setCpf(formView.getCpf());
        newPessoa.setDataNascimento(LocalDate.parse(formView.getDataNasc()));
        newPessoa.setRg(formView.getRg());
        newPessoa.setNomeCompleto(formView.getNome());
        newPessoa.setEmailContato(formView.getEmail());
        newPessoa.setTelefone(formView.getTelefone());
        newPessoa.setEnderecoId(newEndereco.getId());
        this.pessoaRepo.salvar(newPessoa);

        Responsavel responsavel = new Responsavel();
        responsavel.setPessoaId(newPessoa.getId());
        this.responsavelRepo.salvar(responsavel);

        formView.dispose();
        atualizarTabela();
    }

    private boolean isLinhaSelecionada(int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this.listView,
                    "Por favor, selecione um Responsavel na tabela para editar.",
                    "Nenhum Responsavels Selecionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

}