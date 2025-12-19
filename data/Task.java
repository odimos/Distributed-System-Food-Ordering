package data;
import java.io.Serializable;
import java.util.Map;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    public int type;
    public int ID; // this is given from master
    public int clientTaskID; // this is given from client
    public Boolean imediateAnswer;
    // Argument key, argument value
    public Map<String, Serializable> arguments;

    // Constructor
     public Task(int clientTaskID, int type, Boolean imediateAnswer, Map<String, Serializable> arguments) {
        this.type = type;
        this.arguments = arguments;
        this.imediateAnswer = imediateAnswer;
        this.clientTaskID = clientTaskID;
        this.ID = 0;
    }

    public Task(int clientTaskID, int type, Boolean imediateAnswer, Map<String, Serializable> arguments, int ID) {
        this.type = type;
        this.arguments = arguments;
        this.imediateAnswer = imediateAnswer;
        this.clientTaskID = clientTaskID;
        this.ID = ID;
    }
 
    @Override
    public String toString() {
        return "\nTask: " + type + ", ID: "+ID+"\n"+
                "arguments: "+arguments +"\n";
    }
    
}
