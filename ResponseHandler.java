import data.Answer;

public interface ResponseHandler {
    void handleResponseFromServer(Answer res);
}