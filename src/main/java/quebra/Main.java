package quebra;

import autenticacao.Usuario;
import autenticacao.UsuarioDao;
import autenticacao.UsuarioDaoArquivo;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;

public class Main {

    private static char[] alfabeto = new char[255-31];

    public static void main(String[] args) throws IOException {
        popularAlfabeto();
        UsuarioDao dao = new UsuarioDaoArquivo("senhas.txt");
        System.out.print("Tempo total: ");
        long tempoTotal = temporizar(() -> {
            for (Usuario usuario : dao.todos().subList(0, 4)) {
                long tempoUsuario = temporizar(() -> {
                    String senha = encontrarCombinacao("", usuario.senha, 4);
                    System.out.println("Senha encontrada: " + senha);
                });
                System.out.println("Tempo senha do usuário " + usuario.nome + ": " + tempoUsuario);
            }
        });
        System.out.println("Tempo total para quebrar 4 senhas: " + tempoTotal);

    }

    private static long temporizar(Runnable runnable) {
        Instant ini = Instant.now();
        runnable.run();
        Instant fim = Instant.now();
        return Duration.between(ini, fim).getSeconds();
    }

    private static void popularAlfabeto() {
        for (char i = 32; i <= 255; i++) {
            alfabeto[i-32] = i;
        }
    }

    private static String encontrarCombinacao(String prefix, String hash, int k) {
        if (k == 0) return prefix;


        for (int i = 0; i < alfabeto.length; i++) {
            String p = prefix + alfabeto[i];
            String combinacao = encontrarCombinacao(p, hash, k - 1);
            if (combinacao != null) {
                if (md5(combinacao).equals(hash)) {
                    return combinacao;
                }
            }
        }

        return null;
    }

    private static String md5(String conteudo) {
        try {
            final byte[] md5s = MessageDigest.getInstance("MD5").digest(conteudo.getBytes());
            return new BigInteger(1,md5s).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
