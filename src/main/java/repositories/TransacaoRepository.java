package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Transacao;

import java.util.List;
import java.util.Optional;

public class TransacaoRepository extends BaseRepository<Transacao> {
    public TransacaoRepository() {
        super("transacao.json", new TypeReference<List<Transacao>>(){});
    }
    public Transacao salvar(Transacao transacao) {
        int novoId = gerarProximoId(Transacao::getId);
        transacao.setId(novoId);

        this.cache.add(transacao);
        salvarNoArquivo();

        return transacao;
    }

    public List<Transacao> listarTodos() {
        return this.cache;
    }

    public Optional<Transacao> buscarPorId(int id) {
        return this.cache.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public void editar(Transacao transacaoAtualizada) {
        this.cache.removeIf(t -> t.getId() == transacaoAtualizada.getId());
        this.cache.add(transacaoAtualizada);
        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(t -> t.getId() == id);
        salvarNoArquivo();
    }

    public List<Transacao> buscarPorMensalidadeId(int mensalidadeId) {
        return this.cache.stream()
                .filter(t -> t.getMensalidadeId() == mensalidadeId)
                .toList();
    }
    public List<Transacao> buscarPorCategoriaId(int categoriaId) {
        return this.cache.stream()
                .filter(t -> t.getCategoriaId() == categoriaId)
                .toList();
    }
}
