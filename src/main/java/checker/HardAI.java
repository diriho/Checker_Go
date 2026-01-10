package checker;

import javafx.scene.paint.Color;
import java.util.List;
import java.util.Random;

public class HardAI implements MoveStrategy {
    private Random random = new Random();
    private int maxDepth = 6;

    @Override
    public Move chooseMove(Board board, Color activeColor) {
        VirtualBoard vb = new VirtualBoard(board);
        boolean isWhite = activeColor.equals(Color.WHITE);
        byte myColorByte = isWhite ? VirtualBoard.WHITE_MAN : VirtualBoard.BLACK_MAN;

        List<VirtualBoard.VMove> moves = vb.getLegalMoves(myColorByte);
        if (moves.isEmpty()) return null;

        double bestScore = Double.NEGATIVE_INFINITY;
        List<VirtualBoard.VMove> bestMoves = new java.util.ArrayList<>();

        // Alpha-Beta Root
        for (VirtualBoard.VMove move : moves) {
            boolean turnEnded = vb.applyMove(move);
            
            double score;
            if (!turnEnded) {
                score = alphaBeta(vb, maxDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true, isWhite);
            } else {
                score = alphaBeta(vb, maxDepth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false, isWhite);
            }
            
            vb.undoMove(move);

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        if (bestMoves.isEmpty()) return convertToRealMove(board, moves.get(0));
        
        // Tie-breaking
        VirtualBoard.VMove selected = bestMoves.get(random.nextInt(bestMoves.size()));
        return convertToRealMove(board, selected);
    }

    private double alphaBeta(VirtualBoard vb, int depth, double alpha, double beta, boolean isMaximizing, boolean iAmWhite) {
        if (depth == 0) {
            return advancedEvaluate(vb, iAmWhite, isMaximizing);
        }

        byte activeColor = (isMaximizing == iAmWhite) ? 
                           (iAmWhite ? VirtualBoard.WHITE_MAN : VirtualBoard.BLACK_MAN) : 
                           (iAmWhite ? VirtualBoard.BLACK_MAN : VirtualBoard.WHITE_MAN);

        List<VirtualBoard.VMove> moves = vb.getLegalMoves(activeColor);
        if (moves.isEmpty()) {
            return isMaximizing ? -10000 : 10000;
        }

        if (isMaximizing) {
            double value = Double.NEGATIVE_INFINITY;
            for (VirtualBoard.VMove m : moves) {
                boolean turnEnded = vb.applyMove(m);
                boolean nextMaximizing = turnEnded ? !isMaximizing : isMaximizing;
                
                value = Math.max(value, alphaBeta(vb, depth - 1, alpha, beta, nextMaximizing, iAmWhite));
                vb.undoMove(m);
                
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break; // Beta cutoff
            }
            return value;
        } else {
            double value = Double.POSITIVE_INFINITY;
            for (VirtualBoard.VMove m : moves) {
                boolean turnEnded = vb.applyMove(m);
                boolean nextMaximizing = turnEnded ? !isMaximizing : isMaximizing;
                
                value = Math.min(value, alphaBeta(vb, depth - 1, alpha, beta, nextMaximizing, iAmWhite));
                vb.undoMove(m);
                
                beta = Math.min(beta, value);
                if (beta <= alpha) break; // Alpha cutoff
            }
            return value;
        }
    }

    private double advancedEvaluate(VirtualBoard vb, boolean whitePerspective, boolean isMaximizing) {
        // Factors:
        // 1. Piece Count (Man=10, King=30)
        // 2. Mobility (Number of legal moves)
        // 3. Center Control (Pieces in cols 3-6)
        // 4. Promotion Potential (Pieces closer to King row)
        
        double score = 0;
        
        // Material & Position
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                byte p = vb.grid[r][c];
                if (p == VirtualBoard.EMPTY) continue;
                
                boolean isWhite = (p == VirtualBoard.WHITE_MAN || p == VirtualBoard.WHITE_KING);
                boolean isKing = (p == VirtualBoard.BLACK_KING || p == VirtualBoard.WHITE_KING);
                
                double val = isKing ? 30 : 10;
                
                // Center Control (Columns 3,4,5,6)
                if (c >= 3 && c <= 6) val += 2;
                
                // Promotion Potential (For men only)
                if (!isKing) {
                    if (isWhite) val += (9 - r) * 0.5; // Closer to row 0? No, White starts at bottom (confirm logic)
                    // Board logic: White at rows 6-9. Moves "forward" (dir -1) to row 0.
                    // Black at rows 0-3. Moves "forward" (dir 1) to row 9.
                    else val += r * 0.5;
                }
                
                if (isWhite == whitePerspective) score += val;
                else score -= val;
            }
        }
        
        // Mobility (Expensive? We invoke getLegalMoves only for active player usually)
        // We can approximate or skip if too slow. 
        // Let's add mobility for the *current* player (who just moved? No, whose turn it is).
        // Since 'advancedEvaluate' is called at leaf, 'isMaximizing' tells us whose turn it is.
        // We already generated moves for them? No, we are at depth 0.
        // Let's skip expensive full mobility check and just rely on board position.
        
        return score;
    }

    private Move convertToRealMove(Board board, VirtualBoard.VMove vm) {
        Pierce p = board.getMyPierces()[vm.fromR][vm.fromC];
        int jumpR = -1, jumpC = -1;
        if (vm.isCapture && !vm.capturedPos.isEmpty()) {
            jumpR = vm.capturedPos.get(0)[0];
            jumpC = vm.capturedPos.get(0)[1];
        }
        return new Move(p, vm.fromR, vm.fromC, vm.toR, vm.toC, vm.isCapture, jumpR, jumpC);
    }
}
