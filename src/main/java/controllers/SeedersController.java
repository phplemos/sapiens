package controllers;

import enums.TipoPerfilUsuario;
import models.Disciplina;
import models.Endereco;
import models.Pessoa;
import models.Usuario;
import repositories.DisciplinaRepository;
import repositories.EnderecoRepository;
import repositories.PessoaRepository;
import repositories.UsuarioRepository;

import java.time.LocalDate;

public class SeedersController {

    public static boolean populate(){
        final UsuarioRepository usuarioRepo = new UsuarioRepository();
        final PessoaRepository pessoaRepo = new PessoaRepository();
        final EnderecoRepository enderecoRepo  = new EnderecoRepository();
        final DisciplinaRepository disciplinaRepo = new DisciplinaRepository();

        try{
            garantirAdmin(usuarioRepo,enderecoRepo,pessoaRepo);
            popularDisciplinas(disciplinaRepo);
        } catch (Exception e){
            System.out.println("Erro ao popular disciplinas");
            return false;
        }
        return true;
    }

    public static void garantirAdmin(UsuarioRepository usuarioRepo, EnderecoRepository enderecoRepo, PessoaRepository pessoaRepo) {
            if (usuarioRepo.listarTodos().isEmpty()) {
                Endereco endereco = new Endereco();
                endereco.setLogradouro("Rua dos curios");
                endereco.setNumero("189");
                endereco.setComplemento("Casa cinza");
                endereco.setBairro("São luis");
                endereco.setCep("45.203-346");
                endereco.setCidade("Jequié");
                endereco.setEstado("Bahia");
                endereco = enderecoRepo.salvar(endereco);

                Pessoa pessoa = new Pessoa();
                pessoa.setNomeCompleto("Pedro");
                pessoa.setDataNascimento(LocalDate.now() );
                pessoa.setEmailContato("phplemos.dev@gmail.com");
                pessoa.setCpf("076.447.195-39");
                pessoa.setRg("15.350.265-70");
                pessoa.setEnderecoId(endereco.getId());
                pessoa = pessoaRepo.salvar(pessoa);

                Usuario admin = new Usuario();
                admin.setPessoaId(pessoa.getId());
                admin.setLogin("admin");
                admin.setSenhaHash("123"); // Senha padrão
                admin.setTipoPerfil(TipoPerfilUsuario.ADMIN);
                usuarioRepo.salvar(admin);

                System.out.println(">>> Usuário ADMIN criado: login='admin', senha='123' <<<");
            }
    }

    public static void popularDisciplinas(DisciplinaRepository repo) {
            // Se já tiver dados, não faz nada para não duplicar
            if (!repo.listarTodas().isEmpty()) {
                System.out.println("O arquivo disciplinas.json já contém dados. Seed cancelado.");
                return;
            }

            // --- 1. ENSINO FUNDAMENTAL 1 (1º ao 5º Ano) ---
            // Matérias Base
            String[][] materiasEF1 = {
                    {"Matemática", "MAT", "200"},
                    {"Língua Portuguesa", "PORT", "200"}, // Usando PORT como pediu
                    {"Ciências", "CIE", "160"},
                    {"História", "HIS", "120"},
                    {"Geografia", "GEO", "120"},
                    {"Artes", "ART", "80"},
                    {"Educação Física", "EDF", "80"}
            };

            for (int ano = 1; ano <= 5; ano++) {
                for (String[] mat : materiasEF1) {
                    criarDisciplina(new Disciplina(),repo, mat[0], mat[1], "EF1", ano, Integer.parseInt(mat[2]));
                }
            }

            // --- 2. ENSINO FUNDAMENTAL 2 (6º ao 9º Ano) ---
            // Adiciona Inglês
            String[][] materiasEF2 = {
                    {"Matemática", "MAT", "160"},
                    {"Língua Portuguesa", "PORT", "160"},
                    {"Ciências", "CIE", "120"},
                    {"História", "HIS", "100"},
                    {"Geografia", "GEO", "100"},
                    {"Artes", "ART", "60"},
                    {"Educação Física", "EDF", "60"},
                    {"Inglês", "ING", "80"}
            };

            for (int ano = 6; ano <= 9; ano++) {
                for (String[] mat : materiasEF2) {
                    criarDisciplina(new Disciplina(), repo, mat[0], mat[1], "EF2", ano, Integer.parseInt(mat[2]));
                }
            }

            // --- 3. ENSINO MÉDIO (1º ao 3º Ano) ---
            // Separa as ciências e adiciona sociologia/filosofia
            String[][] materiasEM = {
                    {"Matemática", "MAT", "160"},
                    {"Língua Portuguesa", "PORT", "160"},
                    {"Física", "FIS", "120"},
                    {"Química", "QUI", "120"},
                    {"Biologia", "BIO", "120"},
                    {"História", "HIS", "80"},
                    {"Geografia", "GEO", "80"},
                    {"Filosofia", "FIL", "60"},
                    {"Sociologia", "SOC", "60"},
                    {"Inglês", "ING", "80"},
                    {"Artes", "ART", "40"},
                    {"Educação Física", "EDF", "40"}
            };

            for (int ano = 1; ano <= 3; ano++) {
                for (String[] mat : materiasEM) {
                    criarDisciplina(new Disciplina() ,repo, mat[0], mat[1], "EM", ano, Integer.parseInt(mat[2]));
                }
            }

            System.out.println("--- Seed Concluído com Sucesso! ---");

    }

    private static void criarDisciplina(Disciplina d, DisciplinaRepository repo, String nome, String sigla, String modalidade, int ano, int carga) {
        // Formata o nome ex: "Matemática (1º Ano EF1)"
        d.setNome(nome + " (" + ano + "º Ano " + modalidade + ")");

        // Formata o código ex: MAT-EF1-1
        d.setCodigo(sigla + "-" + modalidade + "-" + ano);

        d.setCargaHoraria(carga);
        d.setDescricao("Disciplina de " + nome + " do currículo base do " + ano + "º ano do " + modalidade);
        d.setConteudoProgramatico(" - Conteúdo A\n - Conteúdo B\n - Avaliação Bimestral");

        // Salva (o ID é gerado automaticamente pelo seu BaseRepository)
        repo.salvar(d);

        System.out.println("Criado: " + d.getCodigo());
    }
}
