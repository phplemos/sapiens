package controllers;

import enums.TipoPerfilUsuario;
import models.*;
import repositories.*;
import views.UsuarioPerfilView;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public class UsuarioPerfilController {

    private final UsuarioPerfilView view;
    private final Usuario usuarioLogado;

    // Repositórios
    private final PessoaRepository pessoaRepo = new PessoaRepository();
    private final AlunoRepository alunoRepo = new AlunoRepository();
    private final ProfessorRepository profRepo = new ProfessorRepository();
    private final MatriculaRepository matriculaRepo = new MatriculaRepository();
    private final MatriculaDisciplinaRepository matriculaDisciplinaRepository = new MatriculaDisciplinaRepository();
    private final TurmaRepository turmaRepo = new TurmaRepository();
    private final TurmaDisciplinaRepository tdRepo = new TurmaDisciplinaRepository();
    private final DisciplinaRepository disciplinaRepo = new DisciplinaRepository();

    public UsuarioPerfilController(UsuarioPerfilView view, Usuario usuario) {
        this.view = view;
        this.usuarioLogado = usuario;

        initController();
    }

    private void initController() {
        // 1. Carregar Dados Pessoais (Comuns a todos, exceto Admin puro)
        carregarDadosPessoais();

        // 2. Configurar Layout Específico
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
            case ADMIN:
                view.configurarLayoutAdmin();
                break;
            default:
                break;
        }

        // 3. Ação de Sair
        view.getBtnSair().addActionListener(e -> {
            view.dispose();
        });
    }
    private void carregarDadosAcademicosAluno() {
        int pessoaId = usuarioLogado.getPessoaId();

        // 1. Busca a matrícula ativa
        Optional<Matricula> matOpt = matriculaRepo.listarTodos().stream()
                .filter(m -> m.getAlunoPessoaId() == pessoaId)
                .findFirst();

        if (matOpt.isPresent()) {
            Matricula mat = matOpt.get();
            int turmaId = mat.getTurmaId();

            // Set nome da turma
            Optional<Turma> tOpt = turmaRepo.buscarPorId(turmaId);
            if (tOpt.isPresent()) {
                view.setTurmaAtualAluno(tOpt.get().getNome() + " (" + tOpt.get().getTurno() + ")");
            }

            // 2. Preencher a Tabela de Disciplinas
            view.getModelDisciplinasAluno().setRowCount(0); // Limpa

            // Busca o que o aluno cursa (MatriculaDisciplina)
            List<MatriculaDisciplina> grade = matriculaDisciplinaRepository.buscarPorMatriculaId(mat.getId()); // Usei o método que criamos antes

            for (MatriculaDisciplina md : grade) {
                // Acha a oferta (TurmaDisciplina)
                Optional<TurmaDisciplina> tdOpt = tdRepo.buscarPorId(md.getTurmaDisciplinaId());

                if (tdOpt.isPresent()) {
                    // Acha a disciplina real
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

    /**
     * Lógica do RF027: Exibir o conteúdo programático em um Popup
     */
    private void verConteudoProgramatico() {
        int row = view.getTabelaDisciplinasAluno().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione uma disciplina na tabela.");
            return;
        }

        // Pega o ID da disciplina (coluna 0 oculta)
        int disciplinaId = (int) view.getModelDisciplinasAluno().getValueAt(row, 0);

        Optional<Disciplina> dOpt = disciplinaRepo.buscarPorId(disciplinaId);
        if (dOpt.isPresent()) {
            Disciplina d = dOpt.get();

            // Cria uma área de texto para exibir bonito
            JTextArea textArea = new JTextArea(15, 40);
            textArea.setText(d.getConteudoProgramatico());
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setCaretPosition(0);

            // Mostra no JOptionPane
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

    /**
     * Lógica para PROFESSOR: Busca as disciplinas que ele leciona.
     */
    private void carregarDadosAcademicosProfessor() {
        int pessoaId = usuarioLogado.getPessoaId();

        // Busca onde o professor está alocado na tabela TurmaDisciplina
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