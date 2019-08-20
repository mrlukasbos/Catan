import java.util.ArrayList;
import java.util.HashMap;

public class DiceThrowPhase implements GamePhase {
    Game game;

    DiceThrowPhase(Game game) {
        this.game = game;
    }

    public Phase getPhaseType() {
        return Phase.THROW_DICE;
    }

    public Phase execute() {
        Dice d = new Dice(2);
        int dice = d.throwDice();
        game.setLastDiceThrow(dice);
        game.addEvent(new Event(game, EventType.GENERAL, game.getCurrentPlayer()).withGeneralMessage(" has thrown " + dice));

        if (dice == 7) {
            game.signalGameChange();
            return Phase.MOVE_BANDIT;
        } else {
            distributeResourcesForDice(dice);

            game.signalGameChange();
            return Phase.TRADING;
        }
    }

    // Give all the resources to the players for a given dicethrow
    private void distributeResourcesForDice(int diceThrow) {

        // create a hashmap for each player (for the logging)
        ArrayList<HashMap<Resource, Integer>> resourcesForPlayerId = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            resourcesForPlayerId.add(player.getId(), new HashMap<>());
        }

        for (Tile tile : game.getBoard().getTilesForDiceNumber(diceThrow)) {
            for (Node node : game.getBoard().getNodes(tile)) {
                // TODO This needs some looking into, currently each tile returns 18 nodes. These are obviously duplicates created during board.init()
                // game.print("nodes for tile " + game.getBoard().getNodes(tile).size());
                if (node.hasPlayer() && node.hasStructure()) {
                    int amount = node.getStructure() == Structure.CITY ? 2 : 1;

                    // TODO make this into a tile.getResource()
                    Resource resource = Enum.valueOf(Resource.class, tile.getType().toString());

                    node.getPlayer().addResources(resource, amount);
                    resourcesForPlayerId.get(node.getPlayer().getId()).put(resource, amount);
                }
            }
        }

        // put hashmaps to the game events
        for (Player player : game.getPlayers()) {
            game.addEvent(new Event(game, EventType.GET_RESOURCES, player).withResources(resourcesForPlayerId.get(player.getId())));
        }
    }
}

