package controllers;

import models.Endereco;
import models.Pessoa;
import models.Professor;
import repositories.EnderecoRepository;
import repositories.PessoaRepository;
import repositories.ProfessorRepository;
import views.ProfessorFormView;
import views.ProfessorListView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.List;

public class ProfessorController {

    private final ProfessorRepository professorRepo;
    private final PessoaRepository pessoaRepo;
    private final EnderecoRepository enderecoRepo;

    private final ProfessorListView listView;
    private final ProfessorFormView formView;

    public ProfessorController(ProfessorListView listView) {
        this.professorRepo = new ProfessorRepository();
        this.pessoaRepo = new PessoaRepository();
        this.enderecoRepo = new EnderecoRepository();

        this.listView = listView;
        this.formView = new ProfessorFormView(this.listView);

        initController();
    }

    /**
     * Ponto de entrada do Controller.
     * Carrega os dados iniciais e configura os eventos (cliques de botão).
     */
    private void initController() {
        atualizarTabela("");
        this.listView.getBtnBuscar().addActionListener(e -> {
            atualizarTabela(listView.getTxtBusca().getText());
        });
        this.listView.getBtnNovo().addActionListener(e -> {
            abrirFormularioNovoProfessor();
        });

        this.listView.getBtnEditar().addActionListener(e -> {
            abrirFormularioEditarProfessor();
        });

        this.listView.getBtnExcluir().addActionListener(e -> {
            excluirProfessor();
        });

        this.formView.getBtnSalvar().addActionListener(e -> {
            salvarProfessor();
        });

        this.formView.getBtnCancelar().addActionListener(e -> {
            this.formView.dispose();
        });
    }

    private void atualizarTabela(String searchParam) {
        System.out.println("Atualizando tabela de professores...");

        DefaultTableModel tableModel = this.listView.getTableModel();

        tableModel.setRowCount(0);

        List<Professor> professores = this.professorRepo.listarTodos();

        for (Professor professor : professores) {
            Pessoa pessoa = this.pessoaRepo.buscarPorId(professor.getPessoaId()).orElse(null);

            if (pessoa != null) {
                Object[] rowData = {
                        pessoa.getId(),
                        pessoa.getNomeCompleto(),
                        pessoa.getCpf(),
                        pessoa.getEmailContato()
                };
                if(!searchParam.isEmpty()){
                    if(pessoa.getNomeCompleto().contains(searchParam)|| pessoa.getCpf().contains(searchParam)){
                        tableModel.addRow(rowData);
                    }
                    continue;
                }
                tableModel.addRow(rowData);
            }
        }
    }


    private void abrirFormularioNovoProfessor() {
        System.out.println("Botão NOVO clicado");
        formView.limparFormulario();
        formView.setTitle("Novo Professor");
        formView.setVisible(true);
    }

    private void abrirFormularioEditarProfessor() {
        System.out.println("Botão EDITAR clicado");
        int selectedRow = this.listView.getTabelaProfessores().getSelectedRow();

        if (!this.isLinhaSelecionada(selectedRow)) return;

        int pessoaId = (Integer) this.listView.getTableModel().getValueAt(selectedRow, 0);

        System.out.println("ID selecionado: " + pessoaId);


        Pessoa pessoa = this.pessoaRepo.buscarPorId(pessoaId).orElse(null);
        Professor professor = this.professorRepo.buscarPorPessoaId(pessoaId).orElse(null);
        Endereco endereco = null;
        if (pessoa != null) {
            System.out.println("Entrou aqui");
            endereco = this.enderecoRepo.buscarPorId(pessoa.getEnderecoId()).orElse(null);
        }

        if (pessoa == null || endereco == null || professor == null) {
            JOptionPane.showMessageDialog(
                    this.listView,
                    "Erro ao buscar os dados completos do professor (ID: " + pessoaId + ").",
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
        this.formView.setFormacaoAcademica(professor.getFormacaoAcademica());
        this.formView.setCep(endereco.getCep());
        this.formView.setLogradouro(endereco.getLogradouro());
        this.formView.setNumero(endereco.getNumero());
        this.formView.setBairro(endereco.getBairro());
        this.formView.setCidade(endereco.getCidade());
        this.formView.setEstado(endereco.getEstado());

        this.formView.setTitle("Editando Professor: " + pessoa.getNomeCompleto());
        this.formView.setVisible(true); // Abre o popup
    }

    private void excluirProfessor() {
        int selectedRow = this.listView.getTabelaProfessores().getSelectedRow();
        int pessoaId = (Integer) this.listView.getTableModel().getValueAt(selectedRow, 0);

        if(!this.isLinhaSelecionada(selectedRow)){
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(listView, "Tem certeza que deseja excluir esse Professor?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.professorRepo.excluir(pessoaId);
            formView.dispose();
        }
        atualizarTabela("");
    }

    private void salvarProfessor() {
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

        Professor professor = new Professor();
        professor.setPessoaId(newPessoa.getId());
        professor.setFormacaoAcademica(formView.getFormacaoAcademica());
        this.professorRepo.salvar(professor);

        formView.dispose();
        atualizarTabela("");
    }

    private boolean isLinhaSelecionada(int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this.listView,
                    "Por favor, selecione um aluno na tabela para editar.",
                    "Nenhum Aluno Selecionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }
}