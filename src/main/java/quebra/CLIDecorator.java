package quebra;

import java.util.List;

public abstract class CLIDecorator implements CLI {
    private CLI instanceToBeDecorated;

    CLIDecorator(CLI toBeDocorated) {
        this.instanceToBeDecorated = toBeDocorated;
    }

    @Override
    public List<Character> getAlphabet() {
        return instanceToBeDecorated.getAlphabet();
    }

    @Override
    public boolean multiThread() {
        return false;
    }
}
