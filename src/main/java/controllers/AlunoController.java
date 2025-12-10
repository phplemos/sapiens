package controllers;

import models.*;
import repositories.*;
import views.AlunoHistoricoView;
import views.AlunoListView;
import views.AlunoFormView;
import views.components.ComboItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class AlunoController {

    private final AlunoRepository alunoRepo;
    private final ResponsavelRepository responsavelRepo;
    private final AlunoResponsavelRepository alunoResponsavelRepo;
    private final PessoaRepository pessoaRepo;
    private final EnderecoRepository enderecoRepo;

    private final AlunoListView listView;
    private final AlunoFormView formView;
    private boolean isEditForm = false;

    public AlunoController(AlunoListView listView) {
        this.alunoRepo = new AlunoRepository();
        this.responsavelRepo = new ResponsavelRepository();
        this.alunoResponsavelRepo = new AlunoResponsavelRepository();
        this.pessoaRepo = new PessoaRepository();
        this.enderecoRepo = new EnderecoRepository();

        this.listView = listView;

        this.formView = new AlunoFormView(this.listView);
        carregarComboResponsaveis();
        initController();
    }

    private void carregarComboResponsaveis() {
        List<Responsavel> responsaveis = this.responsavelRepo.listarTodos();

        this.formView.adicionarResponsavelCombo(new ComboItem(0, "Selecione um responsável..."));

        for (Responsavel resp : responsaveis) {
            Pessoa p = this.pessoaRepo.buscarPorId(resp.getPessoaId()).orElse(null);

            if (p != null) {
                this.formView.adicionarResponsavelCombo(new ComboItem(p.getId(), p.getNomeCompleto()));
            }
        }
    }

    private void initController() {
        atualizarTabela("");
        this.listView.getBtnBuscar().addActionListener(e -> {
            atualizarTabela(listView.getTxtBusca().getText());
        });

        this.listView.getBtnNovo().addActionListener(e -> {
            abrirFormularioNovoAluno();
        });

        this.listView.getBtnEditar().addActionListener(e -> {
            abrirFormularioEditarAluno();
        });

        this.listView.getBtnExcluir().addActionListener(e -> {
            excluirAluno();
        });

        this.listView.getBtnHistorico().addActionListener(e -> {
            abrirHistorico();
        });
        this.formView.getBtnSalvar().addActionListener(e -> {
            boolean isValid = this.formView.validateForm(this.isEditForm);
            if (isValid) {
                if(isEditForm){
                    atualizarAluno();
                    isEditForm = false;
                    return;
                }
                salvarAluno();
            }
        });


        this.formView.getBtnCancelar().addActionListener(e -> {
            this.formView.dispose();
        });
    }

    private void atualizarTabela(String searchParam) {
        System.out.println("Atualizando tabela de alunos...");

        DefaultTableModel tableModel = this.listView.getTableModel();

        tableModel.setRowCount(0);

        List<Aluno> alunos = this.alunoRepo.listarTodos();

        for (Aluno aluno : alunos) {
            Pessoa pessoa = this.pessoaRepo.buscarPorId(aluno.getPessoaId()).orElse(null);

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


    private void abrirFormularioNovoAluno() {
        System.out.println("Botão NOVO clicado");
        formView.limparFormulario();
        formView.setTitle("Novo Aluno");
        formView.setVisible(true);
    }

    private void abrirFormularioEditarAluno() {
        isEditForm = true;
        int selectedRow = this.listView.getTabelaAlunos().getSelectedRow();

        if (!this.isLinhaSelecionada(selectedRow)) return;

        int pessoaId = (Integer) this.listView.getTableModel().getValueAt(selectedRow, 0);

        Pessoa pessoa = this.pessoaRepo.buscarPorId(pessoaId).orElse(null);

        models.Endereco endereco = null;
        if (pessoa != null) {
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

        this.formView.setTitle("Editando Aluno: " + pessoa.getNomeCompleto());
        List<AlunoResponsavel> vinculos = this.alunoResponsavelRepo.buscarPorAlunoId(pessoaId);

        if (!vinculos.isEmpty()) {
            // Pega o ID do responsável do primeiro vínculo encontrado
            int idResponsavelVinculado = vinculos.get(0).getResponsavelPessoaId();
            // Manda a View selecionar esse ID no dropdown
            this.formView.setResponsavelSelecionado(idResponsavelVinculado);
        } else {
            this.formView.setResponsavelSelecionado(0); // Nenhum
        }
        this.formView.setVisible(true); // Abre o popup
    }

    private void excluirAluno() {
        int selectedRow = this.listView.getTabelaAlunos().getSelectedRow();
        int pessoaId = (Integer) this.listView.getTableModel().getValueAt(selectedRow, 0);

        if(!this.isLinhaSelecionada(selectedRow)){
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(listView, "Tem certeza que deseja excluir esse Aluno?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.alunoRepo.excluir(pessoaId);
            formView.dispose();
        }
        atualizarTabela("");
    }


    private void salvarAluno() {
        System.out.println("Botão SALVAR (do form) clicado");
        Optional<Pessoa> responsavel = this.pessoaRepo.buscarPorId(this.formView.getResponsavelSelecionadoId());
        if(responsavel.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Problema ao buscar responsavel, verifique se preencheu corretamente");
            return;
        }
        Pessoa newPessoa = new Pessoa();
        newPessoa.setCpf(formView.getCpf());
        newPessoa.setDataNascimento(LocalDate.parse(formView.getDataNasc()));
        newPessoa.setRg(formView.getRg());
        newPessoa.setNomeCompleto(formView.getNome());
        newPessoa.setEmailContato(formView.getEmail());
        newPessoa.setTelefone(formView.getTelefone());
        newPessoa.setEnderecoId(responsavel.get().getEnderecoId());
        this.pessoaRepo.salvar(newPessoa);

        Aluno aluno = new Aluno();
        aluno.setPessoaId(newPessoa.getId());
        this.alunoRepo.salvar(aluno);
        int idResponsavel = this.formView.getResponsavelSelecionadoId();

        if (idResponsavel > 0) {
            AlunoResponsavel vinculo = new AlunoResponsavel();
            vinculo.setAlunoPessoaId(aluno.getPessoaId()); // ID do aluno recém salvo
            vinculo.setResponsavelPessoaId(idResponsavel); // ID do combo selecionado

            this.alunoResponsavelRepo.salvar(vinculo);

            System.out.println("Vínculo criado: Aluno " + aluno.getPessoaId() + " <-> Resp " + idResponsavel);
        }
        formView.dispose();
        atualizarTabela("");
    }

    private void atualizarAluno() {
        Optional<Pessoa> dadosPessoa = this.pessoaRepo.buscarPorId(this.formView.getPessoaIdParaEdicao());
        Optional<Aluno> dadosAluno = this.alunoRepo.buscarPorPessoaId(this.formView.getPessoaIdParaEdicao());
        if(dadosAluno.isEmpty() || dadosPessoa.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Problema ao buscar aluno, verifique se preencheu corretamente");
            return;
        }
        Optional<Pessoa> responsavel = this.pessoaRepo.buscarPorId(this.formView.getResponsavelSelecionadoId());
        if(responsavel.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Problema ao buscar responsavel, verifique se preencheu corretamente");
            return;
        }

        Pessoa pessoa = dadosPessoa.get();
        pessoa.setCpf(formView.getCpf());
        pessoa.setDataNascimento(LocalDate.parse(formView.getDataNasc()));
        pessoa.setRg(formView.getRg());
        pessoa.setNomeCompleto(formView.getNome());
        pessoa.setEmailContato(formView.getEmail());
        pessoa.setTelefone(formView.getTelefone());
        pessoa.setEnderecoId(responsavel.get().getEnderecoId());
        this.pessoaRepo.editar(pessoa);

        int idResponsavel = this.formView.getResponsavelSelecionadoId();

        if (idResponsavel > 0) {
            AlunoResponsavel vinculo = new AlunoResponsavel();
            vinculo.setAlunoPessoaId(dadosAluno.get().getPessoaId()); // ID do aluno recém salvo
            vinculo.setResponsavelPessoaId(idResponsavel); // ID do combo selecionado

            this.alunoResponsavelRepo.salvar(vinculo);

            System.out.println("Vínculo criado: Aluno " + dadosAluno.get().getPessoaId() + " <-> Resp " + idResponsavel);
        }
        formView.dispose();
        atualizarTabela("");
    }

    private void abrirHistorico() {
        int row = listView.getTabelaAlunos().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(listView, "Selecione um aluno.");
            return;
        }

        int pessoaId = (int) listView.getTableModel().getValueAt(row, 0);

        // Abre a tela de histórico
        AlunoHistoricoView histView = new AlunoHistoricoView(listView);
        new AlunoHistoricoController(histView, pessoaId, true);
        histView.setVisible(true);
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