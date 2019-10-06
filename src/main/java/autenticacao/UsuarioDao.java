package autenticacao;

import java.util.List;

public interface UsuarioDao {
    void salvar(Usuario usuario);
    Usuario pesquisar(String usuario);
    List<Usuario> todos();
}
