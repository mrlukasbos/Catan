import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.channels.AsynchronousSocketChannel;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;



public class SocketConnectionManagerTest {

    @Test
    void itConnectsNewConnections() {
        SocketConnectionManagerStub socketConnectionManager = new SocketConnectionManagerStub(10006);
        socketConnectionManager.start();

        Socket s = null;
        DataOutputStream dout = null;
        try {
            s = new Socket("localhost", 10006);
            dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF("Hello\r\ngoodbye");
            dout.flush();
            dout.close();
            s.close();
            while (!s.isClosed()) {}
            System.out.println("closed this socket");
        } catch (Exception e) {
            System.out.println(e);
        }

        await().atMost(1, SECONDS).until(() -> socketConnectionManager.actions.equals("opened-Hello-goodbye-closed-"));
        assertEquals("opened-Hello-goodbye-closed-", socketConnectionManager.actions);
    }
}

class SocketConnectionManagerStub extends SocketConnectionManager {
    public String actions = "";

    SocketConnectionManagerStub(int port) {
        super(port);
    }

    @Override
    void onOpen(AsynchronousSocketChannel conn) {
        actions += "opened-";
    }

    @Override
    void onClose(AsynchronousSocketChannel conn) {
        actions += "closed-";
    }

    @Override
    void onMessage(AsynchronousSocketChannel conn, String message) {
        actions += (message + "-");
    }
}
