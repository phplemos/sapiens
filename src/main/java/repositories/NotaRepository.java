package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Nota;
import models.Turma;

import java.util.List;
import java.util.Optional;

public class NotaRepository extends BaseRepository<Nota> {
    public NotaRepository() {
        super("nota.json", new TypeReference<List<Nota>>() {});
    }
    public Nota salvar(Nota nota) {
        int novoId = gerarProximoId(Nota::getId);
        nota.setId(novoId);

        this.cache.add(nota);
        salvarNoArquivo();

        return nota;
    }

    public List<Nota> listarTodos() {
        return this.cache;
    }

    public Optional<Nota> buscarPorId(int id) {
        return this.cache.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public void editar(Nota notaAtualizada) {
        this.cache.removeIf(t -> t.getId() == notaAtualizada.getId());
        this.cache.add(notaAtualizada);
        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(t -> t.getId() == id);
        salvarNoArquivo();
    }

    public List<Nota> buscarPorMatriculaDisciplinaId(int matriculaDisciplinaId) {
        return this.cache.stream()
                .filter(t -> t.getMatriculaDisciplinaId() == matriculaDisciplinaId)
                .toList();
    }
    public List<Nota> buscarPorPeriodoLetivoId(int periodoLetivoId) {
        return this.cache.stream()
                .filter(t -> t.getPeriodoLetivoId() == periodoLetivoId)
                .toList();
    }
    public Optional<Nota> buscarNota(int matriculaDisciplinaId, int periodoLetivoId) {
        return this.cache.stream()
                .filter(n -> n.getMatriculaDisciplinaId() == matriculaDisciplinaId &&
                        n.getPeriodoLetivoId() == periodoLetivoId)
                .findFirst();
    }

}
