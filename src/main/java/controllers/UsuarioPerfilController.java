package controllers;

import enums.TipoPerfilUsuario;
import models.*;
import repositories.*;
import views.UsuarioPerfilView;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class UsuarioPerfilController {

    private final UsuarioPerfilView view;
    private final Usuario usuarioLogado;

    private final PessoaRepository pessoaRepo = new PessoaRepository();
    private final AlunoRepository alunoRepo = new AlunoRepository();
    private final AlunoResponsavelRepository alunoResponsavelRepo = new AlunoResponsavelRepository();
    private final MatriculaRepository matriculaRepo = new MatriculaRepository();
    private final MatriculaDisciplinaRepository matriculaDisciplinaRepository = new MatriculaDisciplinaRepository();
    private final TurmaRepository turmaRepo = new TurmaRepository();
    private final TurmaDisciplinaRepository tdRepo = new TurmaDisciplinaRepository();
    private final DisciplinaRepository disciplinaRepo = new DisciplinaRepository();
    private final UsuarioRepository usuarioRepo = new UsuarioRepository();

    public UsuarioPerfilController(UsuarioPerfilView view, Usuario usuario) {
        this.view = view;
        this.usuarioLogado = usuario;

        initController();
    }

    private void initController() {
        carregarDadosPessoais();

        switch (usuarioLogado.getTipoPerfil()) {
            case ALUNO:
                view.configurarLayoutAluno();
                carregarDadosAcademicosAluno();
                view.getBtnVerConteudo().addActionListener(e -> verConteudoProgramatico());
                break;
            case PROFESSOR:
                view.configurarLayoutProfessor();
                carregarDadosAcademicosProfessor();
                break;
            case RESPONSAVEL:
                view.configurarLayoutResponsavel();
                carregarDadosResponsavel();
                break;
            case ADMIN:
                view.configurarLayoutAdmin();
                break;
        }

        view.getBtnSair().addActionListener(e -> {
            view.dispose();
        });
        view.getBtnAlterarSenha().addActionListener(e -> abrirDialogoAlterarSenha());
    }
    private void carregarDadosResponsavel() {
        int idResp = usuarioLogado.getPessoaId();
        List<Integer> idsFilhos = alunoResponsavelRepo.buscarIdsAlunosDoResponsavel(idResp);

        view.getModelFilhosResponsavel().setRowCount(0);

        if (idsFilhos.isEmpty()) {
            view.getModelFilhosResponsavel().addRow(new Object[]{"Nenhum dependente encontrado", "-", "-"});
            return;
        }

        for (Integer idFilho : idsFilhos) {
            Optional<Pessoa> filhoPessoa = pessoaRepo.buscarPorId(idFilho);

            if (filhoPessoa.isPresent()) {
                String nome = filhoPessoa.get().getNomeCompleto();
                String matricula = "-";
                String turma = "Sem turma";

                Optional<Matricula> matOpt = matriculaRepo.listarTodos().stream()
                        .filter(m -> m.getAlunoPessoaId() == idFilho)
                        .max(Comparator.comparingInt(Matricula::getId));

                if (matOpt.isPresent()) {
                    matricula = matOpt.get().getNumeroMatricula();
                    Optional<Turma> tOpt = turmaRepo.buscarPorId(matOpt.get().getTurmaId());
                    if (tOpt.isPresent()) turma = tOpt.get().getNome();
                }

                view.getModelFilhosResponsavel().addRow(new Object[]{nome, matricula, turma});
            }
        }
    }

    private void abrirDialogoAlterarSenha() {
        JPasswordField pfAtual = new JPasswordField();
        JPasswordField pfNova = new JPasswordField();
        JPasswordField pfConfirmar = new JPasswordField();

        Object[] message = {
                "Senha Atual:", pfAtual,
                "Nova Senha:", pfNova,
                "Confirmar Nova Senha:", pfConfirmar
        };

        int option = JOptionPane.showConfirmDialog(view, message, "Alterar Senha", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String atual = new String(pfAtual.getPassword());
            String nova = new String(pfNova.getPassword());
            String confirmacao = new String(pfConfirmar.getPassword());
            if (!usuarioLogado.getSenhaHash().equals(atual)) {
                JOptionPane.showMessageDialog(view, "Senha atual incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (nova.length() < 4) {
                JOptionPane.showMessageDialog(view, "A nova senha deve ter pelo menos 4 caracteres.", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!nova.equals(confirmacao)) {
                JOptionPane.showMessageDialog(view, "A confirmação da senha não confere.", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            usuarioLogado.setSenhaHash(nova);
            this.usuarioRepo.editar(usuarioLogado);

            JOptionPane.showMessageDialog(view, "Senha alterada com sucesso!");
        }
    }

    private void carregarDadosAcademicosAluno() {
        int pessoaId = usuarioLogado.getPessoaId();

        Optional<Matricula> matOpt = matriculaRepo.listarTodos().stream()
                .filter(m -> m.getAlunoPessoaId() == pessoaId)
                .findFirst();

        if (matOpt.isPresent()) {
            Matricula mat = matOpt.get();
            int turmaId = mat.getTurmaId();

            Optional<Turma> tOpt = turmaRepo.buscarPorId(turmaId);
            if (tOpt.isPresent()) {
                view.setTurmaAtualAluno(tOpt.get().getNome() + " (" + tOpt.get().getTurno() + ")");
            }

            view.getModelDisciplinasAluno().setRowCount(0); // Limpa

            List<MatriculaDisciplina> grade = matriculaDisciplinaRepository.buscarPorMatriculaId(mat.getId()); // Usei o método que criamos antes

            for (MatriculaDisciplina md : grade) {
                Optional<TurmaDisciplina> tdOpt = tdRepo.buscarPorId(md.getTurmaDisciplinaId());

                if (tdOpt.isPresent()) {
                    Optional<Disciplina> dOpt = disciplinaRepo.buscarPorId(tdOpt.get().getDisciplinaId());

                    if (dOpt.isPresent()) {
                        Disciplina d = dOpt.get();
                        view.getModelDisciplinasAluno().addRow(new Object[]{
                                d.getId(),       // ID para buscar o conteúdo depois
                                d.getNome(),
                                d.getCargaHoraria() + "h"
                        });
                    }
                }
            }
        } else {
            view.setTurmaAtualAluno("Você não está matriculado.");
        }
    }

    private void verConteudoProgramatico() {
        int row = view.getTabelaDisciplinasAluno().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione uma disciplina na tabela.");
            return;
        }

        int disciplinaId = (int) view.getModelDisciplinasAluno().getValueAt(row, 0);

        Optional<Disciplina> dOpt = disciplinaRepo.buscarPorId(disciplinaId);
        if (dOpt.isPresent()) {
            Disciplina d = dOpt.get();

            JTextArea textArea = new JTextArea(15, 40);
            textArea.setText(d.getConteudoProgramatico());
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setCaretPosition(0);

            JOptionPane.showMessageDialog(view, new JScrollPane(textArea),
                    "Conteúdo: " + d.getNome(),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void carregarDadosPessoais() {
        if (usuarioLogado.getTipoPerfil() == TipoPerfilUsuario.ADMIN && usuarioLogado.getPessoaId() == 0) {
            view.setDadosPessoais("ADMINISTRADOR", "Super Usuário", "-", usuarioLogado.getLogin(), "-");
            return;
        }

        Optional<Pessoa> pOpt = pessoaRepo.buscarPorId(usuarioLogado.getPessoaId());
        if (pOpt.isPresent()) {
            Pessoa p = pOpt.get();
            view.setDadosPessoais(
                    usuarioLogado.getTipoPerfil().toString(),
                    p.getNomeCompleto(),
                    p.getCpf(),
                    p.getEmailContato(),
                    p.getTelefone()
            );
        }
    }

    private void carregarDadosAcademicosProfessor() {
        int pessoaId = usuarioLogado.getPessoaId();

        List<TurmaDisciplina> aulas = tdRepo.listarTodos().stream()
                .filter(td -> td.getProfessorPessoaId() == pessoaId)
                .toList();

        for (TurmaDisciplina aula : aulas) {
            String nomeTurma = "N/A";
            String nomeDisciplina = "N/A";
            String turno = "-";

            Optional<Turma> tOpt = turmaRepo.buscarPorId(aula.getTurmaId());
            if (tOpt.isPresent()) {
                nomeTurma = tOpt.get().getNome();
                turno = tOpt.get().getTurno().toString();
            }

            Optional<Disciplina> dOpt = disciplinaRepo.buscarPorId(aula.getDisciplinaId());
            if (dOpt.isPresent()) {
                nomeDisciplina = dOpt.get().getNome();
            }

            view.getModelProfessor().addRow(new Object[]{
                    nomeTurma,
                    nomeDisciplina,
                    turno
            });
        }
    }
}