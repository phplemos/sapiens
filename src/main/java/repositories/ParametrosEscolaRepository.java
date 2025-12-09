package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.ParametrosEscola;
import java.util.List;

public class ParametrosEscolaRepository extends BaseRepository<ParametrosEscola> {

    public ParametrosEscolaRepository() {
        super("parametros_escola.json", new TypeReference<List<ParametrosEscola>>() {});
    }

    public ParametrosEscola buscarParametrosGerais() {
        if (this.cache.isEmpty()) {
            ParametrosEscola padrao = new ParametrosEscola();
            padrao.setId(1);
            this.cache.add(padrao);
            salvarNoArquivo();
            return padrao;
        }
        return this.cache.get(0);
    }

    public void salvar(ParametrosEscola parametros) {
        this.cache.clear();
        this.cache.add(parametros);
        salvarNoArquivo();
    }
}