package quebra;

import java.util.ArrayList;
import java.util.List;

public class WithUpperCase extends CLIDecorator {

    WithUpperCase(CLI toBeDocorated) {
        super(toBeDocorated);
    }

    @Override
    public List<Character> getAlphabet() {
        ArrayList<Character> characters = new ArrayList<>();

        for (char c = 'A'; c <= 'Z'; c++) {
            characters.add(c);
        }

        characters.addAll(super.getAlphabet());

        return characters;
    }
}
