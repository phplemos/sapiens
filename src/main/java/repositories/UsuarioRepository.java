package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Pessoa;
import models.Usuario;

import java.util.List;
import java.util.Optional;

public class UsuarioRepository extends BaseRepository<Usuario> {

    PessoaRepository pessoaRepo = new PessoaRepository();

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

    public Optional<Usuario> autenticar(String login, String senha) {
        return this.cache.stream()
                .filter(u -> u.getLogin().equals(login) && u.getSenhaHash().equals(senha))
                .findFirst();
    }

    public String getNomeUsuario(Usuario usuario){
        Optional<Pessoa> pessoa = pessoaRepo.buscarPorId(usuario.getPessoaId());
        if(pessoa.isPresent()) {
            return pessoa.get().getNomeCompleto();
        }
        return "Não há pessoa cadastrada!";
    }
}
