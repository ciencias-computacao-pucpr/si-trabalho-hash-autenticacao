package autenticacao;

public class PoliticaSenha4Caracteres implements PoliticaSenha {
    @Override
    public boolean validarSenha(String senha) {
        return senha.length() == 4;
    }
}
