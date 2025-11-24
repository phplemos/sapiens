package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Professor;

import java.util.List;
import java.util.Optional;

public class ProfessorRepository extends BaseRepository<Professor> {
    public ProfessorRepository() {
        super("professor.json", new TypeReference<List<Professor>>() {});
    }

    public Professor salvar(Professor professor) {
        this.cache.add(professor);
        salvarNoArquivo();
        return professor;
    }

    public List<Professor> listarTodos() {
        return this.cache;
    }

    public Optional<Professor> buscarPorPessoaId(int pessoaId) {
        return this.cache.stream()
                .filter(p -> p.getPessoaId() == pessoaId)
                .findFirst();
    }

    public void editar(Professor professorAtualizado) {
        this.cache.removeIf(p -> p.getPessoaId() == professorAtualizado.getPessoaId());
        this.cache.add(professorAtualizado);
        salvarNoArquivo();
    }

    public void excluir(int pessoaId) {
        this.cache.removeIf(p -> p.getPessoaId() == pessoaId);
        salvarNoArquivo();
    }
}
