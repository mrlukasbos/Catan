/*
Communication with the visualization. There the game is shown and a game can be started there as well
 */


/*
 * Copyright (c) 2010-2019 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class Sock extends WebSocketServer {

    GameManager gm;
    Server server;

    public Sock( int port , GameManager gm, Server server) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
        this.gm = gm;
        this.server = server;
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " joined!" );
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        System.out.println( conn + " left" );
    }


    @Override
    public void onMessage( WebSocket conn, String message ) {
        if (message.contains("START")) {
            gm.start();
        } else if (message.contains("END")) {
            // first shut down the server, so we can kill the sockets which are hooked to the players
            server.shutDown();
            gm.end();
        }
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        broadcast( message.array() );
        System.out.println( conn + ": " + message );
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
    }
}
