package checker;

import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;

// EasyAI class implementing MoveStrategy for easy difficulty level
public class EasyAI implements MoveStrategy {
    private Random random = new Random();

    @Override
    public Move chooseMove(Board board, Color activeColor) {
        return chooseMove(board, activeColor, null);
    }

    @Override
    public Move chooseMove(Board board, Color activeColor, Pierce forcedPiece) {
        // Use VirtualBoard to find legal moves
        VirtualBoard vb = new VirtualBoard(board);
        byte colorByte = (activeColor.equals(Color.WHITE)) ? VirtualBoard.WHITE_MAN : VirtualBoard.BLACK_MAN;
        
        List<VirtualBoard.VMove> vMoves = vb.getLegalMoves(colorByte);
        
        // Filter for forced piece
        if (forcedPiece != null) {
            int fr = -1, fc = -1;
            Pierce[][] pierces = board.getMyPierces();
            for(int r=0; r<10; r++){
                for(int c=0; c<10; c++){
                    if(pierces[r][c] == forcedPiece) {
                        fr = r; fc = c; break;
                    }
                }
            }
            if (fr != -1) {
                final int r = fr, c = fc;
                vMoves.removeIf(m -> m.fromR != r || m.fromC != c);
            } else {
                vMoves.clear(); 
            }
        }
        
        if (vMoves.isEmpty()) return null;

        // Random choice
        VirtualBoard.VMove selected = vMoves.get(random.nextInt(vMoves.size()));
        
        // Convert VMove to real Move
        return convertToRealMove(board, selected);
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
