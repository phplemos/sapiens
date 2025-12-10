package controllers;

import enums.TipoPerfilUsuario;
import models.*;
import repositories.EnderecoRepository;
import repositories.PessoaRepository;
import repositories.ResponsavelRepository;
import repositories.UsuarioRepository;
import views.ResponsavelFormView;
import views.ResponsavelListView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ResponsavelController {

    private final ResponsavelRepository responsavelRepo;
    private final PessoaRepository pessoaRepo;
    private final EnderecoRepository enderecoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ResponsavelListView listView;
    private final ResponsavelFormView formView;
    private boolean isEditForm = false;

    public ResponsavelController(ResponsavelListView responsavelListView) {
        this.responsavelRepo = new ResponsavelRepository();
        this.pessoaRepo = new PessoaRepository();
        this.enderecoRepo = new EnderecoRepository();
        this.usuarioRepo = new UsuarioRepository();
        this.listView = responsavelListView;
        this.formView = new ResponsavelFormView(listView);
        initController();
    }

    private void initController() {

        atualizarTabela("");
        this.listView.getBtnBuscar().addActionListener(e -> {
            atualizarTabela(listView.getTxtBusca().getText());
        });
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
            boolean isValid = this.formView.validateForm(this.isEditForm);
            if (isValid) {
                if(isEditForm){
                    atualizarResponsavel();
                    this.isEditForm = false;
                    return;
                }
                salvarResponsavel();
            }
        });

        this.formView.getBtnCancelar().addActionListener(e -> {
            this.formView.dispose();
        });
    }

    private void atualizarTabela(String searchParam) {
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


    private void abrirFormularioNovoResponsavel() {
        formView.limparFormulario();
        formView.setTitle("Novo Responsavel");
        formView.setVisible(true);
    }

    private void abrirFormularioEditarResponsavel() {
        this.isEditForm = true;
        int selectedRow = this.listView.getTabelaResponsaveis().getSelectedRow();

        if (!this.isLinhaSelecionada(selectedRow)) return;

        int pessoaId = (Integer) this.listView.getTableModel().getValueAt(selectedRow, 0);

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

        this.formView.limparFormulario();
        this.formView.setPessoaIdParaEdicao(pessoaId);

        this.formView.setNome(pessoa.getNomeCompleto());
        this.formView.setCpf(pessoa.getCpf());
        this.formView.setDataNasc(pessoa.getDataNascimento().toString());
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
        this.formView.setVisible(true);
    }

    private void excluirResponsavel() {
        int selectedRow = this.listView.getTabelaResponsaveis().getSelectedRow();
        int pessoaId = (Integer) this.listView.getTableModel().getValueAt(selectedRow, 0);

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
        atualizarTabela("");
    }

    private void salvarResponsavel() {
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
        newPessoa = this.pessoaRepo.salvar(newPessoa);

        Responsavel responsavel = new Responsavel();
        responsavel.setPessoaId(newPessoa.getId());
        this.responsavelRepo.salvar(responsavel);

        Optional<Usuario> usuarioOpt = usuarioRepo.buscarPorPessoaId(newPessoa.getId());
        if(usuarioOpt.isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setPessoaId(newPessoa.getId());
            usuario.setLogin(newPessoa.getCpf());
            usuario.setSenhaHash("123");
            usuario.setTipoPerfil(TipoPerfilUsuario.RESPONSAVEL);
            usuario.setCriadoEm(LocalDateTime.now());
            this.usuarioRepo.salvar(usuario);
        }

        formView.dispose();
        atualizarTabela("");
    }

    private void atualizarResponsavel() {
        Optional<Pessoa> dadosPessoa = this.pessoaRepo.buscarPorId(this.formView.getPessoaIdParaEdicao());
        Optional<Responsavel> dadosResponsavel = this.responsavelRepo.buscarPorPessoaId(this.formView.getPessoaIdParaEdicao());
        if(dadosResponsavel.isEmpty() || dadosPessoa.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Problema ao buscar Respoonsavel, verifique se preencheu corretamente");
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

        formView.dispose();
        atualizarTabela("");
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