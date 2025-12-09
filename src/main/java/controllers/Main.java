package controllers;

import enums.TurnoTurma;
import models.*;
import repositories.*;
import views.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Runnable responsavel = () -> {
            // Garante que o Swing rode na Thread de Eventos
            SwingUtilities.invokeLater(() -> {

                // 1. Cria a View (A janela)
                ResponsavelListView view = new ResponsavelListView();

                // 2. Cria o Controller (A lógica)
                ResponsavelController controller = new ResponsavelController(view);

                // 3. Exibe a View.
                view.setVisible(true);
            });
        };
        Runnable professor = () -> {
            SwingUtilities.invokeLater(() -> {

                // 1. Cria a View (A janela)
                ProfessorListView view = new ProfessorListView();

                // 2. Cria o Controller (A lógica)
                ProfessorController controller = new ProfessorController(view);

                // 3. Exibe a View.
                view.setVisible(true);
            });
        };
        Runnable disciplina = () -> {
            SwingUtilities.invokeLater(() -> {
                System.out.println("--- Iniciando Teste do Módulo de Disciplinas ---");
                // 1. Cria a View (A janela da lista)
                DisciplinaListView view = new DisciplinaListView();

                // 2. Cria o Controller (A lógica)
                new DisciplinaController(view);

                // 3. Exibe a janela
                view.setVisible(true);

                System.out.println("Janela aberta. Verifique a pasta 'data/disciplinas.json' após salvar.");
            });
        };
        Runnable alunos = ()-> {
            SwingUtilities.invokeLater(() -> {

            // 1. Cria a View (A janela)
            AlunoListView view = new AlunoListView();

            // 2. Cria o Controller (A lógica) e passa a View para ele no construtor.
            AlunoController controller = new AlunoController(view);

            // 3. Exibe a View.
            // O construtor do controller já foi executado e o método 'atualizarTabela()' já foi chamado.
            view.setVisible(true);
        });

        };
        Runnable anoEscolarESeries = ()-> {
            SwingUtilities.invokeLater(() -> {
                // Cria uma janelinha de menu para você escolher o que testar
                JFrame menu = new JFrame("Teste de Configurações");
                menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                menu.setSize(400, 200);
                menu.setLocationRelativeTo(null);
                menu.setLayout(new GridLayout(2, 1, 10, 10)); // 2 botões empilhados

                // --- BOTÃO 1: Testar Anos Escolares ---
                JButton btnAnos = new JButton("📂 Gerenciar Anos Escolares (2025...)");
                btnAnos.setFont(new Font("Arial", Font.BOLD, 14));

                btnAnos.addActionListener(e -> {
                    // 1. Cria a View
                    AnoEscolarListView view = new AnoEscolarListView();
                    // 2. Conecta o Controller
                    new AnoEscolarController(view);
                    // 3. Mostra a tela
                    view.setVisible(true);
                });

                // --- BOTÃO 2: Testar Séries ---
                JButton btnSeries = new JButton("📚 Gerenciar Séries (1º Ano...)");
                btnSeries.setFont(new Font("Arial", Font.BOLD, 14));

                btnSeries.addActionListener(e -> {
                    SerieListView view = new SerieListView();
                    new SerieController(view);
                    view.setVisible(true);
                });

                // Adiciona botões ao menu e exibe
                // Adiciona padding (margem interna)
                JPanel painel = new JPanel(new GridLayout(2, 1, 10, 10));
                painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                painel.add(btnAnos);
                painel.add(btnSeries);
                JButton btnPeriodos = new JButton("📅 Gerenciar Períodos (Bimestres)");
                btnPeriodos.setFont(new Font("Arial", Font.BOLD, 14));

                btnPeriodos.addActionListener(e -> {
                    PeriodoLetivoListView view = new PeriodoLetivoListView();
                    new PeriodoLetivoController(view);
                    view.setVisible(true);
                });

                // Mude o GridLayout para 3 linhas
                // JPanel painel = new JPanel(new GridLayout(3, 1, 10, 10));
                painel.add(btnPeriodos);

                menu.add(painel);
                menu.setVisible(true);

                System.out.println("Menu de teste aberto!");
            });
        };
        Runnable turma = ()-> {
            SwingUtilities.invokeLater(() -> {
                // Certifique-se de que você já cadastrou Séries e Anos
                // no teste anterior, senão os combos virão vazios!

                TurmaListView view = new TurmaListView();
                new TurmaController(view);
                view.setVisible(true);
            });
        };
        Runnable matricula = () -> {
            // 1. PREPARA O TERRENO (Seus arquivos estão vazios? Eu preencho pra você)
            prepararAmbienteDeTeste();

            // 2. INICIA A APLICAÇÃO
            SwingUtilities.invokeLater(() -> {
                System.out.println("\n--- TELA DE MATRÍCULA ABERTA ---");
                System.out.println("Passo a passo para testar:");
                System.out.println("1. Clique em 'Nova Matrícula'.");
                System.out.println("2. Selecione o aluno 'Maria' e a turma '1º Ano A'.");
                System.out.println("3. Clique em 'Matricular'.");
                System.out.println("4. Verifique no console ou no arquivo 'data/matricula_disciplinas.json' se a disciplina foi gerada.");

                MatriculaListView view = new MatriculaListView();
                new MatriculaController(view);
                view.setVisible(true);
            });
        };
        Runnable notas = () -> {
            prepararPeriodo();

            SwingUtilities.invokeLater(() -> {
                NotaLancamentoView view = new NotaLancamentoView();
                new NotaController(view);
                view.setVisible(true);
            });
        };
        Runnable financeiro = () -> {
            SwingUtilities.invokeLater(() -> {
                FinanceiroView view = new FinanceiroView();
                new FinanceiroController(view);
                view.setVisible(true);
            });
        };

    }




    private static void prepararAmbienteDeTeste() {
        System.out.println("--- Verificando ambiente de teste... ---");

        // Repositórios necessários
        DisciplinaRepository disciplinaRepo = new DisciplinaRepository();
        TurmaRepository turmaRepo = new TurmaRepository();
        TurmaDisciplinaRepository tdRepo = new TurmaDisciplinaRepository(); // <--- O Importante
        PessoaRepository pessoaRepo = new PessoaRepository();
        AlunoRepository alunoRepo = new AlunoRepository();
        SerieRepository serieRepo = new SerieRepository();
        AnoEscolarRepository anoRepo = new AnoEscolarRepository();

        // 1. Criar Disciplina (se não existir)
        Disciplina mat = null;
        if (disciplinaRepo.listarTodas().isEmpty()) {
            mat = new Disciplina();
            mat.setNome("Matemática");
            mat.setCodigo("MAT100");
            mat.setCargaHoraria(80);
            disciplinaRepo.salvar(mat);
            System.out.println("[SETUP] Disciplina 'Matemática' criada.");
        } else {
            mat = disciplinaRepo.listarTodas().get(0);
        }

        // 2. Criar Série e Ano (se não existir)
        Serie serie = null;
        if (serieRepo.listarTodos().isEmpty()) {
            serie = new Serie();
            serie.setNome("1º Ano Médio");
            serieRepo.salvar(serie);
        } else {
            serie = serieRepo.listarTodos().get(0);
        }

        AnoEscolar ano = null;
        if (anoRepo.listarTodos().isEmpty()) {
            ano = new AnoEscolar();
            ano.setAno(2025);
            ano.setStatus("Ativo");
            anoRepo.salvar(ano);
        } else {
            ano = anoRepo.listarTodos().get(0);
        }

        // 3. Criar Turma (se não existir)
        Turma turma = null;
        if (turmaRepo.listarTodos().isEmpty()) {
            turma = new Turma();
            turma.setNome("1º Ano A");
            turma.setTurno(TurnoTurma.MANHA);
            turma.setSerieId(serie.getId());
            turma.setAnoEscolarId(ano.getId());
            turmaRepo.salvar(turma);
            System.out.println("[SETUP] Turma '1º Ano A' criada.");
        } else {
            turma = turmaRepo.listarTodos().get(0);
        }

        // 4. *** CRUCIAL ***: Vincular Disciplina à Turma
        // Sem isso, a matrícula automática não gera nada!
        if (tdRepo.listarTodos().isEmpty()) {
            TurmaDisciplina td = new TurmaDisciplina();
            td.setTurmaId(turma.getId());
            td.setDisciplinaId(mat.getId());
            td.setProfessorPessoaId(0); // Sem professor por enquanto, só pra teste
            tdRepo.salvar(td);
            System.out.println("[SETUP] Vínculo criado: Turma " + turma.getNome() + " tem Matemática.");
        }

        // 5. Criar Aluno (se não existir)
        if (alunoRepo.listarTodos().isEmpty()) {
            // Cria Pessoa
            Pessoa p = new Pessoa();
            p.setNomeCompleto("Maria da Silva");
            p.setCpf("123.456.789-00");
            pessoaRepo.salvar(p);

            // Cria Aluno
            Aluno a = new Aluno();
            a.setPessoaId(p.getId());
            alunoRepo.salvar(a);
            System.out.println("[SETUP] Aluna 'Maria' criada.");
        }

        System.out.println("--- Ambiente pronto. ---");
    }
    private static void prepararPeriodo() {
        // Garante que existe pelo menos um período para a Turma (Ano) criada nos testes anteriores
        AnoEscolarRepository anoRepo = new AnoEscolarRepository();
        PeriodoLetivoRepository periodoRepo = new PeriodoLetivoRepository();

        if (anoRepo.listarTodos().isEmpty()) {
            System.out.println("ERRO: Rode o MainMatriculaTeste primeiro para criar o Ano/Turma.");
            return;
        }

        int anoId = anoRepo.listarTodos().get(0).getId();

        if (periodoRepo.listarTodos().isEmpty()) {
            PeriodoLetivo p = new PeriodoLetivo();
            p.setNome("1º Bimestre");
            p.setAnoEscolarId(anoId);
            p.setDataInicio(LocalDate.now());
            p.setDataFim(LocalDate.now().plusMonths(2));
            periodoRepo.salvar(p);
            System.out.println("SETUP: Período '1º Bimestre' criado.");
        }
    }
}