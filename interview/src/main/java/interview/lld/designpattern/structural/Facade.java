package interview.lld.designpattern.structural;
//A facade is just a coordinator class that hides complexity.
enum GameState {
    IN_PROGRESS,
    WON,
    DRAW
}
class Board {
    public boolean placeMark(int row, int col, String mark) {
        // Place mark logic
        return true;
    }

    public boolean checkWin(int row, int col) {
        // Check win logic
        return false;
    }

    public boolean isFull() {
        // Check if board is full
        return false;
    }
}

class Player {
    private String mark;

    public Player(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}

class Game {
    private Board board;
    private Player playerX;
    private Player playerO;
    private Player currentPlayer;
    private GameState state;

    public Game() {
        this.board = new Board();
        this.playerX = new Player("X");
        this.playerO = new Player("O");
        this.currentPlayer = playerX;
        this.state = GameState.IN_PROGRESS;
    }

    public boolean makeMove(int row, int col) {
        // Coordinates board, player, and state logic
        // Caller doesn't need to understand internal details
        if (state != GameState.IN_PROGRESS) return false;
        if (!board.placeMark(row, col, currentPlayer.getMark())) return false;

        if (board.checkWin(row, col)) {
            state = GameState.WON;
        } else if (board.isFull()) {
            state = GameState.DRAW;
        } else {
            currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
        }
        return true;
    }
}

class GameFacade {
    static void main() {
        // Usage - simple interface hides all the coordination
        Game game = new Game();
        game.makeMove(0, 0);
        game.makeMove(1, 1);
    }
}
