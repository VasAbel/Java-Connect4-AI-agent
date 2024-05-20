import java.util.ArrayList;

public class StudentPlayer extends Player{

    private int searchDepth = 6;
    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
    }

    @Override
    public int step(Board board) {
        return minimax(board);
    }

    public int minimax(Board board){
        return max(board, searchDepth);

    }

    public int max(Board board, int depth){
        ArrayList<Board> possStates = PossibleStates(board, playerIndex);     //lehetséges lépés utáni állások
        if(board.gameEnded() || depth == 0) return evaluate(board);      //ha nincs köv. lépés (levélhez értünk) vagy elértük a mélységkorlátot, elkezd visszatérni a rekurzió a hasznosságfgv-nyel
        int col = 0;
        int v = -1000000000;
        for(Board ps : possStates){
            int vBefore = v;
            v = Math.max(v, min(ps, depth-1));                         //minden lehetséges követőállapotból lemegyünk, ami végül visszaadja a maximális értékű állapotot, amit elérhetünk
            if(depth==searchDepth && v > vBefore)                                      //ha a top szinten vagyunk és most egy nagyobb értéket találtunk, mint előbb, megjegyezzük az oszlopot
                col = ps.getLastPlayerColumn();
        }
        if(depth==searchDepth) return col;                                            //ha a top szinten vagyunk, érték helyett oszlopszámot adunk vissza
        return v;
    }

    public int min(Board board, int depth){
        ArrayList<Board> possStates = PossibleStates(board, 1);
        if(board.gameEnded() || depth == 0) return evaluate(board);
        int v = 1000000000;
        for(Board ps : possStates){
            v = Math.min(v, max(ps, depth-1));
        }
        return v;
    }

    public ArrayList<Board> PossibleStates(Board board, int playerIdx){
        ArrayList<Board> temp = new ArrayList<>();
        ArrayList<Integer> possibleSteps = board.getValidSteps();           //lehetséges lépések oszlopai
        for(int ps : possibleSteps){
            Board tmpBoard = new Board(board);                              //lemásoljuk a táblát, és ezen lépünk egyet a megfelelő karakterrel, ill. ezt adjuk hozzá a listánkhoz. Megmarad az eredeti board, így azt minden ciklusban újra le tudjuk másolni
            tmpBoard.step(playerIdx, ps);
            temp.add(tmpBoard);
        }
        return temp;
    }


    public int evaluate(Board board){
        int score = 0;
        int[][] state = board.getState();

        for(int row = 0; row < boardSize[0]; row++){
            for(int col = 0; col < boardSize[1] - 3; col++){
                score+= evaluateArea(state[row][col], state[row][col+1], state[row][col+2], state[row][col+3]);
            }
        }

        for(int row = 0; row < boardSize[0] - 3; row++){
            for(int col = 0; col < boardSize[1]; col++){
                score+= evaluateArea(state[row][col], state[row+1][col], state[row+2][col], state[row+3][col]);
            }
        }

        for(int row = 0; row < boardSize[0] - 3; row++){
            for(int col = 0; col < boardSize[1] - 3; col++){
                score+= evaluateArea(state[row][col], state[row+1][col+1], state[row+2][col+2], state[row+3][col+3]);
            }
        }

        for(int row = 3; row < boardSize[0]; row++){
            for(int col = 0; col < boardSize[1] - 3; col++){
                score+= evaluateArea(state[row][col], state[row-1][col+1], state[row-2][col+2], state[row-3][col+3]);
            }
        }

        return score;
    }

    public int evaluateArea(int a, int b, int c, int d){
        int score = 0;
        int allyScore = 0;
        int opponentScore = 0;

        if(a==2) allyScore++;
        if(b==2) allyScore++;
        if(c==2) allyScore++;
        if(d==2) allyScore++;

        if(a==1) opponentScore++;
        if(b==1) opponentScore++;
        if(c==1) opponentScore++;
        if(d==1) opponentScore++;

        if (allyScore == 4) {
            score += 1000; // AI wins
        } else if (allyScore == 3 && opponentScore == 0) {
            score += 100; // AI has three in a row
        } else if (allyScore == 2 && opponentScore == 0) {
            score += 10; // AI has two in a row
        }

        if(opponentScore == 4)
            score -= 1000;
        else if (opponentScore == 3 && allyScore == 0) {
            score -= 100; // Opponent has three in a row
        } else if (opponentScore == 2 && allyScore == 0) {
            score -= 10; // Opponent has two in a row
        }

        return score;
    }



}
