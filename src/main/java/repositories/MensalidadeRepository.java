package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Mensalidade;

import java.util.List;
import java.util.Optional;

public class MensalidadeRepository extends BaseRepository<Mensalidade> {
    public MensalidadeRepository() {
        super("mensalidade.json", new TypeReference<List<Mensalidade>>() {});
    }
    public Mensalidade salvar(Mensalidade mensalidade) {
        int novoId = gerarProximoId(Mensalidade::getId);
        mensalidade.setId(novoId);

        this.cache.add(mensalidade);
        salvarNoArquivo();

        return mensalidade;
    }

    public List<Mensalidade> listarTodos() {
        return this.cache;
    }

    public Optional<Mensalidade> buscarPorId(int id) {
        return this.cache.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public void editar(Mensalidade mensalidadeAtualizada) {
        this.cache.removeIf(t -> t.getId() == mensalidadeAtualizada.getId());
        this.cache.add(mensalidadeAtualizada);
        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(t -> t.getId() == id);
        salvarNoArquivo();
    }

    public List<Mensalidade> buscarPorMatriculaId(int matriculaId) {
        return this.cache.stream()
                .filter(t -> t.getMatriculaId() == matriculaId)
                .toList();
    }
}
