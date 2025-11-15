package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Disciplina;

import java.util.List;
import java.util.Optional;

public class DisciplinaRepository extends BaseRepository<Disciplina> {

    public DisciplinaRepository() {
        super("disciplinas.json", new TypeReference<List<Disciplina>>() {});
    }

    public Disciplina salvar(Disciplina disciplina) {
        int novoId = gerarProximoId();
        disciplina.setId(novoId);

        this.cache.add(disciplina);
        salvarNoArquivo();

        return disciplina;
    }

    public List<Disciplina> listarTodas() {
        return this.cache;
    }

    public Optional<Disciplina> buscarPorId(int id) {
        return this.cache.stream()
                .filter(d -> d.getId() == id)
                .findFirst();
    }

    public void editar(Disciplina disciplinaAtualizada) {
        this.cache.removeIf(d -> d.getId() == disciplinaAtualizada.getId());
        this.cache.add(disciplinaAtualizada);

        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(d -> d.getId() == id);
        salvarNoArquivo();
    }


    private int gerarProximoId() {
        return this.cache.stream()
                .mapToInt(Disciplina::getId)
                .max()
                .orElse(0) + 1;
    }
}