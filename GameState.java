public class GameState {

    public enum State {
        WAITING,
        PLAYING,
        WON,
        LOST
    }
    private State current;
    private int elapsedSeconds;
    private javax.swing.Timer timer;
    private int flagsPlaced;
    private int mineCount;

    public GameState(int mineCount) {
        this.mineCount = mineCount;
        reset();
    }

    public void reset() {
        current = State.WAITING;
        elapsedSeconds = 0;
        flagsPlaced = 0;
        if(timer != null) {
            timer.stop();
        }
    }

    public void startGame() {
        current = State.PLAYING;
        elapsedSeconds = 0;
        timer = new javax.swing.Timer(1000, e -> {
            elapsedSeconds++;
        });
        timer.start();
    }

    public void win(){
        current = State.WON;
        timer.stop();
    }

    public void lose(){
        current = State.LOST;
        timer.stop();
    }

    public void incrementFlags(){
        flagsPlaced++;
    }
    public void decrementFlags(){
        flagsPlaced--;
    }

    public State getState(){
        return current;
    }

    public int getElapsedSeconds(){
        return elapsedSeconds;
    }

    public int getRemainingMines(){
        return mineCount - flagsPlaced;
    }

    public boolean isPlaying(){
        return current == State.PLAYING;
    }

    public boolean isWaiting(){
        return current == State.WAITING;
    }
}
