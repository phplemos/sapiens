package controllers;

import enums.StatusMatricula;
import enums.TipoPerfilUsuario;
import models.*;
import repositories.*;
import views.components.ComboItem;
import views.MatriculaFormView;
import views.MatriculaListView;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MatriculaController {

    private final MatriculaRepository matriculaRepo;
    private final MatriculaDisciplinaRepository matDisciplinaRepo;
    private final TurmaDisciplinaRepository turmaDisciplinaRepo;
    private final UsuarioRepository usuarioRepo;

    private final AlunoRepository alunoRepo;
    private final PessoaRepository pessoaRepo;
    private final TurmaRepository turmaRepo;

    private final MatriculaListView listView;
    private final MatriculaFormView formView;

    public MatriculaController(MatriculaListView listView) {
        this.matriculaRepo = new MatriculaRepository();
        this.matDisciplinaRepo = new MatriculaDisciplinaRepository();
        this.turmaDisciplinaRepo = new TurmaDisciplinaRepository();
        this.usuarioRepo = new UsuarioRepository();

        this.alunoRepo = new AlunoRepository();
        this.pessoaRepo = new PessoaRepository();
        this.turmaRepo = new TurmaRepository();

        this.listView = listView;
        this.formView = new MatriculaFormView(listView);


        carregarCombos();
        initController();
    }

    private void carregarCombos() {
        List<Aluno> alunos = alunoRepo.listarTodos();
        for (Aluno a : alunos) {
            Pessoa p = pessoaRepo.buscarPorId(a.getPessoaId()).orElse(null);
            if (p != null) {
                formView.adicionarAluno(new ComboItem(a.getPessoaId(), p.getNomeCompleto()));
            }
        }

        List<Turma> turmas = turmaRepo.listarTodos();
        for (Turma t : turmas) {
            formView.adicionarTurma(new ComboItem(t.getId(), t.getNome() + " (" + t.getTurno() + ")"));
        }
    }

    private void initController() {
        atualizarTabela();

        listView.getBtnNovaMatricula().addActionListener(e -> abrirFormNovo());
        listView.getBtnExcluir().addActionListener(e -> cancelarMatricula());

        formView.getBtnSalvar().addActionListener(e -> realizarMatricula());
        formView.getBtnCancelar().addActionListener(e -> formView.dispose());
    }

    private void atualizarTabela() {
        listView.getTableModel().setRowCount(0);
        List<Matricula> lista = matriculaRepo.listarTodos();

        for (Matricula m : lista) {
            String nomeAluno = "Desconhecido";
            Optional<Pessoa> p = pessoaRepo.buscarPorId(m.getAlunoPessoaId());
            if (p.isPresent()) nomeAluno = p.get().getNomeCompleto();

            String nomeTurma = "Desconhecida";
            Optional<Turma> t = turmaRepo.buscarPorId(m.getTurmaId());
            if (t.isPresent()) nomeTurma = t.get().getNome();

            listView.getTableModel().addRow(new Object[]{
                    m.getId(),
                    nomeAluno,
                    nomeTurma,
                    m.getDataMatricula(),
                    m.getNumeroMatricula()
            });
        }
    }

    private void abrirFormNovo() {
        formView.limparFormulario();
        formView.setVisible(true);
    }

    private void realizarMatricula() {
        int alunoId = formView.getAlunoSelecionadoId();
        int turmaId = formView.getTurmaSelecionadaId();

        if (alunoId == 0 || turmaId == 0) {
            JOptionPane.showMessageDialog(formView, "Selecione Aluno e Turma.");
            return;
        }
        Optional<Pessoa> pessoaOpt = this.pessoaRepo.buscarPorId(alunoId);
        if(pessoaOpt.isEmpty()) {
            JOptionPane.showMessageDialog(formView, "Pessoa/Aluno nao cadastrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (matriculaRepo.alunoJaMatriculado(alunoId, turmaId)) {
            JOptionPane.showMessageDialog(formView, "Aluno já está matriculado nesta turma!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(matriculaRepo.alunoJaMatriculadoEmTurmaAtiva(alunoId)){
            JOptionPane.showMessageDialog(formView, "Aluno já está matriculado em outra turma que está ativa!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }


        Matricula m = new Matricula();
        m.setAlunoPessoaId(alunoId);
        m.setTurmaId(turmaId);
        m.setDataMatricula(LocalDate.now());
        m.setStatus(StatusMatricula.ATIVA);

        String numMatricula = LocalDate.now().getYear() + String.format("%04d", alunoId) + turmaId;
        m.setNumeroMatricula(numMatricula);

        m = matriculaRepo.salvar(m);


        System.out.println("Gerando grade curricular para a matrícula " + m.getId() + "...");

        List<TurmaDisciplina> gradeDaTurma = turmaDisciplinaRepo.buscarPorTurmaId(turmaId);

        if (gradeDaTurma.isEmpty()) {
            JOptionPane.showMessageDialog(formView, "Aviso: Esta turma não tem disciplinas cadastradas.\nO aluno foi matriculado, mas sem matérias.");
        }

        for (TurmaDisciplina td : gradeDaTurma) {
            MatriculaDisciplina md = new MatriculaDisciplina();
            md.setMatriculaId(m.getId()); // Vincula a esta matrícula nova
            md.setTurmaDisciplinaId(td.getId()); // Vincula à oferta da disciplina
            md.setTotalFaltas(0);

            matDisciplinaRepo.salvar(md);
            System.out.println(" - Disciplina vinculada: ID " + td.getDisciplinaId());
        }
        Optional<Usuario> usuarioOpt = usuarioRepo.buscarPorPessoaId(alunoId);
        if(usuarioOpt.isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setPessoaId(alunoId);
            usuario.setLogin(pessoaOpt.get().getCpf());
            usuario.setSenhaHash("123");
            usuario.setTipoPerfil(TipoPerfilUsuario.ALUNO);
            usuario.setCriadoEm(LocalDateTime.now());
            this.usuarioRepo.salvar(usuario);
        }

        String titulo = "Matrícula Realizada";
        String corpo = "Bem-vindo! Sua matrícula foi confirmada com sucesso.";
        ComunicadoController.dispararNotificacaoSistema(m.getAlunoPessoaId(), titulo, corpo);

        JOptionPane.showMessageDialog(formView, "Matrícula realizada com sucesso!\nNº: " + numMatricula);
        formView.dispose();
        atualizarTabela();
    }

    private void cancelarMatricula() {
        int row = listView.getTabela().getSelectedRow();
        if (row != -1) {
            int id = (int) listView.getTabela().getValueAt(row, 0);

            List<MatriculaDisciplina> mds = matDisciplinaRepo.buscarPorMatriculaId(id);
            for(MatriculaDisciplina md : mds) {
                matDisciplinaRepo.excluir(md.getId());
            }
            matriculaRepo.excluir(id);

            atualizarTabela();
        }
    }
}