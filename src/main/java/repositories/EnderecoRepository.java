package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Endereco;

import java.util.List;
import java.util.Optional;

public class EnderecoRepository extends BaseRepository<Endereco> {

    public EnderecoRepository() {
        super("endereco.json", new TypeReference<List<Endereco>>() {});
    }

    public Endereco salvar(Endereco endereco) {
        int novoId = gerarProximoId(Endereco::getId);
        endereco.setId(novoId);
        this.cache.add(endereco);
        salvarNoArquivo();
        return endereco;
    }

    public List<Endereco> listarTodos() {
        return this.cache;
    }

    public Optional<Endereco> buscarPorId(int enderecoId) {
        return this.cache.stream()
                .filter(a -> a.getId() == enderecoId)
                .findFirst();
    }

    public void editar(Endereco enderecoAtualizado) {
        this.cache.removeIf(a -> a.getId() == enderecoAtualizado.getId());
        this.cache.add(enderecoAtualizado);
        salvarNoArquivo();
    }

    public void excluir(int enderecoId) {
        this.cache.removeIf(a -> a.getId() == enderecoId);
        salvarNoArquivo();
    }
}
