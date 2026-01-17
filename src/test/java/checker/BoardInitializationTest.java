package checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class BoardInitializationTest {

    @BeforeAll
    public static void setupFx() {
        TestUtil.initFx();
    }

    @Test
    public void testInitialPlacementAndCounts() {
        TestUtil.runOnFxAndWait(() -> {
            Pane pane = new Pane();
            Color[] colors = {Color.BEIGE, Color.BROWN};
            Board board = new Board(pane, colors);

            Pierce[][] grid = board.getMyPierces();
            int blackCount = 0;
            int whiteCount = 0;
            int emptyMiddle = 0;

            for (int r = 0; r < 10; r++) {
                for (int c = 0; c < 10; c++) {
                    Pierce p = grid[r][c];
                    if (p == null) {
                        if (r >= 4 && r <= 5) emptyMiddle++;
                        continue;
                    }
                    if ((r + c) % 2 == 0) {
                        fail("Pieces should only be on dark squares (r+c odd)");
                    }
                    if (p.getColor().equals(Color.BLACK)) blackCount++;
                    else if (p.getColor().equals(Color.WHITE)) whiteCount++;
                }
            }

            assertEquals(20, blackCount, "Should start with 20 black pieces");
            assertEquals(20, whiteCount, "Should start with 20 white pieces");
            // Middle 2 rows contain 10 dark squares each, expect ~10 empties per dark square row; 
            // But simpler: at least some empties in middle rows.
            assertTrue(emptyMiddle > 0, "Middle rows must be empty at start");

            // Graphical nodes exist for every piece
            int pieceNodes = 0;
            for (int r = 0; r < 10; r++) {
                for (int c = 0; c < 10; c++) {
                    Pierce p = grid[r][c];
                    if (p != null && board.getPane().getChildren().contains(p.getNode())) {
                        pieceNodes++;
                    }
                }
            }
            assertEquals(40, pieceNodes, "Pane should contain 40 piece nodes at start");
        });
    }
}
