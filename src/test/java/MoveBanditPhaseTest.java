import com.google.gson.JsonArray;
import communication.WebSocketConnectionServer;
import game.Game;
import game.Phase;
import game.phases.MoveBanditPhase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Constants;
import utils.jsonValidator;

import static org.junit.jupiter.api.Assertions.*;

class MoveBanditPhaseTest {
    private WebSocketConnectionServer iface = new WebSocketConnectionServer(10007);
    private Game game = new Game(iface);
    private MoveBanditPhase moveBanditPhase = new MoveBanditPhase(game);
    private  PlayerStub player = new PlayerStub(game,0, "tester");
    private  PlayerStub player2 = new PlayerStub(game,1, "tester1");

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
        JsonArray jsonArray = jsonValidator.getAsJsonArray(message);
        assertTrue(moveBanditPhase.moveIsValid(jsonArray));
        moveBanditPhase.move(player, jsonArray);
        assertEquals(game.getLastResponse().getCode(), Constants.OK.getCode());
    }

    @Test
    void playerCannotPlaceBanditOnSeaTile() {
        String message = "[{ \"location\": \"" + game.getBoard().getTiles().get(0).getKey() + "\" }]";
        JsonArray jsonArray = jsonValidator.getAsJsonArray(message);
        assertFalse(moveBanditPhase.moveIsValid(jsonArray));
        assertEquals(game.getLastResponse().getCode(), Constants.CAN_NOT_PLACE_BANDIT_ON_SEA_TILE_ERROR.getCode());
    }

    @Test
    void playerCannotPlaceBanditOnSameTile() {
        String message = "[{ \"location\": \"" + game.getBoard().getBandit().getTile().getKey() + "\" }]";
        JsonArray jsonArray = jsonValidator.getAsJsonArray(message);
        assertFalse(moveBanditPhase.moveIsValid(jsonArray));
        assertEquals(game.getLastResponse().getCode(), Constants.CAN_NOT_PLACE_BANDIT_ON_SAME_TILE_ERROR.getCode());
    }

    @Test
    void itHandlesUserCommandsTest() {
        String message = "[{ \"location\": \"" + game.getBoard().getTilesForDiceNumber(8).get(0).getKey() + "\" }]";
        player.setBufferedReply(message);
        JsonArray jsonArray = moveBanditPhase.getValidCommandFromUser(player);
        assertTrue(moveBanditPhase.moveIsValid(jsonArray));
        moveBanditPhase.move(player, jsonArray);
        assertEquals(game.getLastResponse().getCode(), Constants.OK.getCode());
    }
}
