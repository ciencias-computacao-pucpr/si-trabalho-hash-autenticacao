package autenticacao;

public class PoliticaSenhaForte implements PoliticaSenha {
    @Override
    public boolean validarSenha(String senha) {
        return temCaracterEspecial(senha)
                && temLetraMaiuscula(senha)
                && temLetraMinuscula(senha)
                && temNumero(senha)
                && isCompridaOSuficiente(senha);
    }

    private boolean temLetraMinuscula(String senha) {
        return senha.matches(".*[a-z]+.*");
    }
    private boolean temLetraMaiuscula(String senha) {
        return senha.matches(".*[A-Z]+.*");
    }
    private boolean temNumero(String senha) {
        return senha.matches(".*[0-9]+.*");
    }
    private boolean temCaracterEspecial(String senha) {
        return senha.matches(".*[^a-zA-Z0-9]+.*");
    }
    private boolean isCompridaOSuficiente(String senha) {
        return senha.length() >= 8;
    }
}
