package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Aluno;

import java.util.List;
import java.util.Optional;

public class AlunoRepository extends BaseRepository<Aluno> {

    public AlunoRepository() {
        super("alunos.json", new TypeReference<List<Aluno>>() {});
    }

    public Aluno salvar(Aluno aluno) {
        this.cache.add(aluno);
        salvarNoArquivo();
        return aluno;
    }

    public List<Aluno> listarTodos() {
        return this.cache;
    }

    public Optional<Aluno> buscarPorPessoaId(int pessoaId) {
        return this.cache.stream()
                .filter(a -> a.getPessoaId() == pessoaId)
                .findFirst();
    }

    public void editar(Aluno alunoAtualizado) {
        this.cache.removeIf(a -> a.getPessoaId() == alunoAtualizado.getPessoaId());
        this.cache.add(alunoAtualizado);
        salvarNoArquivo();
    }

    public void excluir(int pessoaId) {
        this.cache.removeIf(a -> a.getPessoaId() == pessoaId);
        salvarNoArquivo();
    }
}