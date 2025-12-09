package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.AlunoResponsavel;
import models.ComunicadoDestinatario;

import java.util.List;
import java.util.Optional;

public class ComunicadoDestinatarioRepository extends BaseRepository<ComunicadoDestinatario> {
    public ComunicadoDestinatarioRepository() {
        super("comunicado_destinatario.json", new TypeReference<List<ComunicadoDestinatario>>() {});
    }

    public void salvar(ComunicadoDestinatario cd) {
        int novoId = gerarProximoId(ComunicadoDestinatario::getId);
        cd.setId(novoId);
        this.cache.add(cd);
        salvarNoArquivo();
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
    public void marcarComoLido(int id) {
        Optional<ComunicadoDestinatario> opt = this.cache.stream().filter(cd -> cd.getComunicadoId() == id).findFirst();
        if(opt.isPresent()) {
            opt.get().setLido(true);
            salvarNoArquivo();
        }
    }

    // Busca todas as mensagens destinadas a uma pessoa
    public List<ComunicadoDestinatario> buscarPorDestinatario(int pessoaId) {
        return this.cache.stream()
                .filter(cd -> cd.getDestinatarioPessoaId() == pessoaId)
                .toList();
    }
}
