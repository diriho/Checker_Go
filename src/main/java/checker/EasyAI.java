package checker;

import javafx.scene.paint.Color;
import java.util.List;
import java.util.Random;

public class EasyAI implements MoveStrategy {
    private Random random = new Random();

    @Override
    public Move chooseMove(Board board, Color activeColor) {
        // Use VirtualBoard to find legal moves
        VirtualBoard vb = new VirtualBoard(board);
        byte colorByte = (activeColor.equals(Color.WHITE)) ? VirtualBoard.WHITE_MAN : VirtualBoard.BLACK_MAN;
        
        List<VirtualBoard.VMove> vMoves = vb.getLegalMoves(colorByte);
        
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
