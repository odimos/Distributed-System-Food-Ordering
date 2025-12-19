import data.Answer;
import data.Task;

public interface RequestHandler {
    Answer handleRequestFromClient(Task req);
}
