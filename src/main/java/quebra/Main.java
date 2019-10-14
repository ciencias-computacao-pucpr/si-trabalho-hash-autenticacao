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

public class Main {

    private List<Character> alfabeto;
    private CLI cli = new DefaultCLI();

    public static void main(String[] args) {
        Main m = new Main();
        m.parseArgs(String.join("", args));

        m.alfabeto = m.cli.getAlphabet();

        UsuarioDao dao = new UsuarioDaoArquivo("politica_fraca_senhas.txt");
        Duration tempoTotal = m.temporizar(() -> {
            for (Usuario usuario : dao.todos()) {
                Duration tempoUsuario = m.temporizar(() -> {
                    String senha = m.encontrarCombinacao(usuario.senha, m.cli.multiThread());
                    System.out.println("Senha encontrada: " + senha);
                });
                System.out.println("Tempo senha do usuário " + usuario.nome + ": " + tempoUsuario);
            }
        });
        System.out.println();
        System.out.println("Tempo total para quebrar 4 senhas: " + tempoTotal.toString());

    }

    private void parseArgs(String z) {
        if (z.contains("u")) {
            cli = new WithUpperCase(cli);
        }
        if (z.contains("n")) {
            cli = new WithNumbers(cli);
        }
        if (z.contains("s")) {
            cli = new WithSymbols(cli);
        }
        if (z.contains("m")) {
            cli = new MultiThread(cli);
        }
    }

    private Duration temporizar(Runnable runnable) {
        Instant ini = Instant.now();
        runnable.run();
        Instant fim = Instant.now();
        return Duration.between(ini, fim);
    }

    private String encontrarCombinacao(String hash, boolean multiThread) {
        List<Future<String>> futures = new ArrayList<>();
        AtomicBoolean encontrado = new AtomicBoolean(false);
        int cores = multiThread ? Runtime.getRuntime().availableProcessors() : 1;
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

    private String encontrarCombinacao(AtomicBoolean encontrado, String prefix, String hash, int k) {
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

}
