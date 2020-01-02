import com.google.gson.JsonArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MoveBanditPhaseTest {
    private Interface iface = new Interface(10007);
    private Game game = new Game(iface);
    private MoveBanditPhase moveBanditPhase = new MoveBanditPhase(game);
    private  Player player = new Player(game,0, "tester");
    private  Player player2 = new Player(game,1, "tester1");

    @BeforeEach
    void beforeTest() {
        game.addPlayer(player);
        game.addPlayer(player2);
        game.setCurrentPlayer(player);
        game.init();
    }

    @Test
    void phaseNameShouldBeCorrectTest() {
        assertEquals(moveBanditPhase.getPhaseType(), Phase.MOVE_BANDIT);
    }

    @Test
    void playerCanPlaceBanditOnTile() {
        String message = "[{ \"location\": \"" + game.getBoard().getTilesForDiceNumber(8).get(0).getKey() + "\" }]";
        JsonArray jsonArray = new jsonValidator().getJsonIfValid(player, message);
        assertTrue(moveBanditPhase.moveIsValid(player, jsonArray));
        moveBanditPhase.move(player, jsonArray);
        assertEquals(game.getLastResponse().getCode(), Constants.OK.getCode());
    }

    @Test
    void requiresALocationValueTest() {
        String message = "[{}]";
        JsonArray jsonArray = new jsonValidator().getJsonIfValid(player, message);
        assertFalse(moveBanditPhase.moveIsValid(player, jsonArray));
        assertEquals(game.getLastResponse().getCode(), Constants.INVALID_BANDIT_MOVE_ERROR.getCode());
    }

    @Test
    void requiresAValidLocationValueTest() {
        String message = "[{ \"location\": {} }]";
        JsonArray jsonArray = new jsonValidator().getJsonIfValid(player, message);
        assertFalse(moveBanditPhase.moveIsValid(player, jsonArray));
        assertEquals(game.getLastResponse().getCode(), Constants.INVALID_BANDIT_MOVE_ERROR.getCode());

        message = "[{ \"location\": \"foo\" }]";
        jsonArray = new jsonValidator().getJsonIfValid(player, message);
        assertFalse(moveBanditPhase.moveIsValid(player, jsonArray));
        assertEquals(game.getLastResponse().getCode(), Constants.INVALID_BANDIT_MOVE_ERROR.getCode());
    }

    @Test
    void playerCannotPlaceBanditOnSeaTile() {
        String message = "[{ \"location\": \"" + game.getBoard().getTiles().get(0).getKey() + "\" }]";
        JsonArray jsonArray = new jsonValidator().getJsonIfValid(player, message);
        assertFalse(moveBanditPhase.moveIsValid(player, jsonArray));
        assertEquals(game.getLastResponse().getCode(), Constants.CAN_NOT_PLACE_BANDIT_ON_SEA_TILE_ERROR.getCode());
    }

    @Test
    void playerCannotPlaceBanditOnSameTile() {
        String message = "[{ \"location\": \"" + game.getBoard().getBandit().getTile().getKey() + "\" }]";
        JsonArray jsonArray = new jsonValidator().getJsonIfValid(player, message);
        assertFalse(moveBanditPhase.moveIsValid(player, jsonArray));
        assertEquals(game.getLastResponse().getCode(), Constants.CAN_NOT_PLACE_BANDIT_ON_SAME_TILE_ERROR.getCode());
    }
}
