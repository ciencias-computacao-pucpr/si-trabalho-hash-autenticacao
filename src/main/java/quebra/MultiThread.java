package quebra;

public class MultiThread extends CLIDecorator {
    MultiThread(CLI toBeDocorated) {
        super(toBeDocorated);
    }

    @Override
    public boolean multiThread() {
        return true;
    }
}
