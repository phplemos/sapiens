package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Usuario;

import java.util.List;
import java.util.Optional;

public class UsuarioRepository extends BaseRepository<Usuario> {

    public UsuarioRepository() {
        super("usuario.json", new TypeReference<List<Usuario>>(){});
    }

    public Usuario salvar(Usuario usuario) {
        this.cache.add(usuario);
        salvarNoArquivo();
        return usuario;
    }

    public List<Usuario> listarTodos() {
        return this.cache;
    }

    public Optional<Usuario> buscarPorPessoaId(int pessoaId) {
        return this.cache.stream()
                .filter(p -> p.getPessoaId() == pessoaId)
                .findFirst();
    }

    public void editar(Usuario usuarioAtualizado) {
        this.cache.removeIf(p -> p.getPessoaId() == usuarioAtualizado.getPessoaId());
        this.cache.add(usuarioAtualizado);
        salvarNoArquivo();
    }

    public void excluir(int pessoaId) {
        this.cache.removeIf(p -> p.getPessoaId() == pessoaId);
        salvarNoArquivo();
    }
}
