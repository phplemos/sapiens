package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Serie;
import models.TurmaDisciplina;

import java.util.List;
import java.util.Optional;

public class TurmaDisciplinaRepository extends BaseRepository<TurmaDisciplina> {

    public TurmaDisciplinaRepository() {
        super("turma_disciplina.json", new TypeReference<List<TurmaDisciplina>>(){});
    }


    public TurmaDisciplina salvar(TurmaDisciplina turmaDisciplina) {
        int novoId = gerarProximoId(TurmaDisciplina::getId);
        turmaDisciplina.setId(novoId);

        this.cache.add(turmaDisciplina);
        salvarNoArquivo();

        return turmaDisciplina;
    }

    public List<TurmaDisciplina> listarTodos() {
        return this.cache;
    }

    public Optional<TurmaDisciplina> buscarPorId(int id) {
        return this.cache.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public void editar(TurmaDisciplina turmaDisciplinaAtualizada) {
        this.cache.removeIf(t -> t.getId() == turmaDisciplinaAtualizada.getId());
        this.cache.add(turmaDisciplinaAtualizada);
        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(t -> t.getId() == id);
        salvarNoArquivo();
    }

    public List<TurmaDisciplina> buscarPorDisciplinaId(int disciplinaId) {
        return this.cache.stream()
                .filter(t -> t.getDisciplinaId() == disciplinaId)
                .toList();
    }
    public List<TurmaDisciplina> buscarPorTurmaId(int turmaId) {
        return this.cache.stream()
                .filter(t -> t.getTurmaId() == turmaId)
                .toList();
    }
}
