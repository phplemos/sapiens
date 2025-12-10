package controllers;

import enums.TipoPerfilUsuario;
import models.*;
import repositories.EnderecoRepository;
import repositories.PessoaRepository;
import repositories.ProfessorRepository;
import repositories.UsuarioRepository;
import views.ProfessorFormView;
import views.ProfessorListView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ProfessorController {

    private final ProfessorRepository professorRepo;
    private final PessoaRepository pessoaRepo;
    private final EnderecoRepository enderecoRepo;
    private final UsuarioRepository usuarioRepo;

    private final ProfessorListView listView;
    private final ProfessorFormView formView;
    private boolean isEditForm = false;

    public ProfessorController(ProfessorListView listView) {
        this.professorRepo = new ProfessorRepository();
        this.pessoaRepo = new PessoaRepository();
        this.enderecoRepo = new EnderecoRepository();
        this.usuarioRepo = new UsuarioRepository();

        this.listView = listView;
        this.formView = new ProfessorFormView(this.listView);

        initController();
    }


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
            boolean isValid = this.formView.validateForm(this.isEditForm);
            if (isValid) {
                if(isEditForm){
                    atualizarProfessor();
                    this.isEditForm = false;
                    return;
                }
                salvarProfessor();
            }
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
                    if(pessoa.getNomeCompleto().toLowerCase().contains(searchParam.toLowerCase())|| pessoa.getCpf().trim().contains(searchParam.trim())){
                        tableModel.addRow(rowData);
                    }
                    continue;
                }
                tableModel.addRow(rowData);
            }
        }
    }


    private void abrirFormularioNovoProfessor() {
        formView.limparFormulario();
        formView.setTitle("Novo Professor");
        formView.setVisible(true);
    }

    private void abrirFormularioEditarProfessor() {
        isEditForm = true;
        int selectedRow = this.listView.getTabelaProfessores().getSelectedRow();

        if (!this.isLinhaSelecionada(selectedRow)) return;

        int pessoaId = (Integer) this.listView.getTableModel().getValueAt(selectedRow, 0);

        Pessoa pessoa = this.pessoaRepo.buscarPorId(pessoaId).orElse(null);
        Professor professor = this.professorRepo.buscarPorPessoaId(pessoaId).orElse(null);
        Endereco endereco = null;
        if (pessoa != null) {
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

        this.formView.limparFormulario();
        this.formView.setPessoaIdParaEdicao(pessoaId);

        this.formView.setNome(pessoa.getNomeCompleto());
        this.formView.setCpf(pessoa.getCpf());
        this.formView.setDataNasc(pessoa.getDataNascimento().toString());
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
        this.formView.setVisible(true);
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

        Optional<Usuario> usuarioOpt = usuarioRepo.buscarPorPessoaId(newPessoa.getId());
        if(usuarioOpt.isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setPessoaId(newPessoa.getId());
            usuario.setLogin(newPessoa.getEmailContato());
            usuario.setSenhaHash("123");
            usuario.setTipoPerfil(TipoPerfilUsuario.PROFESSOR);
            usuario.setCriadoEm(LocalDateTime.now());
            this.usuarioRepo.salvar(usuario);
        }
        formView.dispose();
        atualizarTabela("");
    }

    private void atualizarProfessor() {
        Optional<Pessoa> dadosPessoa = this.pessoaRepo.buscarPorId(this.formView.getPessoaIdParaEdicao());
        Optional<Professor> dadosProfessor = this.professorRepo.buscarPorPessoaId(this.formView.getPessoaIdParaEdicao());
        if(dadosProfessor.isEmpty() || dadosPessoa.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Problema ao buscar Professor, verifique se preencheu corretamente");
            return;
        }

        Pessoa pessoa = dadosPessoa.get();
        Optional<Endereco> endereco = this.enderecoRepo.buscarPorId(pessoa.getEnderecoId());
        if(endereco.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar o endereço anterior");
            return;
        }
        Endereco enderecoAtualizado = endereco.get();
        enderecoAtualizado.setCep(formView.getCep());
        enderecoAtualizado.setLogradouro(formView.getLogradouro());
        enderecoAtualizado.setNumero(formView.getNumero());
        enderecoAtualizado.setBairro(formView.getBairro());
        enderecoAtualizado.setCidade(formView.getCidade());
        enderecoAtualizado.setEstado(formView.getEstado());
        this.enderecoRepo.editar(enderecoAtualizado);

        pessoa.setCpf(formView.getCpf());
        pessoa.setDataNascimento(LocalDate.parse(formView.getDataNasc()));
        pessoa.setRg(formView.getRg());
        pessoa.setNomeCompleto(formView.getNome());
        pessoa.setEmailContato(formView.getEmail());
        pessoa.setTelefone(formView.getTelefone());
        this.pessoaRepo.editar(pessoa);

        Professor professorAtualizado = dadosProfessor.get();
        professorAtualizado.setFormacaoAcademica(formView.getFormacaoAcademica());
        this.professorRepo.editar(professorAtualizado);

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