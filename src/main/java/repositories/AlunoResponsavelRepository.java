package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.AlunoResponsavel;
import java.util.List;
import java.util.stream.Collectors;

public class AlunoResponsavelRepository extends BaseRepository<AlunoResponsavel> {

    public AlunoResponsavelRepository() {
        super("alunos_responsavel.json", new TypeReference<List<AlunoResponsavel>>() {});
    }

    public AlunoResponsavel salvar(AlunoResponsavel associacao) {
        this.cache.add(associacao);
        salvarNoArquivo();
        return associacao;
    }

    public List<AlunoResponsavel> listarTodos() {
        return this.cache;
    }

    public List<AlunoResponsavel> buscarPorAlunoId(int alunoPessoaId) {
        return this.cache.stream()
                .filter(ar -> ar.getAlunoPessoaId() == alunoPessoaId)
                .toList();
    }

    public List<AlunoResponsavel> buscarPorResponsavelId(int responsavelPessoaId) {
        return this.cache.stream()
                .filter(ar -> ar.getResponsavelPessoaId() == responsavelPessoaId)
                .toList();
    }
    public List<Integer> buscarIdsAlunosDoResponsavel(int responsavelPessoaId) {
        return this.cache.stream()
                .filter(ar -> ar.getResponsavelPessoaId() == responsavelPessoaId)
                .map(AlunoResponsavel::getAlunoPessoaId)
                .collect(Collectors.toList());
    }

    public void excluir(int alunoPessoaId, int responsavelPessoaId) {
        this.cache.removeIf(ar ->
                ar.getAlunoPessoaId() == alunoPessoaId &&
                        ar.getResponsavelPessoaId() == responsavelPessoaId
        );
        salvarNoArquivo();
    }
}
