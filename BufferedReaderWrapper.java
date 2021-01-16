import java.io.BufferedReader;
import java.io.IOException;

/**
 * helper class to BufferedReader
 *
 */
public final class BufferedReaderWrapper implements DataStack {
    public BufferedReaderWrapper(BufferedReader r) throws IOException {
        this.reader = r;
        reload();
    }
    public void close() throws IOException {
        this.reader.close();
    }

    public boolean empty() {
        return this.cache == null;
    }

    public String peek() {
        return this.cache;
    }

    public String pop() throws IOException {
        String answer = peek().toString();
        reload();
        return answer;
    }

    private void reload() throws IOException {
        this.cache = this.reader.readLine();
    }

    private BufferedReader reader;

    private String cache;

}
