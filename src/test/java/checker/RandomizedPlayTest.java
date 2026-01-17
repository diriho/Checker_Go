package checker;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class RandomizedPlayTest {

    @BeforeAll
    public static void setupFx() {
        TestUtil.initFx();
    }

    @Test
    public void randomizedTwentyMovesStayConsistentBetweenUIAndVirtualBoard() {
        TestUtil.runOnFxAndWait(() -> {
            Pane pane = new Pane();
            Board board = new Board(pane, new Color[]{Color.BEIGE, Color.BROWN});

            // Players (we will construct per color as needed)
            HumanPlayer black = new HumanPlayer(board, Color.BLACK);
            HumanPlayer white = new HumanPlayer(board, Color.WHITE);
            black.setOnTurnEnd(() -> {});
            white.setOnTurnEnd(() -> {});

            VirtualBoard vBoard = new VirtualBoard(board);
            Random rnd = new Random(42); // deterministic

            for (int turn = 0; turn < 20; turn++) {
                boolean whiteTurn = (turn % 2 == 1);
                byte active = whiteTurn ? VirtualBoard.WHITE_MAN : VirtualBoard.BLACK_MAN;
                List<VirtualBoard.VMove> legal = vBoard.getLegalMoves(active);
                assertFalse(legal.isEmpty(), "There should be at least one legal move");

                VirtualBoard.VMove chosen = legal.get(rnd.nextInt(legal.size()));

                // Apply to VirtualBoard with chain if needed (mandatory multiple captures)
                boolean turnComplete = vBoard.applyMove(chosen);
                while (!turnComplete) {
                    // continue captures from the same piece position
                    List<VirtualBoard.VMove> nextMoves = vBoard.getLegalMoves(active);
                    // filter for moves starting at the last toR,toC
                    VirtualBoard.VMove chain = null;
                    for (VirtualBoard.VMove m : nextMoves) {
                        if (m.isCapture && m.fromR == chosen.toR && m.fromC == chosen.toC) {
                            chain = m; break;
                        }
                    }
                    assertNotNull(chain, "Expected a follow-up capture move for chain");
                    chosen = chain;
                    turnComplete = vBoard.applyMove(chosen);
                }

                // Apply same sequence to real Board using HumanPlayer methods
                HumanPlayer actor = whiteTurn ? white : black;
                actor.setTurn(true);

                // Re-create starting piece reference from real board
                Pierce start = board.getMyPierces()[chosen.fromR][chosen.fromC];
                assertNotNull(start, "Real board should have the starting piece");
                actor.selectedPiece = start;

                // If capture, compute mid square
                if (chosen.isCapture) {
                    int midR = (chosen.fromR + chosen.toR) / 2;
                    int midC = (chosen.fromC + chosen.toC) / 2;
                    actor.executeCaptureMove(chosen.fromR, chosen.fromC, chosen.toR, chosen.toC, midR, midC);

                    // Chain captures if any
                    boolean chainNeeded = actor.forcedPiece != null && actor.mustCapture;
                    while (chainNeeded) {
                        // Find legal follow-up capture for this piece in VirtualBoard
                        List<VirtualBoard.VMove> follow = vBoard.getLegalMoves(active);
                        VirtualBoard.VMove next = null;
                        for (VirtualBoard.VMove m : follow) {
                            if (m.isCapture && m.fromR == chosen.toR && m.fromC == chosen.toC) {
                                next = m; break;
                            }
                        }
                        if (next == null) break; // no further captures
                        int mid2R = (next.fromR + next.toR) / 2;
                        int mid2C = (next.fromC + next.toC) / 2;
                        actor.executeCaptureMove(next.fromR, next.fromC, next.toR, next.toC, mid2R, mid2C);
                        chosen = next; // advance pointer
                        chainNeeded = actor.forcedPiece != null && actor.mustCapture;
                    }
                } else {
                    actor.executeSimpleMove(chosen.fromR, chosen.fromC, chosen.toR, chosen.toC);
                }

                actor.setTurn(false);

                // Validate that real board equals virtual board state
                VirtualBoard check = new VirtualBoard(board);
                for (int r = 0; r < 10; r++) {
                    for (int c = 0; c < 10; c++) {
                        byte vb = vBoard.grid[r][c];
                        byte rb;
                        Pierce p = board.getMyPierces()[r][c];
                        if (p == null) rb = VirtualBoard.EMPTY;
                        else if (p.getColor().equals(Color.BLACK)) rb = p.isKing() ? VirtualBoard.BLACK_KING : VirtualBoard.BLACK_MAN;
                        else rb = p.isKing() ? VirtualBoard.WHITE_KING : VirtualBoard.WHITE_MAN;
                        assertEquals(vb, rb, "Mismatch at (" + r + "," + c + ") after move " + turn);
                    }
                }
            }
        });
    }
}
