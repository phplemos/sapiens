package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Pessoa;

import java.util.List;
import java.util.Optional;

public class PessoaRepository extends BaseRepository<Pessoa> {

    public PessoaRepository() {
        super("pessoas.json", new TypeReference<List<Pessoa>>() {});
    }

    public Pessoa salvar(Pessoa pessoa) {
        int novoId = gerarProximoId(Pessoa::getId);

        pessoa.setId(novoId);
        this.cache.add(pessoa);
        salvarNoArquivo();

        return pessoa; // Retorna a pessoa com o ID atribuído
    }

    public List<Pessoa> listarTodas() {
        return this.cache;
    }

    public Optional<Pessoa> buscarPorId(int id) {
        return this.cache.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    public void editar(Pessoa pessoaAtualizada) {
        this.cache.removeIf(p -> p.getId() == pessoaAtualizada.getId());
        this.cache.add(pessoaAtualizada);
        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(p -> p.getId() == id);
        salvarNoArquivo();
    }
}