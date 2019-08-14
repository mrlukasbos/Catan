/*
A node is a junction between tiles. It can support settlements and cities in the game
 */

import java.util.Arrays;

public class Node {
    private Tile t;
    private Tile l;
    private Tile r;
    private Structure structure;
    private Player player;

    Node(Tile t, Tile r, Tile l) {
        this.t = t;
        this.r = r;
        this.l = l;
        this.structure = Structure.NONE;
    }

    double getDistanceToNode(Node otherNode) {
        // pick a surrounding node. We don't care which one as long as it's the same relative to the node.
        Tile a = this.t;
        Tile b = otherNode.t;

        // if one of them is uneven and one is not
        boolean aIsEven = (a.getY()%2 == 0);
        boolean bIsEven = (b.getY()%2 == 0);

        if (aIsEven ^ bIsEven) {
            return (a.getDistance(b) * 2) - 2;
        }

        return a.getDistance(b) * 2;
    }


    public Player getPlayer() {
        return player;
    }

    void setPlayer(Player player) {
        this.player = player;
    }

    boolean hasPlayer() {
        return player != null;
    }

    boolean hasStructure() {
        return structure != Structure.NONE;
    }

    public Structure getStructure() {
        return structure;
    }

    void setStructure(Structure structure) {
        this.structure = structure;
    }

    Tile[] getTiles() {
        Tile[] tiles = {t, l, r};
        return tiles;
    }

    String getKey() {
        Tile[] neighbors = {t, l, r};
        Arrays.sort(neighbors);
        return "(" + neighbors[0].getKey() + "," + neighbors[1].getKey() + "," + neighbors[2].getKey() + ")";
    }

    String structureToString(Structure structure) {
        switch (structure) {
            case NONE:
                return "none";
            case CITY:
                return "city";
            case SETTLEMENT:
                return "settlement";
            default:
                return "unknown";
        }
    }

    @java.lang.Override
    public java.lang.String toString() {
        String playerString = "";
        if (player != null) {
            playerString = "\"player\": " + player.getId() + "," +
                    "\"player_color\": \"" + player.getColor() + "\",";
        }
        return "{" +
                "\"model\": \"node\", " +
                "\"key\": \"" + getKey() + "\", " +
                "\"attributes\": {" +
                "\"structure\": \"" + structureToString(structure) + "\"," +
                playerString +
                "\"t_key\": \"" + t.getKey() + "\"," +
                "\"r_key\": \"" + r.getKey()+ "\"," +
                "\"l_key\": \"" + l.getKey() + "\"" +
                "}" +
                "}";
    }
}

enum Structure {
    NONE,
    SETTLEMENT,
    CITY,
    STREET
}