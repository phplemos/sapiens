package controllers;

import models.*;
import repositories.*;
import views.components.ComboItem;
import views.NotaLancamentoView;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public class GestaoNotasAdminController {

    private final NotaLancamentoView view;

    private final TurmaRepository turmaRepo;
    private final TurmaDisciplinaRepository turmaDiscRepo;
    private final DisciplinaRepository disciplinaRepo;
    private final PeriodoLetivoRepository periodoRepo;
    private final MatriculaRepository matriculaRepo;
    private final MatriculaDisciplinaRepository matDiscRepo;
    private final PessoaRepository pessoaRepo;
    private final NotaRepository notaRepo;
    private final AlunoRepository alunoRepo;
    private final BoletimStatusRepository boletimRepo;

    public GestaoNotasAdminController(NotaLancamentoView view) {
        this.view = view;

        this.turmaRepo = new TurmaRepository();
        this.turmaDiscRepo = new TurmaDisciplinaRepository();
        this.disciplinaRepo = new DisciplinaRepository();
        this.periodoRepo = new PeriodoLetivoRepository();
        this.matriculaRepo = new MatriculaRepository();
        this.matDiscRepo = new MatriculaDisciplinaRepository();
        this.pessoaRepo = new PessoaRepository();
        this.notaRepo = new NotaRepository();
        this.alunoRepo = new AlunoRepository();
        this.boletimRepo = new BoletimStatusRepository();

        initController();
    }

    private void initController() {
        carregarComboTurmas();

        view.getCbTurma().addActionListener(e -> aoSelecionarTurma());

        // Botões
        view.getBtnBuscar().addActionListener(e -> listarAlunosENotas());
        view.getBtnSalvarCorrecao().addActionListener(e -> salvarNotas());
        view.getBtnPublicar().addActionListener(e -> publicarBoletim());
    }
    private void publicarBoletim() {
        int turmaId = view.getTurmaSelecionadaId();
        int periodoId = view.getPeriodoSelecionadoId();

        if (turmaId == 0 || periodoId == 0) {
            JOptionPane.showMessageDialog(view, "Selecione Turma e Período.");
            return;
        }

        // Verifica se já está publicado
        boolean jaPublicado = boletimRepo.isPublicado(turmaId, periodoId);

        if (jaPublicado) {
            int confirm = JOptionPane.showConfirmDialog(view,
                    "Este boletim já está publicado. Deseja OCULTAR as notas novamente?",
                    "Despublicar", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boletimRepo.registrarPublicacao(turmaId, periodoId, false); // False = Oculto
                JOptionPane.showMessageDialog(view, "Boletim ocultado.");
            }
        } else {
            int confirm = JOptionPane.showConfirmDialog(view,
                    "Isso tornará as notas visíveis para todos os alunos da turma.\nConfirma?",
                    "Publicar Boletim", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boletimRepo.registrarPublicacao(turmaId, periodoId, true); // True = Visível
                JOptionPane.showMessageDialog(view, "Boletim publicado com sucesso!");
            }
        }
    }
    private void carregarComboTurmas() {
        List<Turma> turmas = turmaRepo.listarTodos();
        view.getCbTurma().removeAllItems();
        view.adicionarTurma(new ComboItem(0, "Selecione..."));

        for (Turma t : turmas) {
            view.adicionarTurma(new ComboItem(t.getId(), t.getNome()));
        }
    }

    private void aoSelecionarTurma() {
        int turmaId = view.getTurmaSelecionadaId();
        view.limparDisciplinas();
        view.limparPeriodos();

        if (turmaId == 0) return;

        // 1. Carregar Disciplinas Ofertadas na Turma
        List<TurmaDisciplina> ofertas = turmaDiscRepo.buscarPorTurmaId(turmaId);
        for (TurmaDisciplina td : ofertas) {
            Optional<Disciplina> d = disciplinaRepo.buscarPorId(td.getDisciplinaId());
            d.ifPresent(disciplina ->
                    view.adicionarDisciplina(new ComboItem(disciplina.getId(), disciplina.getNome()))
            );
        }

        // 2. Carregar Períodos (Baseado no Ano Escolar da Turma)
        Optional<Turma> turmaOpt = turmaRepo.buscarPorId(turmaId);
        if (turmaOpt.isPresent()) {
            int anoId = turmaOpt.get().getAnoEscolarId();
            List<PeriodoLetivo> periodos = periodoRepo.buscarPorAnoEscolarId(anoId);
            for (PeriodoLetivo p : periodos) {
                view.adicionarPeriodo(new ComboItem(p.getId(), p.getNome()));
            }
        }
    }

    private void listarAlunosENotas() {
        int turmaId = view.getTurmaSelecionadaId();
        int disciplinaId = view.getDisciplinaSelecionadaId();
        int periodoId = view.getPeriodoSelecionadoId();

        if (turmaId == 0 || disciplinaId == 0 || periodoId == 0) {
            JOptionPane.showMessageDialog(view, "Selecione Turma, Disciplina e Período.");
            return;
        }

        view.getTableModel().setRowCount(0);

        // A. Busca todas as matrículas da turma
        List<Matricula> matriculas = matriculaRepo.listarTodos().stream()
                .filter(m -> m.getTurmaId() == turmaId)
                .toList();

        if (matriculas.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhum aluno matriculado nesta turma.");
            return;
        }

        // B. Para cada aluno, busca o vínculo com a disciplina e a nota existente
        for (Matricula m : matriculas) {

            // 1. Pega o Nome do Aluno
            String nomeAluno = "Desconhecido";
            Optional<Aluno> alunoOpt = alunoRepo.buscarPorPessoaId(m.getAlunoPessoaId()); // Busca pelo Aluno->Pessoa
            if (alunoOpt.isPresent()) {
                // Busca a Pessoa para pegar o nome
                Optional<Pessoa> p = pessoaRepo.buscarPorId(alunoOpt.get().getPessoaId());
                if(p.isPresent()) nomeAluno = p.get().getNomeCompleto();
            }

            // 2. Acha o MatriculaDisciplina correspondente a essa Disciplina
            // Precisamos encontrar qual é o ID da oferta da turma para essa disciplina
            Optional<TurmaDisciplina> oferta = turmaDiscRepo.buscarPorTurmaId(turmaId).stream()
                    .filter(td -> td.getDisciplinaId() == disciplinaId)
                    .findFirst();

            if (oferta.isEmpty()) continue; // Algo errado, essa disciplina não é da turma

            int ofertaId = oferta.get().getId();

            // Agora busca o vínculo do aluno com essa oferta
            Optional<MatriculaDisciplina> mdOpt = matDiscRepo.buscarPorMatriculaId(m.getId()).stream()
                    .filter(md -> md.getTurmaDisciplinaId() == ofertaId)
                    .findFirst();

            if (mdOpt.isPresent()) {
                int matriculaDisciplinaId = mdOpt.get().getId();
                int totalFaltas = mdOpt.get().getTotalFaltas();
                // 3. Busca se já existe NOTA lançada
                Optional<Nota> notaOpt = notaRepo.buscarNota(matriculaDisciplinaId, periodoId);
                String valorNota = "";
                if (notaOpt.isPresent()) {
                    valorNota = String.valueOf(notaOpt.get().getValorNota());
                }

                // 4. Adiciona na tabela: [ID_OCULTO, NOME, NOTA]
                view.getTableModel().addRow(new Object[]{
                        matriculaDisciplinaId, // Coluna 0 (Oculta)
                        nomeAluno,             // Coluna 1
                        valorNota,
                        totalFaltas// Coluna 2 (Editável)
                });
            }
        }
    }

    private void salvarNotas() {
        // Para edição, precisamos parar a edição da célula se o usuário ainda estiver digitando
        if (view.getTabela().isEditing()) {
            view.getTabela().getCellEditor().stopCellEditing();
        }

        int periodoId = view.getPeriodoSelecionadoId();
        if (periodoId == 0) return;

        int linhas = view.getTableModel().getRowCount();
        int notasSalvas = 0;

        for (int i = 0; i < linhas; i++) {
            try {
                // Pega os dados da tabela
                int matriculaDisciplinaId = (int) view.getTableModel().getValueAt(i, 0);
                Object valorObj = view.getTableModel().getValueAt(i, 2); // Coluna da nota

                String valorStr = (valorObj == null) ? "" : valorObj.toString().trim();

                // Se estiver vazio, ignora (ou poderia deletar a nota se existisse)
                if (valorStr.isEmpty()) continue;

                // Converte para double (aceita ponto ou vírgula)
                double valor = Double.parseDouble(valorStr.replace(",", "."));

                if (valor < 0 || valor > 10) {
                    throw new NumberFormatException("Nota inválida");
                }

                // Verifica se já existe nota para atualizar ou criar nova
                Optional<Nota> notaExistente = notaRepo.buscarNota(matriculaDisciplinaId, periodoId);

                Nota nota;
                if (notaExistente.isPresent()) {
                    nota = notaExistente.get();
                    nota.setValorNota(valor);
                    notaRepo.editar(nota);
                } else {
                    nota = new Nota();
                    nota.setMatriculaDisciplinaId(matriculaDisciplinaId);
                    nota.setPeriodoLetivoId(periodoId);
                    nota.setValorNota(valor);
                    notaRepo.salvar(nota);
                }
                notasSalvas++;

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view,
                        "Erro na linha " + (i + 1) + ": Nota inválida.\nUse números entre 0 e 10.",
                        "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return; // Para tudo se tiver erro
            }
        }

        JOptionPane.showMessageDialog(view, notasSalvas + " notas salvas com sucesso!");
    }
}
