package autenticacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Main {

    private static final String arquivoSenhas = "senhas.txt";
    private static BufferedReader reader;
    private static PoliticaSenha politicaSenha;
    private static UsuarioDao dao;

    public static void main(String[] args) throws IOException {
        reader = new BufferedReader(new InputStreamReader(System.in));
        if (args.length > 0) {
            if (Objects.equals(args[0], "forte")) {
                politicaSenha = new PoliticaSenhaForte();
            } else {
                politicaSenha = new PoliticaSenha4Caracteres();
            }
        } else {
            politicaSenha = new PoliticaSenha4Caracteres();
        }
        dao = new UsuarioDaoArquivo(arquivoSenhas);
        userInterface();
    }


    public static void userInterface () throws IOException {
        mostrarOpcoes();

        String s;
        while ((s = reader.readLine()) != null && !s.isEmpty()) {
            String opcao = s.trim();
            if ("1".equals(opcao)) cadastrar();
            else if ("2".equals(opcao)) login();
            else System.out.println("Opção inválida");

            mostrarOpcoes();
        }
    }

    private static void mostrarOpcoes() {
        System.out.println("1 - Cadastrar");
        System.out.println("2 - Login");
        System.out.println();
        System.out.print("Digite uma opção:");
    }

    private static void login() throws IOException {
        String nomeUsuario, senhaUsuarioDigitado;
        System.out.println("Digita 0 para cancelar");
        Usuario usuario = null;
        do {
            do {
                System.out.print("Usuário: ");
                nomeUsuario = reader.readLine();
                if ("0".equals(nomeUsuario)) return;
            } while (isUsuarioValido(nomeUsuario));

            do {
                System.out.print("Senha: ");
                senhaUsuarioDigitado = reader.readLine();
                if ("0".equals(senhaUsuarioDigitado)) return;
            } while (!politicaSenha.validarSenha(senhaUsuarioDigitado));

            usuario = dao.pesquisar(nomeUsuario);
            if (!autenticar(usuario, senhaUsuarioDigitado)) {
                System.out.println("Usuário ou senha inválidos");
            }

        } while (!autenticar(usuario, senhaUsuarioDigitado));

        System.out.println("Usuário autenticado com sucesso");

    }

    private static boolean autenticar(Usuario usuario, String senhaUsuario) {
        return usuario != null && usuario.senha.equals(md5(senhaUsuario));
    }

    private static void cadastrar() throws IOException {
        String nomeUsuario, senhaUsuario;
        System.out.println("Digita 0 para cancelar");
        do {
            System.out.print("Usuário: ");
            nomeUsuario = reader.readLine();
            if ("0".equals(nomeUsuario)) return;
            if (isUsuarioValido(nomeUsuario)) System.out.println("Usuário inválido.");
            if (isUsuarioUtilizado(nomeUsuario)) System.out.println("Usuário já cadastrado");
        } while(isUsuarioValido(nomeUsuario) || isUsuarioUtilizado(nomeUsuario));

        do {
            System.out.print("Senha: ");
            senhaUsuario = reader.readLine();
            if ("0".equals(senhaUsuario)) return;
            if (isUsuarioValido(nomeUsuario)) System.out.println("Senha inválida.");
        } while(!politicaSenha.validarSenha(senhaUsuario));

        Usuario usuario = new Usuario(nomeUsuario, md5(senhaUsuario));
        dao.salvar(usuario);
    }

    private static boolean isUsuarioUtilizado(String usuario) {
        return dao.pesquisar(usuario) != null;
    }

    private static boolean isUsuarioValido(String usuario) {
        return usuario == null || usuario.length() <= 3 || usuario.contains(":");
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
