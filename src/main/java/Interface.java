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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class Interface extends WebSocketServer {

    boolean readyToStart = false;

    public Interface(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        if (message.contains("START")) {
            readyToStart = true;
            print("Received START signal");
        } else if (message.contains("STOP")) {
            readyToStart = false;
            print("Received STOP signal");
        }
    }

    boolean isReadyToStart() {
        return readyToStart;
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        broadcast( message.array() );
        print(conn + ": " + message );
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
        try {
            print("Visualization started on:" + InetAddress.getLocalHost() + ":" + getPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        setConnectionLostTimeout(10);
    }

    void broadcast(broadcastType type, String str) {
        broadcast(broadcastTypeToString(type) + str);
    }

    String broadcastTypeToString(broadcastType type) {
        switch (type) {
            case INFO: return "INF";
            case COMMAND: return "CMD";
            case SETTING: return "SET";
            case COMMUNICATION: return "COM";
            case BOARD: return "BOA";
            case PLAYERS: return "PLA";
            case STATUS: return "STA";

        }
        return "";
    }

    public void shutDown() {
        try {
            stop();
        } catch (Exception f) {
            f.printStackTrace();
        }
    }

    private void print(String msg) {
        System.out.println("[Iface] \t" + msg);
    }
}

enum broadcastType {
    SETTING,
    COMMAND,
    INFO,
    COMMUNICATION,
    BOARD,
    PLAYERS,
    STATUS
}