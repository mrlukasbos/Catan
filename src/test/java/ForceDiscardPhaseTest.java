import com.google.gson.JsonArray;
import communication.WebSocketConnectionServer;
import game.Game;
import game.Phase;
import game.Resource;
import game.phases.ForceDiscardPhase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Constants;
import utils.jsonValidator;

import static org.junit.jupiter.api.Assertions.*;

class ForceDiscardPhaseTest {
    private WebSocketConnectionServer iface = new WebSocketConnectionServer(10007);
    private Game game = new Game(iface);
    private ForceDiscardPhase forceDiscardPhase = new ForceDiscardPhase(game);
    private  PlayerStub player = new PlayerStub(game,0, "tester");
    private  PlayerStub player2 = new PlayerStub(game,1, "tester1");

    @BeforeEach
    void beforeTest() {
        game.addPlayer(player);
        game.addPlayer(player2);
        game.setCurrentPlayer(player);
        game.startGame();
    }

    @Test
    void phaseNameShouldBeCorrectTest() {
        assertEquals(forceDiscardPhase.getPhaseType(), Phase.FORCE_DISCARD);
    }

    @Test
    void nextPhaseIsMoveBanditTest() {
        Phase nextPhase = forceDiscardPhase.execute();
        assertEquals(Phase.MOVE_BANDIT, nextPhase);
    }

    @Test
    void playerCanDiscardResourcesTest() {
        player.addResources(Resource.GRAIN, 8);

        String message = " [{\"type\":\"grain\", \"value\": 4}]";
        JsonArray jsonArray = jsonValidator.getAsJsonArray(message);
        forceDiscardPhase.discard(player, jsonArray);

        assertEquals(4, player.countResources(Resource.GRAIN));
    }

    @Test
    void playersMustDiscardHalfTheirResourcesTest() {
        player.addResources(Resource.GRAIN, 8);

        String message = " [{\"type\":\"grain\", \"value\": 3}]";
        JsonArray jsonArray = jsonValidator.getAsJsonArray(message);
        assertFalse(forceDiscardPhase.discardIsValid(player, jsonArray));
        assertEquals(game.getLastResponse().getCode(), Constants.NOT_ENOUGH_RESOURCES_DISCARDED_ERROR.getCode());

        message = " [{\"type\":\"grain\", \"value\": 4}]";
        jsonArray = jsonValidator.getAsJsonArray(message);
        assertTrue(forceDiscardPhase.discardIsValid(player, jsonArray));
    }

    @Test
    void onlyPlayersWithMoreThanSevenResourcesShouldDiscardTest() {
        player.addResources(Resource.GRAIN, 7);
        assertEquals(Phase.MOVE_BANDIT, forceDiscardPhase.execute());
    }

    @Test
    void playerCanOnlyDiscardCardsAlreadyInHand() {
        player.addResources(Resource.GRAIN, 8);

        String message = " [{\"type\":\"grain\", \"value\": 9}]";
        JsonArray jsonArray = jsonValidator.getAsJsonArray(message);
        assertFalse(forceDiscardPhase.discardIsValid(player, jsonArray));
        assertEquals(game.getLastResponse().getCode(), Constants.MORE_RESOURCES_DISCARDED_THAN_OWNED_ERROR.getCode());
    }
}
