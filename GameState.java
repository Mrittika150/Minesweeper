public class GameState {

    public enum State {
        WAITING,
        PLAYING,
        WON,
        LOST
    }
    private State current;
    private int elaspedSeconds;
    private javax.swing.Timer timer;
    private int flagsPlaced;
    private int mineCount;

    public GameState(int mineCount) {
        this.mineCount = mineCount;
        reset();
    }

    public void reset() {
        current = State.WAITING;
        elaspedSeconds = 0;
        flagsPlaced = 0;
        if(timer != null) {
            timer.stop();
        }
    }

    public void startGame() {
        current = State.PLAYING;
        elaspedSeconds = 0;
        timer = new javax.swing.Timer(1000, e -> {
            elaspedSeconds++;
        });
        timer.start();
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

    public int getElaspedSeconds(){
        return elaspedSeconds;
    }

    public int getRemainingSeconds(){
        return mineCount - flagsPlaced;
    }

    public boolean isPlaying(){
        return current == State.PLAYING;
    }

    public boolean isWaiting(){
        return current == State.WAITING;
    }
}
