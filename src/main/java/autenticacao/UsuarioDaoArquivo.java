package autenticacao;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UsuarioDaoArquivo implements UsuarioDao {

    private final Path arquivo;

    public UsuarioDaoArquivo(String caminhoArquivo) {
        arquivo = Path.of(caminhoArquivo);
    }

    @Override
    public void salvar(Usuario usuario) {
        try(OutputStream writer = Files.newOutputStream(arquivo, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            writer.write(String.format("%s:%s%s", usuario.nome, usuario.senha, System.lineSeparator()).getBytes());
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Usuario pesquisar(String usuario) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(arquivo)))) {
            String linha;
            while((linha = reader.readLine()) != null) {
                final String nomeUsuario = linha.split(":")[0];
                final String senhaUsuario = linha.split(":")[1];
                if (Objects.equals(usuario, nomeUsuario)) {
                    return new Usuario(nomeUsuario, senhaUsuario);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Usuario> todos() {
        List<Usuario> usuarios = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(arquivo)))) {
            String linha;
            while((linha = reader.readLine()) != null) {
                final String nomeUsuario = linha.split(":")[0];
                final String senhaUsuario = linha.split(":")[1];
                usuarios.add(new Usuario(nomeUsuario, senhaUsuario));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return usuarios;
    }
}
