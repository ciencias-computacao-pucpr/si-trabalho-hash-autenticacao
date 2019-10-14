package quebra;

import java.util.ArrayList;
import java.util.List;

public class DefaultCLI implements CLI {
    @Override
    public List<Character> getAlphabet() {
        final List<Character> r = new ArrayList<>();
        for (char c = 'a'; c <= 'z'; c++) {
            r.add(c);
        }
        return r;
    }

    @Override
    public boolean multiThread() {
        return false;
    }
}
