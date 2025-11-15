package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.MatriculaDisciplina;

import java.util.List;
import java.util.Optional;

public class MatriculaDisciplinaRepository extends BaseRepository<MatriculaDisciplina> {

    public MatriculaDisciplinaRepository() {
        super("matricula_disciplina", new TypeReference<List<MatriculaDisciplina>>(){});
    }

    public MatriculaDisciplina salvar(MatriculaDisciplina matriculaDisciplina) {
        int novoId = gerarProximoId(MatriculaDisciplina::getId);
        matriculaDisciplina.setId(novoId);

        this.cache.add(matriculaDisciplina);
        salvarNoArquivo();

        return matriculaDisciplina;
    }

    public List<MatriculaDisciplina> listarTodos() {
        return this.cache;
    }

    public Optional<MatriculaDisciplina> buscarPorId(int id) {
        return this.cache.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public void editar(MatriculaDisciplina matriculaDisciplinaAtualizada) {
        this.cache.removeIf(t -> t.getId() == matriculaDisciplinaAtualizada.getId());
        this.cache.add(matriculaDisciplinaAtualizada);
        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(t -> t.getId() == id);
        salvarNoArquivo();
    }

    public List<MatriculaDisciplina> buscarPorTurmaDisciplinaId(int turmaDisciplinaId) {
        return this.cache.stream()
                .filter(t -> t.getTurmaDisciplinaId() == turmaDisciplinaId)
                .toList();
    }

    public List<MatriculaDisciplina> buscarPorMatriculaId(int matriculaId) {
        return this.cache.stream()
                .filter(t -> t.getMatriculaId() == matriculaId)
                .toList();
    }
}
