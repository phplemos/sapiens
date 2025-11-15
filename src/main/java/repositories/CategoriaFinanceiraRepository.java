package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import enums.TipoTransacao;
import models.CategoriaFinanceira;

import java.util.List;
import java.util.Optional;

public class CategoriaFinanceiraRepository extends BaseRepository<CategoriaFinanceira>{
    public CategoriaFinanceiraRepository() {
        super("categoria_financeira.json", new TypeReference<List<CategoriaFinanceira>>() {});
    }
    public CategoriaFinanceira salvar(CategoriaFinanceira categoriaFinanceira) {
        int novoId = gerarProximoId(CategoriaFinanceira::getId);
        categoriaFinanceira.setId(novoId);

        this.cache.add(categoriaFinanceira);
        salvarNoArquivo();

        return categoriaFinanceira;
    }

    public List<CategoriaFinanceira> listarTodos() {
        return this.cache;
    }

    public Optional<CategoriaFinanceira> buscarPorId(int id) {
        return this.cache.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public void editar(CategoriaFinanceira categoriaFinanceiraAtualizada) {
        this.cache.removeIf(t -> t.getId() == categoriaFinanceiraAtualizada.getId());
        this.cache.add(categoriaFinanceiraAtualizada);
        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(t -> t.getId() == id);
        salvarNoArquivo();
    }

    public List<CategoriaFinanceira> buscarPorTipo(TipoTransacao tipo) {
        return this.cache.stream()
                .filter(t -> t.getTipo() == tipo)
                .toList();
    }
}
