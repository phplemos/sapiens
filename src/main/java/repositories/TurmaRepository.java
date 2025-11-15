package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Turma;

import java.util.List;
import java.util.Optional;

public class TurmaRepository extends BaseRepository<Turma> {

    public TurmaRepository() {
        super("turmas.json", new TypeReference<List<Turma>>() {});
    }

    public Turma salvar(Turma turma) {
        int novoId = gerarProximoId(Turma::getId);
        turma.setId(novoId);

        this.cache.add(turma);
        salvarNoArquivo();

        return turma;
    }

    public List<Turma> listarTodos() {
        return this.cache;
    }

    public Optional<Turma> buscarPorId(int id) {
        return this.cache.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public void editar(Turma turmaAtualizada) {
        this.cache.removeIf(t -> t.getId() == turmaAtualizada.getId());
        this.cache.add(turmaAtualizada);
        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(t -> t.getId() == id);
        salvarNoArquivo();
    }

    public List<Turma> buscarPorAnoEscolarId(int anoId) {
        return this.cache.stream()
                .filter(t -> t.getAnoEscolarId() == anoId)
                .toList();
    }
}
