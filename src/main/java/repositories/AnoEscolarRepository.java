package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.AnoEscolar;

import java.util.List;
import java.util.Optional;

public class AnoEscolarRepository extends BaseRepository<AnoEscolar> {
    public AnoEscolarRepository() {
        super("ano_escolar.json", new TypeReference<List<AnoEscolar>>() {});
    }

    public AnoEscolar salvar(AnoEscolar anoEscolar) {
        int novoId = gerarProximoId(AnoEscolar::getId);
        anoEscolar.setId(novoId);

        this.cache.add(anoEscolar);
        salvarNoArquivo();

        return anoEscolar;
    }

    public List<AnoEscolar> listarTodos() {
        return this.cache;
    }

    public Optional<AnoEscolar> buscarPorId(int id) {
        return this.cache.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public void editar(AnoEscolar anoEscolarAtualizado) {
        this.cache.removeIf(t -> t.getId() == anoEscolarAtualizado.getId());
        this.cache.add(anoEscolarAtualizado);
        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(t -> t.getId() == id);
        salvarNoArquivo();
    }

    public List<AnoEscolar> buscarPorStatus(String status) {
        return this.cache.stream()
                .filter(t -> t.getStatus() == status)
                .toList();
    }
}
