package controllers.alunos;

import models.Aluno;
import models.Endereco;
import models.Pessoa;
import repositories.AlunoRepository;
import repositories.PessoaRepository;
import repositories.EnderecoRepository;
import views.alunos.AlunoListView;
import views.alunos.AlunoFormView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.List;

public class AlunoController {

    private final AlunoRepository alunoRepo;
    private final PessoaRepository pessoaRepo;
    private final EnderecoRepository enderecoRepo;

    private final AlunoListView listView;
    private AlunoFormView formView; // O formulário de popup

    public AlunoController(AlunoListView listView) {
        this.alunoRepo = new AlunoRepository();
        this.pessoaRepo = new PessoaRepository();
        this.enderecoRepo = new EnderecoRepository();

        this.listView = listView;

        this.formView = new AlunoFormView(this.listView);

        initController();
    }

    /**
     * Ponto de entrada do Controller.
     * Carrega os dados iniciais e configura os eventos (cliques de botão).
     */
    private void initController() {
        atualizarTabela();

        this.listView.getBtnNovo().addActionListener(e -> {
            abrirFormularioNovoAluno();
        });

        this.listView.getBtnEditar().addActionListener(e -> {
            abrirFormularioEditarAluno();
        });

        this.listView.getBtnExcluir().addActionListener(e -> {
            excluirAluno();
        });

        this.formView.getBtnSalvar().addActionListener(e -> {
            salvarAluno();
        });

        this.formView.getBtnCancelar().addActionListener(e -> {
            this.formView.dispose();
        });
    }

    private void atualizarTabela() {
        System.out.println("Atualizando tabela de alunos...");

        DefaultTableModel tableModel = this.listView.getTableModel();

        tableModel.setRowCount(0);

        List<Aluno> alunos = this.alunoRepo.listarTodos();

        for (Aluno aluno : alunos) {
            Pessoa pessoa = this.pessoaRepo.buscarPorId(aluno.getPessoaId()).orElse(null);

            if (pessoa != null) {
                Object[] rowData = {
                        pessoa.getId(), // Coluna "ID"
                        pessoa.getNomeCompleto(), // Coluna "Nome Completo"
                        pessoa.getCpf(), // Coluna "CPF"
                        pessoa.getEmailContato() // Coluna "Email"
                };

                tableModel.addRow(rowData);
            }
        }
    }


    private void abrirFormularioNovoAluno() {
        System.out.println("Botão NOVO clicado");
        formView.limparFormulario();
        formView.setTitle("Novo Aluno");
        formView.setVisible(true);
    }

    private void abrirFormularioEditarAluno() {
        System.out.println("Botão EDITAR clicado");
        int selectedRow = this.listView.getTabelaAlunos().getSelectedRow();

        if (!this.isLinhaSelecionada(selectedRow)) return;

        int pessoaId = (Integer) this.listView.getTableModel().getValueAt(selectedRow, 0);

        System.out.println("ID selecionado: " + pessoaId);


        Pessoa pessoa = this.pessoaRepo.buscarPorId(pessoaId).orElse(null);

        models.Endereco endereco = null;
        if (pessoa != null) {
            System.out.println("Entrou aqui");
            endereco = this.enderecoRepo.buscarPorId(pessoa.getEnderecoId()).orElse(null);
        }

        if (pessoa == null || endereco == null) {
            JOptionPane.showMessageDialog(
                    this.listView,
                    "Erro ao buscar os dados completos do aluno (ID: " + pessoaId + ").",
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

        this.formView.setTitle("Editando Aluno: " + pessoa.getNomeCompleto());
        this.formView.setVisible(true); // Abre o popup
    }

    private void excluirAluno() {
        int selectedRow = this.listView.getTabelaAlunos().getSelectedRow();
        int pessoaId = (Integer) this.listView.getTableModel().getValueAt(selectedRow, 0);

        if(!this.isLinhaSelecionada(selectedRow)){
            return;
        }

        this.alunoRepo.excluir(pessoaId);
        formView.dispose();
        atualizarTabela();
    }

    private void salvarAluno() {
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

        Aluno aluno = new Aluno();
        aluno.setPessoaId(newPessoa.getId());
        this.alunoRepo.salvar(aluno);

        formView.dispose();
        atualizarTabela();
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