package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Responsavel;

import java.util.List;
import java.util.Optional;

public class ResponsavelRepository extends BaseRepository<Responsavel> {
    public ResponsavelRepository() {
        super("responsavel.json", new TypeReference<List<Responsavel>>() {});
    }

    public Responsavel salvar(Responsavel responsavel) {
        this.cache.add(responsavel);
        salvarNoArquivo();
        return responsavel;
    }

    public List<Responsavel> listarTodos() {
        return this.cache;
    }

    public Optional<Responsavel> buscarPorPessoaId(int pessoaId) {
        return this.cache.stream()
                .filter(p -> p.getPessoaId() == pessoaId)
                .findFirst();
    }

    public void editar(Responsavel responsavelAtualizado) {
        this.cache.removeIf(p -> p.getPessoaId() == responsavelAtualizado.getPessoaId());
        this.cache.add(responsavelAtualizado);
        salvarNoArquivo();
    }

    public void excluir(int pessoaId) {
        this.cache.removeIf(p -> p.getPessoaId() == pessoaId);
        salvarNoArquivo();
    }
}
