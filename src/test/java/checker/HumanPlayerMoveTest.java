package checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class HumanPlayerMoveTest {

    @BeforeAll
    public static void setupFx() {
        TestUtil.initFx();
    }

    @Test
    public void testSimpleForwardMoveUpdatesBoardAndUI() {
        TestUtil.runOnFxAndWait(() -> {
            Pane pane = new Pane();
            Board board = new Board(pane, new Color[]{Color.BEIGE, Color.BROWN});
            HumanPlayer black = new HumanPlayer(board, Color.BLACK);
            black.setTurn(true);

            // Find a black piece that can move forward one step
            int fromR = -1, fromC = -1, toR = -1, toC = -1;
            Pierce sel = null;
            Pierce[][] grid = board.getMyPierces();
            for (int r = 0; r < 10 && sel == null; r++) {
                for (int c = 0; c < 10; c++) {
                    Pierce p = grid[r][c];
                    if (p != null && p.getColor().equals(Color.BLACK)) {
                        int nr = r + 1; // black moves down
                        if (nr < 10) {
                            for (int dc : new int[]{-1, 1}) {
                                int nc = c + dc;
                                if (nc >= 0 && nc < 10 && grid[nr][nc] == null) {
                                    sel = p; fromR = r; fromC = c; toR = nr; toC = nc; break;
                                }
                            }
                        }
                    }
                    if (sel != null) break;
                }
            }
            assertNotNull(sel, "Found a movable black piece");

            // Select and move
            black.selectedPiece = sel; // set selection
            black.executeSimpleMove(fromR, fromC, toR, toC);

            // Board grid updated
            assertNull(grid[fromR][fromC], "Old square should be empty after move");
            assertEquals(sel, grid[toR][toC], "Piece should be at target square");

            // UI updated (pixel coords)
            int expectedX = toC * 50 + 45;
            int expectedY = toR * 50 + 45;
            int[] coords = sel.getPierceCoords();
            assertEquals(expectedX, coords[0], "Piece X updated");
            assertEquals(expectedY, coords[1], "Piece Y updated");
        });
    }

    @Test
    public void testCaptureRemovesEnemyAndUpdatesBoardAndUI() {
        TestUtil.runOnFxAndWait(() -> {
            Pane pane = new Pane();
            Board board = new Board(pane, new Color[]{Color.BEIGE, Color.BROWN});
            HumanPlayer black = new HumanPlayer(board, Color.BLACK);
            black.setTurn(true);

            Pierce[][] grid = board.getMyPierces();

            // Construct a capture scenario: choose a black piece near row 3, place a white piece diagonally ahead
            int br = -1, bc = -1;
            Pierce bp = null;
            for (int c = 1; c < 9; c++) { // row 3 exists by setup
                if (grid[3][c] != null && grid[3][c].getColor().equals(Color.BLACK)) {
                    bp = grid[3][c]; br = 3; bc = c; break;
                }
            }
            assertNotNull(bp, "Found a black piece on row 3");

            // Place a white piece at (4, bc+1) if in bounds and empty; target capture to (5, bc+2)
            int midR = br + 1;
            int midC = bc + 1;
            int tr = br + 2;
            int tc = bc + 2;
            assertTrue(midR < 10 && midC < 10 && tr < 10 && tc < 10, "Capture path in bounds");

            // Ensure target empty
            grid[tr][tc] = null;
            // Create and place white piece at midpoint
            int midX = midC * 50 + 45;
            int midY = midR * 50 + 45;
            Pierce wp = new Pierce(Color.WHITE, midX, midY);
            grid[midR][midC] = wp;
            pane.getChildren().add(wp.getNode());

            // Pre counts
            int preChildren = pane.getChildren().size();

            // Execute capture
            black.selectedPiece = bp;
            black.executeCaptureMove(br, bc, tr, tc, midR, midC);

            // Board updates
            assertNull(grid[br][bc], "Origin should be empty after capture");
            assertEquals(bp, grid[tr][tc], "Black piece moved to target after capture");
            assertNull(grid[midR][midC], "Captured piece removed from grid");

            // UI removed captured node
            int postChildren = pane.getChildren().size();
            assertTrue(postChildren == preChildren - 1, "Pane should have one less child after capture");

            // Pixel coords of moving piece correct
            int[] coords = bp.getPierceCoords();
            assertEquals(tc * 50 + 45, coords[0]);
            assertEquals(tr * 50 + 45, coords[1]);
        });
    }
}
