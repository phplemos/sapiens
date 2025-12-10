package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.PeriodoLetivo;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PeriodoLetivoRepository extends BaseRepository<PeriodoLetivo> {


    public PeriodoLetivoRepository() {
        super("periodo_letivo.json", new TypeReference<List<PeriodoLetivo>>() {});
    }

    public PeriodoLetivo salvar(PeriodoLetivo periodoLetivo) {
        int novoId = gerarProximoId(PeriodoLetivo::getId);
        periodoLetivo.setId(novoId);
        this.cache.add(periodoLetivo);
        salvarNoArquivo();
        return periodoLetivo;
    }

    public List<PeriodoLetivo> listarTodos() {
        return this.cache;
    }

    public Optional<PeriodoLetivo> buscarPorId(int id) {
        return this.cache.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public void editar(PeriodoLetivo periodoLetivoAtualizado) {
        this.cache.removeIf(t -> t.getId() == periodoLetivoAtualizado.getId());
        this.cache.add(periodoLetivoAtualizado);
        salvarNoArquivo();
    }

    public void excluir(int id) {
        this.cache.removeIf(t -> t.getId() == id);
        salvarNoArquivo();
    }

    public List<PeriodoLetivo> buscarPorAnoEscolarId(int anoEscolarId) {
        return this.cache.stream()
                .filter(p -> p.getAnoEscolarId() == anoEscolarId)
                .sorted(Comparator.comparing(PeriodoLetivo::getDataInicio))
                .collect(Collectors.toList());
    }
}