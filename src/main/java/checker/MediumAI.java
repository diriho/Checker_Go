package checker;

import javafx.scene.paint.Color;
import java.util.List;
import java.util.Random;

public class MediumAI implements MoveStrategy {
    private Random random = new Random();
    private int maxDepth = 4;

    @Override
    public Move chooseMove(Board board, Color activeColor) {
        VirtualBoard vb = new VirtualBoard(board);
        boolean isWhite = activeColor.equals(Color.WHITE);
        byte myColorByte = isWhite ? VirtualBoard.WHITE_MAN : VirtualBoard.BLACK_MAN;

        List<VirtualBoard.VMove> moves = vb.getLegalMoves(myColorByte);
        if (moves.isEmpty()) return null;

        double bestScore = Double.NEGATIVE_INFINITY;
        List<VirtualBoard.VMove> bestMoves = new java.util.ArrayList<>();

        // Minimax Root
        for (VirtualBoard.VMove move : moves) {
            boolean turnEnded = vb.applyMove(move);
            
            double score;
            if (!turnEnded) {
                // Same player continues
                score = minimax(vb, maxDepth, true, isWhite); // depth not reduced for multi-jump step? Or should we? Treating as ply.
            } else {
                // Opponent's turn
                score = minimax(vb, maxDepth - 1, false, isWhite);
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
        
        // Tie-breaking with minimal randomness
        VirtualBoard.VMove selected = bestMoves.get(random.nextInt(bestMoves.size()));
        return convertToRealMove(board, selected);
    }

    private double minimax(VirtualBoard vb, int depth, boolean isMaximizing, boolean iAmWhite) {
        if (depth == 0) {
            return evaluate(vb, iAmWhite);
        }

        byte activeColor = (isMaximizing == iAmWhite) ? 
                           (iAmWhite ? VirtualBoard.WHITE_MAN : VirtualBoard.BLACK_MAN) : 
                           (iAmWhite ? VirtualBoard.BLACK_MAN : VirtualBoard.WHITE_MAN);

        List<VirtualBoard.VMove> moves = vb.getLegalMoves(activeColor);
        if (moves.isEmpty()) {
            return isMaximizing ? -1000 : 1000; // No moves = loss for active player
        }

        double bestVal = isMaximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        for (VirtualBoard.VMove m : moves) {
            boolean turnEnded = vb.applyMove(m);
            double value;
            
            // If turn didn't end, the same player moves again (logic is slightly simplified here, 
            // assuming we must take the best path of the multi-jump)
            // But normally maxDepth reduces on every PLY.
            
            boolean nextMaximizing = turnEnded ? !isMaximizing : isMaximizing;
            value = minimax(vb, depth - 1, nextMaximizing, iAmWhite);

            vb.undoMove(m);

            if (isMaximizing) {
                bestVal = Math.max(bestVal, value);
            } else {
                bestVal = Math.min(bestVal, value);
            }
        }
        return bestVal;
    }

    private double evaluate(VirtualBoard vb, boolean whitePerspective) {
        // Basic Eval: Piece Count + King Count (Kings > Regular)
        // Values: Man=10, King=30
        return vb.getScore(whitePerspective);
    }
    
    // Duplication of helper - in a real project this would be in a Utils class
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
