package checker;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

/**
 * A lightweight board representation for AI calculations.
 * Decoupled from JavaFX.
 * 0 = Empty
 * 1 = Black Man
 * 2 = Black King
 * 3 = White Man
 * 4 = White King
 */
// VirtualBoard class to represent a lightweight version of the game board for AI calculations
public class VirtualBoard {
    public byte[][] grid;

    public static final byte EMPTY = 0;
    public static final byte BLACK_MAN = 1;
    public static final byte BLACK_KING = 2;
    public static final byte WHITE_MAN = 3;
    public static final byte WHITE_KING = 4;

    // Constructor for of the virtual version of the board to be used in AI calculations and decision making about the next move
    public VirtualBoard(Board realBoard) {
        this.grid = new byte[10][10];
        Pierce[][] pierces = realBoard.getMyPierces();
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                Pierce p = pierces[r][c];
                if (p != null) {
                    boolean isKing = p.isKing();
                    if (p.getColor().equals(Color.BLACK)) {
                        this.grid[r][c] = isKing ? BLACK_KING : BLACK_MAN;
                    } else {
                        this.grid[r][c] = isKing ? WHITE_KING : WHITE_MAN;
                    }
                } else {
                    this.grid[r][c] = EMPTY;
                }
            }
        }
    }

    public VirtualBoard(byte[][] grid) {
        this.grid = new byte[10][10];
        for(int i=0; i<10; i++) {
            System.arraycopy(grid[i], 0, this.grid[i], 0, 10);
        }
    }

    public VirtualBoard copy() {
        return new VirtualBoard(this.grid);
    }
    
    // Internal move representation
    public static class VMove {
        public int fromR, fromC;
        public int toR, toC;
        public boolean isCapture;
        public List<int[]> capturedPos; // Used for undoing complex turns
        public byte[] capturedTypes;
        public boolean wasPromotion;

        public VMove(int fr, int fc, int tr, int tc, boolean capt) {
            this.fromR = fr; this.fromC = fc;
            this.toR = tr; this.toC = tc;
            this.isCapture = capt;
            this.capturedPos = new ArrayList<>();
        }
    }

    // Get all legal moves for the given player color
    public List<VMove> getLegalMoves(byte playerColorType) { // BLACK_MAN or WHITE_MAN base
        boolean isWhite = (playerColorType == WHITE_MAN || playerColorType == WHITE_KING);
        List<VMove> moves = new ArrayList<>();
        List<VMove> captures = new ArrayList<>();

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                byte piece = grid[r][c];
                if (piece == EMPTY) continue;
                
                boolean pieceIsWhite = (piece == WHITE_MAN || piece == WHITE_KING);
                if (pieceIsWhite != isWhite) continue;

                addMovesForPiece(r, c, piece, moves, captures);
            }
        }

        if (!captures.isEmpty()) return captures;
        return moves;
    }

    // Helper to add moves for a specific piece
    private void addMovesForPiece(int r, int c, byte piece, List<VMove> moves, List<VMove> captures) {
        boolean isKing = (piece == BLACK_KING || piece == WHITE_KING);
        boolean isWhite = (piece == WHITE_MAN || piece == WHITE_KING);
        int forward = isWhite ? -1 : 1;

        // Simple moves
        int[] rows = isKing ? new int[]{r + forward, r - forward} : new int[]{r + forward};
        for (int nr : rows) {
            if (nr < 0 || nr >= 10) continue;
            // Check left and right
            checkSimple(r, c, nr, c - 1, moves);
            checkSimple(r, c, nr, c + 1, moves);
        }

        // Capture moves
        int[] dr = {-2, -2, 2, 2};
        int[] dc = {-2, 2, -2, 2};
        
        for (int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            
            // Direction check
            if (!isKing) {
               // Must move forward
               if (isWhite && dr[i] > 0) continue;
               if (!isWhite && dr[i] < 0) continue;
            }

            if (nr >= 0 && nr < 10 && nc >= 0 && nc < 10) {
                 if (grid[nr][nc] == EMPTY) {
                     int midR = (r + nr) / 2;
                     int midC = (c + nc) / 2;
                     byte midP = grid[midR][midC];
                     if (midP != EMPTY) {
                         boolean midIsWhite = (midP == WHITE_MAN || midP == WHITE_KING);
                         if (midIsWhite != isWhite) {
                             // Valid single capture
                             // Generate full chain if possible
                             VMove baseMove = new VMove(r, c, nr, nc, true);
                             baseMove.capturedPos.add(new int[]{midR, midC});
                             // Note: In minimax, we usually just evaluate the single step or 
                             // recursively Find longest chain.
                             // For simplicity: We will just return the immediate capture step available.
                             // The search will treat each jump as a ply if we wanted, OR we settle the chain.
                             // Implementing full chain settlement is safest for eval.
                             
                             // Let's settle the chain immediately in `applyMove`? 
                             // No, Minimax needs to know the final state.
                             // So the move generator should probably return the "Final Destination" of the turn?
                             // Multi-jump logic is complex. 
                             // Let's stick to single steps for VMove, but `applyMove` handles just that step.
                             // BUT, rule is: if you can capture, you MUST. And if you capture, turn continues.
                             // So a "Turn" consists of multiple "Moves".
                             // Standard Minimax works on Turns.
                             // So getLegalMoves should return full turns.
                             
                             // REVISION: We will Expand the single capture. 
                             // If `applyMove` results in state where `canCaptureAgain`, we don't switch turns?
                             // We'll handle that in Minimax recursion? 
                             // If state allows capture for SAME player, player must move again.
                             
                            captures.add(baseMove);
                         }
                     }
                 }
            }
        }
    }

    // Helper to check simple move validity
    private void checkSimple(int r, int c, int nr, int nc, List<VMove> moves) {
        if (nc >= 0 && nc < 10 && grid[nr][nc] == EMPTY) {
            moves.add(new VMove(r, c, nr, nc, false));
        }
    }
    
    // Applies move and returns if turn is complete (true) or needs more captures (false)
    public boolean applyMove(VMove m) {
        byte p = grid[m.fromR][m.fromC];
        grid[m.fromR][m.fromC] = EMPTY;
        grid[m.toR][m.toC] = p;
        
        // Handle promotion
        m.wasPromotion = false;
        if (p == WHITE_MAN && m.toR == 0) {
            grid[m.toR][m.toC] = WHITE_KING;
            m.wasPromotion = true;
        } else if (p == BLACK_MAN && m.toR == 9) {
            grid[m.toR][m.toC] = BLACK_KING;
            m.wasPromotion = true;
        }

        if (m.isCapture) {
            // Remove captured
            // We only support single jump in VMove structure above for now?
            // Actually, let's just handle the single jump. 
            // The capturedPos list has the victim.
            if (!m.capturedPos.isEmpty()) {
                m.capturedTypes = new byte[m.capturedPos.size()];
                for(int i=0; i<m.capturedPos.size(); i++) {
                    int[] pos = m.capturedPos.get(i);
                    m.capturedTypes[i] = grid[pos[0]][pos[1]];
                    grid[pos[0]][pos[1]] = EMPTY;
                }
            }
            // Check multi-jump
            // If just promoted, turn ends? (Standard rules: yes/no varies. We'll assume yes for simplicity or no)
            // Let's assume standard: yes, promotion ends turn.
            if (m.wasPromotion) return true; 
            
            return !canCaptureFrom(m.toR, m.toC, p);
        }
        
        return true; // Simple move always ends turn
    }

    public void undoMove(VMove m) {
        byte p = grid[m.toR][m.toC];
        if (m.wasPromotion) {
            // Demote back
            if (p == WHITE_KING) p = WHITE_MAN;
            if (p == BLACK_KING) p = BLACK_MAN;
        }
        grid[m.toR][m.toC] = EMPTY;
        grid[m.fromR][m.fromC] = p;

        // Restore captured
        if (m.isCapture && m.capturedTypes != null) {
            for(int i=0; i<m.capturedPos.size(); i++) {
                int[] pos = m.capturedPos.get(i);
                grid[pos[0]][pos[1]] = m.capturedTypes[i];
            }
        }
    }

    private boolean canCaptureFrom(int r, int c, byte p) {
        boolean isKing = (p == BLACK_KING || p == WHITE_KING);
        boolean isWhite = (p == WHITE_MAN || p == WHITE_KING);
        
        int[] dr = {-2, -2, 2, 2};
        int[] dc = {-2, 2, -2, 2};

         for (int i = 0; i < 4; i++) {
            if (!isKing) {
               if (isWhite && dr[i] > 0) continue;
               if (!isWhite && dr[i] < 0) continue;
            }
            int nr = r + dr[i];
            int nc = c + dc[i];
            if (nr >= 0 && nr < 10 && nc >= 0 && nc < 10 && grid[nr][nc] == EMPTY) {
                 int midR = (r + nr) / 2;
                 int midC = (c + nc) / 2;
                 byte midP = grid[midR][midC];
                 if (midP != EMPTY) {
                      boolean midIsWhite = (midP == WHITE_MAN || midP == WHITE_KING);
                      if (midIsWhite != isWhite) return true;
                 }
            }
         }
         return false;
    }

    // Evaluation helpers
    public int getPieceCount(boolean white) {
        int count = 0;
        for(int r=0; r<10; r++)
            for(int c=0; c<10; c++)
                if(grid[r][c] != EMPTY) {
                    boolean w = (grid[r][c] == WHITE_MAN || grid[r][c] == WHITE_KING);
                    if(w == white) count++;
                }
        return count;
    }
    
    public int getScore(boolean whitePerspective) {
        int score = 0;
        for(int r=0; r<10; r++) {
            for(int c=0; c<10; c++) {
                byte p = grid[r][c];
                if (p == EMPTY) continue;
                
                int val = 0;
                if (p == WHITE_MAN || p == BLACK_MAN) val = 10;
                if (p == WHITE_KING || p == BLACK_KING) val = 30; // King weighted higher

                boolean isWhite = (p == WHITE_MAN || p == WHITE_KING);
                if (isWhite == whitePerspective) score += val;
                else score -= val;
            }
        }
        return score;
    }
}
