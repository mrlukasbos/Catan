import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculateScoreTest {
    private Game game = new Game(new Interface(8888));

    @Test
    void getPlayerScore() {
        game.startGame();
        Player player = new Player(game, 1, "test");
        game.addPlayer(player);

        assertEquals(0, player.getVictoryPoints());

        Board board = game.getBoard();
        board.placeVillage(player, board.getNode("([1,2],[2,1],[2,2])"));
        board.placeCity(player, board.getNode("([3,3],[3,4],[4,3])"));
        player.addDevelopmentCard(DevelopmentCard.VICTORY_POINT);
        player.useDevelopmentCard(DevelopmentCard.VICTORY_POINT);

        assertEquals(4, player.getVictoryPoints());
    }

    @Test
    void assignLongestRoadScore() {
        game.startGame();
        Player player = new Player(game, 1, "test");
        game.addPlayer(player);

        assertEquals(0, player.getVictoryPoints());

        assertTrue(false);

        // TODO longest road calculation
        /*
            1. Find circles
                -> If a circle is found split it at both sides of the 3rd grade node
                -> Continue searching for more circles recursively
            2. For each resulting tree(s) calculate the distances between each leaf. (every 1st grade node is a leaf)
         */
    }

    @Test
    void assignLargestArmyScore() {
        game.startGame();
        Player player = new Player(game, 1, "test");
        game.addPlayer(player);
        Player player2 = new Player(game, 2, "test2");
        game.addPlayer(player2);

        assertEquals(0, player.getVictoryPoints());

        for (int i = 0; i < 2; i++) {
            player.addDevelopmentCard(DevelopmentCard.KNIGHT);
            player.useDevelopmentCard(DevelopmentCard.KNIGHT);
        }
        game.assignLargestArmyAward();

        assertEquals(0, player.getVictoryPoints());

        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        player.useDevelopmentCard(DevelopmentCard.KNIGHT);
        game.assignLargestArmyAward();

        // Only awards largest army points when a player has at least 3 armies
        assertEquals(2, player.getVictoryPoints());
        assertEquals(0, player2.getVictoryPoints());

        for (int i = 0; i < 3; i++) {
            player2.addDevelopmentCard(DevelopmentCard.KNIGHT);
            player2.useDevelopmentCard(DevelopmentCard.KNIGHT);
        }
        game.assignLargestArmyAward();

        assertEquals(2, player.getVictoryPoints());
        assertEquals(0, player2.getVictoryPoints());

        player2.addDevelopmentCard(DevelopmentCard.KNIGHT);
        player2.useDevelopmentCard(DevelopmentCard.KNIGHT);
        game.assignLargestArmyAward();

        // Only changes largest army award when player surpasses other player
        assertEquals(0, player.getVictoryPoints());
        assertEquals(2, player2.getVictoryPoints());
    }

    @Test
    void calculateVillagesScore() {
        game.startGame();
        Player player = new Player(game, 1, "test");
        game.addPlayer(player);

        assertEquals(0, player.getVictoryPoints());

        Board board = game.getBoard();
        board.placeVillage(player, board.getNode("([1,2],[2,1],[2,2])"));

        assertEquals(1, player.getVictoryPoints());
    }

    @Test
    void calculateCitiesScore() {
        game.startGame();
        Player player = new Player(game, 1, "test");
        game.addPlayer(player);

        assertEquals(0, player.getVictoryPoints());

        Board board = game.getBoard();
        board.placeCity(player, board.getNode("([3,3],[3,4],[4,3])"));

        assertEquals(2, player.getVictoryPoints());
    }

    @Test
    void calculateDevelopmentcardsScore() {
        game.startGame();
        Player player = new Player(game, 1, "test");
        game.addPlayer(player);

        assertEquals(0, player.getVictoryPoints());

        player.addDevelopmentCard(DevelopmentCard.VICTORY_POINT);
        player.useDevelopmentCard(DevelopmentCard.VICTORY_POINT);

        assertEquals(1, player.getVictoryPoints());
    }

    @Test
    void getsWinner() {
        game.startGame();
        Player player = new Player(game, 1, "test");
        game.addPlayer(player);

        assertNull(game.getWinner());

        for (int i = 0; i < 10; i++) {
            player.addDevelopmentCard(DevelopmentCard.VICTORY_POINT);
            player.useDevelopmentCard(DevelopmentCard.VICTORY_POINT);
        }

        assertEquals(player, game.getWinner());
    }
}
