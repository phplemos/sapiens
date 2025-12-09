package repositories;

import models.BoletimStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import models.Comunicado;

import java.util.List;
import java.util.Optional;

public class BoletimStatusRepository extends BaseRepository<BoletimStatus> {

    public BoletimStatusRepository() {
        super("boletim_status.json", new TypeReference<List<BoletimStatus>>() {});
    }

    public void registrarPublicacao(int turmaId, int periodoId, boolean status) {
        Optional<BoletimStatus> existente = buscarPorTurmaEPeriodo(turmaId, periodoId);

        if (existente.isPresent()) {
            BoletimStatus b = existente.get();
            b.setPublicado(status);
            editar(b); // Método genérico do BaseRepository (precisa implementar editar lá ou aqui)
            // Se o Base não tiver editar genérico, implemente:
            this.cache.removeIf(item -> item.getId() == b.getId());
            this.cache.add(b);
            salvarNoArquivo();
        } else {
            // Cria novo
            BoletimStatus b = new BoletimStatus();
            b.setId(gerarProximoId(BoletimStatus::getId));
            b.setTurmaId(turmaId);
            b.setPeriodoLetivoId(periodoId);
            b.setPublicado(status);
            this.cache.add(b);
            salvarNoArquivo();
        }
    }
    public void editar(BoletimStatus boletimStatus) {
        this.cache.removeIf(t -> t.getId() == boletimStatus.getId());
        this.cache.add(boletimStatus);
        salvarNoArquivo();
    }

    public Optional<BoletimStatus> buscarPorTurmaEPeriodo(int turmaId, int periodoId) {
        return this.cache.stream()
                .filter(b -> b.getTurmaId() == turmaId && b.getPeriodoLetivoId() == periodoId)
                .findFirst();
    }

    public boolean isPublicado(int turmaId, int periodoId) {
        return buscarPorTurmaEPeriodo(turmaId, periodoId)
                .map(BoletimStatus::isPublicado)
                .orElse(false); // Se não achar registro, assume NÃO publicado
    }
}