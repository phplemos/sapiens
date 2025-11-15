package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.AlunoResponsavel;
import models.ComunicadoDestinatario;

import java.util.List;

public class ComunicadoDestinatarioRepository extends BaseRepository<ComunicadoDestinatario> {
    public ComunicadoDestinatarioRepository() {
        super("comunicado_destinatario.json", new TypeReference<List<ComunicadoDestinatario>>() {});
    }

    public ComunicadoDestinatario salvar(ComunicadoDestinatario comunicadoDestinatario) {
        this.cache.add(comunicadoDestinatario);
        salvarNoArquivo();
        return comunicadoDestinatario;
    }

    public List<ComunicadoDestinatario> listarTodos() {
        return this.cache;
    }

    public List<ComunicadoDestinatario> buscarPorDestinatarioPessoaId(int destinatarioPessoaId) {
        return this.cache.stream()
                .filter(ar -> ar.getDestinatarioPessoaId() == destinatarioPessoaId)
                .toList();
    }

    public List<ComunicadoDestinatario> buscarPorComunicadoId(int comunicadoId) {
        return this.cache.stream()
                .filter(ar -> ar.getComunicadoId() == comunicadoId)
                .toList();
    }

    public void excluir(int comunicadoId, int destinatarioPessoaId) {
        this.cache.removeIf(ar ->
                ar.getComunicadoId() == comunicadoId &&
                        ar.getDestinatarioPessoaId() == destinatarioPessoaId
        );
        salvarNoArquivo();
    }
}
