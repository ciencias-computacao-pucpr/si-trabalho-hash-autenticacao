package autenticacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class UsuarioDaoArquivo implements UsuarioDao {

    private final Path arquivo;

    public UsuarioDaoArquivo(String caminhoArquivo) {
        arquivo = Path.of(caminhoArquivo);
    }

    @Override
    public void salvar(Usuario usuario) {
        try (OutputStream writer = Files.newOutputStream(arquivo, CREATE, APPEND)) {
            writer.write(String.format("%s:%s%s", usuario.nome, usuario.senha, System.lineSeparator()).getBytes());
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Usuario pesquisar(String usuario) {
        List<Usuario> todos = todos();
        if (todos.isEmpty()) {
            return null;
        }

        return todos.stream().filter(u -> u.nome.equals(usuario)).findAny().orElse(null);
    }

    @Override
    public List<Usuario> todos() {
        return readLines();
    }

    private List<Usuario> readLines() {
        List<Usuario> usuarios = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(arquivo, CREATE)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                usuarios.add(linha2UserConverter(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return usuarios;
    }

    private Usuario linha2UserConverter(String linha) {
        final String nomeUsuario = linha.split(":")[0];
        final String senhaUsuario = linha.split(":")[1];
        return new Usuario(nomeUsuario, senhaUsuario);
    }
}
