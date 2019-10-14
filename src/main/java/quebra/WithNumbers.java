package quebra;

import java.util.ArrayList;
import java.util.List;

public class WithNumbers extends CLIDecorator {
    WithNumbers(CLI toBeDocorated) {
        super(toBeDocorated);
    }

    @Override
    public List<Character> getAlphabet() {
        ArrayList<Character> characters = new ArrayList<>();

        for (char c = '0'; c <= '9'; c++) {
            characters.add(c);
        }

        characters.addAll(super.getAlphabet());

        return characters;
    }
}
