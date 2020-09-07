import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WebSocketConnectionServer extends WebSocketServer {
    private GameManager gameManager;
    private SocketConnectionServer socketConnectionServer;

    HashMap<Player, Boolean> registeredPlayers = new HashMap<>();

    // Create an interface for a specific port
    WebSocketConnectionServer(int port) {
        super(new InetSocketAddress(port));
        setReuseAddr(true);
    }

    // Start the interface thread
    void start(SocketConnectionServer socketServer, GameManager gameManager) {
        this.gameManager = gameManager;
        this.socketConnectionServer = socketServer;
        start();
    }

    // When the visualization starts broadcasting show a message where we can connect to it
    @Override
    public void onStart() {
        try {
            print("Visualization started on:" + InetAddress.getLocalHost() + ":" + getPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        setConnectionLostTimeout(10);
    }


    // When a new connection is opened we have to transmit the game data to it
    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        broadcast(gameManager.getCurrentGame().toString());
    }

    // Receive messages from the interface
    // Currently we can receive start and stop signals from it to start/stop the game.
    @Override
    public void onMessage( WebSocket conn, String message ) {
        JsonParser parser = new JsonParser();
        JsonElement elem = parser.parse(message);
        JsonObject obj = elem.getAsJsonObject();

        String model = obj.get("model").getAsString();

        switch(model) {
            case "join": {
                JsonObject attrs = obj.get("attributes").getAsJsonObject();
                int connectionId = attrs.get("id").getAsInt();
                boolean reconnection = false;
                Player player = null;
                for (Player p : registeredPlayers.keySet()) {
                    if (connectionId == p.getId()) {

               //         print("reconnecting player: " + p.getName() + " using id: " + p.getId());
             //           p.setConnection(conn); // apparently a reconnect: renew the connection

                        // if the player was not joined in the game it must be added again
                        if (!registeredPlayers.get(p)) {
                            gameManager.getCurrentGame().addPlayer(p);
                        }
                        registeredPlayers.replace(p, true);

                        reconnection = true;
                        player = p;
                        break;
                    }
                }

                if (!reconnection || !registeredPlayers.getOrDefault(player, true)) {
                    registerPlayer(conn, attrs.get("name").getAsString());
                }
                gameManager.getCurrentGame().signalGameChange();
                break;

            }
            case "leave": {
                // todo use the new json validator validations here
                JsonObject attrs = obj.get("attributes").getAsJsonObject();
                int connectionId = attrs.get("id").getAsInt();
                for (Player p : registeredPlayers.keySet()) {
                    if (connectionId == p.getId()) {
                        print("removing player: " + p.getName() + " using id: " + p.getId());
                        gameManager.getCurrentGame().removePlayer(p);
                        gameManager.getCurrentGame().signalGameChange();
                        registeredPlayers.replace(p, false);
                    }
                }
                break;
            }
            case "control": {
                // todo use the new json validator validations here
                JsonObject attrs = obj.get("attributes").getAsJsonObject();
                handleControl(attrs.get("command").getAsString());
                break;
            }
            case "client-response": {
                for (Player player : registeredPlayers.keySet()) {
                    if (conn.equals(player.getConnection().getWebSocket())) {
                        // todo use the new json validator validations here
                        JsonArray arr = obj.get("attributes").getAsJsonArray();
                        String buildRequest = arr.toString();
                        player.setBufferedReply(buildRequest);
                    }
                }
            }
            default: break;
        }
    }

    void handleControl(String command) {
        if (command.contains("START")) {
            print("Received START signal");
            if (gameManager.getCurrentGame().getPlayers().size() >= Constants.MINIMUM_AMOUNT_OF_PLAYERS && !gameManager.getCurrentGame().isRunning()) {
                gameManager.startGame();
            } else {
                print("not enough players to start with");
            }
        } else if (command.contains("STOP")) {
            print("Received STOP signal");
            gameManager.stopGame();
        } else {
            print(command);
        }
    }

    void registerPlayer(WebSocket conn, String name) {
        WebSocketConnection connection = new WebSocketConnection(conn);
        Player newPlayer = new Player(connection, gameManager.getCurrentGame(), gameManager.getCurrentGame().getPlayers().size(), name);

        if (!gameManager.getCurrentGame().isRunning()) {
            print("Registering new interface player: " + name);
            gameManager.getCurrentGame().addPlayer(newPlayer);
            registeredPlayers.put(newPlayer, true);
            Response idAcknowledgement = Constants.ID_ACK.withAdditionalInfo("" + newPlayer.getId());
            newPlayer.send(idAcknowledgement.toString());

        } else {
            print("warning! we must maybe reply with an error here");
        }
    }

    // Receive messages from the interface
    // This callback is currently not used
    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        broadcast( message.array() );
        print(conn + ": " + message );
    }

    // When there is a problem with the connection print it
    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        for (Player player : registeredPlayers.keySet()) {
//            if (player.getConnection().equals(conn)) {
//                print("lost connection with player: " + player.getName() + " using id: " + player.getId());
//            }
        }
    }

    private void print(String msg) {
        System.out.println("[Iface] \t" + msg);
    }
}