package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import enums.StatusMatricula;
import models.Matricula;

import java.util.List;
import java.util.Optional;

public class MatriculaRepository extends BaseRepository<Matricula> {
    public MatriculaRepository() {
        super("matricula.json", new TypeReference<List<Matricula>>(){});
    }

    public Matricula salvar(Matricula matricula) {
        int novoId = gerarProximoId(Matricula::getId);
        matricula.setId(novoId);

        this.cache.add(matricula);
        salvarNoArquivo();

        return matricula;
    }

    public List<Matricula> listarTodos() {
        return this.cache;
    }

    public Optional<Matricula> buscarPorId(int id) {
        return this.cache.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public void editar(Matricula matriculaAtualizada) {
        this.cache.removeIf(t -> t.getId() == matriculaAtualizada.getId());
        this.cache.add(matriculaAtualizada);
        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(t -> t.getId() == id);
        salvarNoArquivo();
    }

    public List<Matricula> buscarPorTurmaId(int turmaId) {
        return this.cache.stream()
                .filter(t -> t.getTurmaId() == turmaId)
                .toList();
    }

    public boolean alunoJaMatriculado(int alunoId, int turmaId) {
        return this.cache.stream().anyMatch(m -> m.getAlunoPessoaId() == alunoId && m.getTurmaId() == turmaId);
    }

    public boolean alunoJaMatriculadoEmTurmaAtiva(int alunoId){
        return this.cache.stream().anyMatch(m -> m.getAlunoPessoaId() == alunoId && m.getStatus() == StatusMatricula.ATIVA);
    }
}
