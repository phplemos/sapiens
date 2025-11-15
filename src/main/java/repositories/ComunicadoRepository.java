package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Comunicado;

import java.util.List;
import java.util.Optional;

public class ComunicadoRepository extends BaseRepository<Comunicado> {
    public ComunicadoRepository() {
        super("comunicado.json", new TypeReference<List<Comunicado>>() {});
    }
    public Comunicado salvar(Comunicado comunicado) {
        int novoId = gerarProximoId(Comunicado::getId);
        comunicado.setId(novoId);

        this.cache.add(comunicado);
        salvarNoArquivo();

        return comunicado;
    }

    public List<Comunicado> listarTodos() {
        return this.cache;
    }

    public Optional<Comunicado> buscarPorId(int id) {
        return this.cache.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public void editar(Comunicado comunicadoAtualizado) {
        this.cache.removeIf(t -> t.getId() == comunicadoAtualizado.getId());
        this.cache.add(comunicadoAtualizado);
        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(t -> t.getId() == id);
        salvarNoArquivo();
    }

    public List<Comunicado> buscarPorRemetentePessoaId(int remetentePessoaId) {
        return this.cache.stream()
                .filter(t -> t.getRemetentePessoaId() == remetentePessoaId)
                .toList();
    }
}
