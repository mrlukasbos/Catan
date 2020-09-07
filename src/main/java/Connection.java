import org.java_websocket.WebSocket;
import java.nio.channels.AsynchronousSocketChannel;

abstract class Connection {

    abstract boolean isOpen();
    abstract void send(String message);
    abstract void close();

    WebSocket getWebSocket() {
        return null;
    }

    AsynchronousSocketChannel getSocket() {
        return null;
    }
}
