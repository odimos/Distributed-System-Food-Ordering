package data;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Answer implements Serializable {
    private static final long serialVersionUID = 6L;

    public int type;
    public int ID;
    public Boolean imediateAnswer;
    public String message;
    public int clientTaskID;
    // Argument key, argument value
    public Map<String, Serializable> arguments;

    public Answer(Task task, String message){
        this.clientTaskID = task.clientTaskID;
        this.type = task.type;
        this.ID = task.ID;
        this.imediateAnswer = task.imediateAnswer;
        this.message = message;
        this.arguments = new HashMap<>();
    }

    public Answer( String message){

        this.message = message;
        this.arguments = new HashMap<>();
    }
 
    @Override
    public String toString() {
        return "Answer for Task: " + type + ", ID: "+ID+"\n"+
        "message: "+message+"\n"+
                "arguments: "+arguments +"\n";
    }
}
