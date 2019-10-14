package quebra;

import java.util.ArrayList;
import java.util.List;

public class WithSymbols extends CLIDecorator {
    WithSymbols(CLI toBeDocorated) {
        super(toBeDocorated);
    }

    @Override
    public List<Character> getAlphabet() {
        ArrayList<Character> characters = new ArrayList<>();

        for (char c = 0; c <= 255; c++) {
            if (!inRanges(c, 'a', 'z', 'A', 'Z', '0', '9'))
                characters.add(c);
        }

        characters.addAll(super.getAlphabet());

        return characters;
    }

    private boolean inRange(char c, char ini, char fim) {
        return c >= ini && c <= fim;
    }

    private boolean inRanges(char c, char... ranges) {
        for (char i = 0; i < ranges.length; i += 2) {
            if (inRange(c, ranges[i], ranges[i + 1])) {
                return true;
            }
        }
        return false;
    }
}
