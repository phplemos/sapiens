package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Serie;
import models.Turma;

import java.util.List;
import java.util.Optional;

public class SerieRepository extends BaseRepository<Serie> {
    public SerieRepository() {
        super("serie.json", new TypeReference<List<Serie>>(){});
    }

    public Serie salvar(Serie serie) {
        int novoId = gerarProximoId(Serie::getId);
        serie.setId(novoId);

        this.cache.add(serie);
        salvarNoArquivo();

        return serie;
    }

    public List<Serie> listarTodos() {
        return this.cache;
    }

    public Optional<Serie> buscarPorId(int id) {
        return this.cache.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public void editar(Serie serieAtualizada) {
        this.cache.removeIf(t -> t.getId() == serieAtualizada.getId());
        this.cache.add(serieAtualizada);
        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(t -> t.getId() == id);
        salvarNoArquivo();
    }

    public List<Serie> buscarPorNome(String nome) {
        return this.cache.stream()
                .filter(t -> t.getNome().contains(nome))
                .toList();
    }
}
