import data.Answer;

public class Pending {
    public Answer answer;
    public boolean isSet = false;

    public synchronized Answer waitForAnswer() {
        while (!isSet) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
        return answer;
    }

    public synchronized void setAnswer(Answer answer) {
        if (!isSet) {
            this.answer = answer;
            this.isSet = true;
            notify();  // Wake up waiting thread
        }
    }
    
    

}