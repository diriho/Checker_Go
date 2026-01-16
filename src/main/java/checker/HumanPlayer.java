package checker;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HumanPlayer implements PlayerInterface {
    // instance variables and methods for HumanPlayer
    protected Board board;
    protected Color myColor;
    protected Pierce selectedPiece;
    protected Set<Pierce> myPierces;
    protected int pieceCount; 
    protected Rectangle highlightRect;
    protected boolean isTurn;
    protected Runnable onTurnEnd;
    protected boolean mustCapture;
    protected Pierce forcedPiece;

    // Constructor for HumanPlayer class
    public HumanPlayer(Board board, Color color) {
        this.board = board;
        this.myColor = color;
        this.myPierces = this.getPiecesByColor(color);
        this.pieceCount = 20;
        this.isTurn = false;
        this.mustCapture = false;
        this.forcedPiece = null;
        this.setupHighlightRect();
        this.setUpMouseHandler();
    }

    public void setTurn(boolean isTurn) {
        this.isTurn = isTurn;
        this.forcedPiece = null; // Reset forced piece on new turn
        
        if (isTurn) {
            this.mustCapture = checkGlobalCaptures();
            if (this.mustCapture) {
                // System.out.println("Mandatory capture active for " + this.myColor);
            }
        }
    }

    public void setOnTurnEnd(Runnable onTurnEnd) {
        this.onTurnEnd = onTurnEnd;
    }

    private void setupHighlightRect() {
        this.highlightRect = new Rectangle(50, 50);
        this.highlightRect.setFill(Color.TRANSPARENT);
        this.highlightRect.setStroke(Color.YELLOW);
        this.highlightRect.setStrokeWidth(3);
    }

    protected void setUpMouseHandler() {
        this.board.getPane().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            double x = event.getX();
            double y = event.getY();
            
            // Convert to board coordinates
            // Constants from Board.java: offset=20, squareSize=50
            int col = (int) (x - 20) / 50;
            int row = (int) (y - 20) / 50;
            
            if (col >= 0 && col < 10 && row >= 0 && row < 10) {
               this.processClick(row, col);
            }
        });
    }

    private void processClick(int row, int col) {
        if (!this.isTurn) {
            return;
        }

        Pierce p = this.board.getMyPierces()[row][col];
        
        // Multi-capture rule: If we are in the middle of a chain, lock onto forcedPiece
        if (this.forcedPiece != null) {
            // Can only interact if clicking the forced piece (to re-select/verify)
            // or clicking a valid move target (handled by p==null branch)
            if (p != null && p != this.forcedPiece) {
                return; // Ignore clicks on other pieces
            }
        }

        // Selection Logic
        if (p != null && this.myPierces.contains(p)) {
            // Mandatory Capture Rule: If capture exists, must select a piece that CAN capture
            if (this.mustCapture) {
                int[] coords = p.getPierceCoords(); 
                int pr = (int) Math.round((coords[1] - 45) / 50.0);
                int pc = (int) Math.round((coords[0] - 45) / 50.0);
                if (!canCaptureAgain(pr, pc)) {
                     // This piece cannot capture, but someone else can. Ignore selection.
                     return;
                }
            }
            
            this.selectedPiece = p;
            this.highlightSquare(row, col);
        }
        // Move Logic
        else if (p == null && this.selectedPiece != null) {
            this.tryMoveTo(row, col);
        }
    }

    private void highlightSquare(int row, int col) {
        // Calculate position: offset(20) + col * 50
        double x = 20 + col * 50;
        double y = 20 + row * 50;
        
        this.highlightRect.setX(x);
        this.highlightRect.setY(y);
        
        this.board.getPane().getChildren().remove(this.highlightRect);
        this.board.getPane().getChildren().add(this.highlightRect);
    }

    private void tryMoveTo(int targetRow, int targetCol) {
        // Get current position of selected piece
        int[] coords = this.selectedPiece.getPierceCoords(); // pixel coords [x, y]
        int oldCol = (int) Math.round((coords[0] - 45) / 50.0);
        int oldRow = (int) Math.round((coords[1] - 45) / 50.0);
        
        int rowDiff = targetRow - oldRow;
        int colDiff = Math.abs(targetCol - oldCol);
        
        if (this.selectedPiece == null) return; // Guard clause

        // 1. Simple Move (1 step)
        if (colDiff == 1) {
             if (this.mustCapture) return; // Mandatory capture rule: Block simple moves

             boolean validRow = false;
            // Black moves down (row increase), White moves up (row decrease)
            if (this.myColor.equals(Color.BLACK)) {
                validRow = (rowDiff == 1);
            } else if (this.myColor.equals(Color.WHITE)) {
                validRow = (rowDiff == -1);
            }

            if (validRow) {
                 executeSimpleMove(oldRow, oldCol, targetRow, targetCol);
            }
        } 
        // 2. Capture Move (2 steps)
        else if (colDiff == 2 && Math.abs(rowDiff) == 2) {
             boolean validDirection = true; // Allow capturing in any direction (forward or backward)
             
             if (validDirection) {
                 // Calculate jumped square
                 int jumpedRow = (oldRow + targetRow) / 2;
                 int jumpedCol = (oldCol + targetCol) / 2;
                 
                 Pierce jumpedPiece = this.board.getMyPierces()[jumpedRow][jumpedCol];
                 
                 // Check if there is an opponent piece to capture
                 if (jumpedPiece != null && !this.myPierces.contains(jumpedPiece)) {
                     executeCaptureMove(oldRow, oldCol, targetRow, targetCol, jumpedRow, jumpedCol);
                 }
             }
        }
    }
    
    protected void executeSimpleMove(int oldRow, int oldCol, int targetRow, int targetCol) {
        this.updateBoardAndPiece(oldRow, oldCol, targetRow, targetCol);
        this.finalizeTurn();
    }

    protected void executeCaptureMove(int oldRow, int oldCol, int targetRow, int targetCol, int jumpedRow, int jumpedCol) {
        // Remove captured piece
        Pierce capturedPiece = this.board.getMyPierces()[jumpedRow][jumpedCol];
        this.board.getMyPierces()[jumpedRow][jumpedCol] = null;
        if (capturedPiece != null) {
            this.board.getPane().getChildren().remove(capturedPiece.getNode());
        }
        
        this.updateBoardAndPiece(oldRow, oldCol, targetRow, targetCol);
        
        // Check for double jump recursively
        if (canCaptureAgain(targetRow, targetCol)) {
             this.selectedPiece = this.board.getMyPierces()[targetRow][targetCol];
             this.forcedPiece = this.selectedPiece; // Lock piece
             this.mustCapture = true; // Reinforce capture state
             this.highlightSquare(targetRow, targetCol);
        } else {
            this.forcedPiece = null;
            this.finalizeTurn();
        }
    }
    
    protected void updateBoardAndPiece(int oldRow, int oldCol, int targetRow, int targetCol) {
        this.board.getMyPierces()[oldRow][oldCol] = null;
        this.board.getMyPierces()[targetRow][targetCol] = this.selectedPiece;
         
        int newPixelX = targetCol * 50 + 45;
        int newPixelY = targetRow * 50 + 45;
        this.movePiece(this.selectedPiece, newPixelX, newPixelY);
        
        // Check for promotion
        if (!this.selectedPiece.isKing()) {
            boolean promoted = false;
            if (this.myColor.equals(Color.WHITE) && targetRow == 0) {
                promoted = true;
            } else if (this.myColor.equals(Color.BLACK) && targetRow == 9) {
                promoted = true;
            }
            
            if (promoted) {
                this.selectedPiece.makeKing();
            }
        }
    }
    
    protected void finalizeTurn() {
        this.selectedPiece = null;
        this.board.getPane().getChildren().remove(this.highlightRect);
        if (this.onTurnEnd != null) {
            this.onTurnEnd.run();
        }
    }
    
    private boolean checkGlobalCaptures() {
        // Use removeIf to clean up any "ghost" pieces that might have been captured
        // but not removed from our local set yet (sync with Board state).
        this.myPierces.removeIf(p -> {
            int[] coords = p.getPierceCoords();
            int r = (int) Math.round((coords[1] - 45) / 50.0);
            int c = (int) Math.round((coords[0] - 45) / 50.0);
            if (r < 0 || r >= 10 || c < 0 || c >= 10) return true; // Out of bounds
            return this.board.getMyPierces()[r][c] != p; // Not on board anymore
        });

        for (Pierce p : this.myPierces) {
            int[] coords = p.getPierceCoords();
            // Pixel to Grid
            int r = (int) Math.round((coords[1] - 45) / 50.0);
            int c = (int) Math.round((coords[0] - 45) / 50.0);
            
            if (canCaptureAgain(r, c)) {
                return true;
            }
        }
        return false;
    }

    private boolean canCaptureAgain(int r, int c) {
        Pierce p = this.board.getMyPierces()[r][c];
        if (p == null) {
            return false;
        }
        boolean isKing = p.isKing();
        int direction = (this.myColor.equals(Color.WHITE)) ? -1 : 1;

        // Check all 4 diagonal directions for a valid capture
        int[] dr = {-2, -2, 2, 2};
        int[] dc = {-2, 2, -2, 2};
        
        for (int i = 0; i < 4; i++) {
            // Check all directions for capture, regardless of king status
            // Filter invalid directions for regular pieces -> now REMOVED to allow backward capture
            
            int nr = r + dr[i];
            int nc = c + dc[i];
            
            // Check bounds
            if (nr >= 0 && nr < 10 && nc >= 0 && nc < 10) {
                 // Check if target is empty
                 if (this.board.getMyPierces()[nr][nc] == null) {
                     // Check midpoint for opponent
                     int mr = (r + nr) / 2;
                     int mc = (c + nc) / 2;
                     Pierce mid = this.board.getMyPierces()[mr][mc];
                     if (mid != null && !this.myPierces.contains(mid)) {
                         return true;
                     }
                 }
            }
        }
        return false;
    }

    // helper method to get all the pieces of the a player
    private Set<Pierce> getPiecesByColor(Color color) {
        Set<Pierce> playerPieces = new HashSet<>();
        // Assuming a standard 10x10 board grid
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Pierce p = this.board.getMyPierces()[row][col];
                if (p != null && p.getColor().equals(color)) {
                    playerPieces.add(p);
              }
            }   
        }
        return playerPieces;
    }

    // getter method to return the number of pieces a player has
    public int getPieceCount() {
        return this.pieceCount; 
    }

    // getter method to return the set of pieces a player has
    public Set<Pierce> getPierces() {
        return this.myPierces;
    }   

    // set the counts of pieces a player has
    public void setPieceCount(int count) {
        this.pieceCount = count;    
    }

    public void movePiece(Pierce piece, int newX, int newY) {
        // Logic to move the piece to new coordinates
        piece.setCoords(newX, newY);

    }


}