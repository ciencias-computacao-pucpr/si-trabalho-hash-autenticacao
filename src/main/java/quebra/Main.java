package quebra;

import autenticacao.Usuario;
import autenticacao.UsuarioDao;
import autenticacao.UsuarioDaoArquivo;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class Main {

    private static int[] alfabeto;

    public static void main(String[] args) {
        popularAlfabeto();
        UsuarioDao dao = new UsuarioDaoArquivo("politica_fraca_senhas.txt");
        Duration tempoTotal = temporizar(() -> {
            for (Usuario usuario : dao.todos().subList(0, 4)) {
                Duration tempoUsuario = temporizar(() -> {
                    String senha = encontrarCombinacao(usuario.senha, false);
                    System.out.println("Senha encontrada: " + senha);
                });
                System.out.println("Tempo senha do usuário " + usuario.nome + ": " + tempoUsuario);
            }
        });
        System.out.println();
        System.out.println("Tempo total para quebrar 4 senhas: " + tempoTotal.toString());

    }

    private static Duration temporizar(Runnable runnable) {
        Instant ini = Instant.now();
        runnable.run();
        Instant fim = Instant.now();
        return Duration.between(ini, fim);
    }

    private static void popularAlfabeto() {
        alfabeto = createAlphabet(true, true, true);
    }

    private static String encontrarCombinacao(String hash, boolean singleThread) {
        List<Future<String>> futures = new ArrayList<>();
        AtomicBoolean encontrado = new AtomicBoolean(false);
        int cores = singleThread ? 1 : Runtime.getRuntime().availableProcessors();
        ExecutorService execurtor = Executors.newFixedThreadPool(cores);
        int added = 0;
        for (int letra : alfabeto) {
            futures.add(execurtor.submit(() -> encontrarCombinacao(encontrado, String.valueOf((char) letra), hash, 3)));
            added++;
            if (added >= cores) {
                added = 0;
                for (Future<String> future : futures) {
                    try {
                        String result = future.get();
                        if (result != null)
                            return result;
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        execurtor.shutdown();
        return null;
    }

    private static String encontrarCombinacao(AtomicBoolean encontrado, String prefix, String hash, int k) {
        if (encontrado.get())
            return null;
        if (k == 0) return prefix;


        for (int value : alfabeto) {
            String p = prefix + (char) value;
            String combinacao = encontrarCombinacao(encontrado, p, hash, k - 1);
            if (combinacao != null) {
                if (md5(combinacao).equals(hash)) {
                    encontrado.set(true);
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

    private static int[] createAlphabet(boolean incluirMaiusculas, boolean incluirNumeros, boolean incluirSimbolos) {
        return IntStream.rangeClosed(0, 255)
                .filter(c -> {
                    boolean result = isInCharClosedRange((char) c, 'a', 'z');
                    if (incluirMaiusculas) result = result || isInCharClosedRange((char) c, 'A', 'Z');
                    if (incluirNumeros) result = result || isInCharClosedRange((char) c, '0', '9');
                    if (incluirSimbolos)
                        result = result || !isInCharClosedRange((char) c, 'A', 'Z', 'a', 'z', '0', '9');

                    return result;
                }).toArray();
    }

    private static boolean isInCharClosedRange(char c, char... ranges) {
        boolean result = false;
        for (int i = 0; i < ranges.length; i += 2) {
            int ini = ranges[i];
            int fim = ranges[i + 1];
            result = result || c >= ini && c <= fim;
        }

        return false;
    }
}
