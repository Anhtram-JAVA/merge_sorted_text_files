import java.io.IOException;


public interface DataStack {
    public void close() throws IOException;

    public boolean empty();

    public String peek();

    public String pop() throws IOException;

}
