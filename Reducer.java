import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import data.Answer;
import data.Task;

public class Reducer implements ResponseHandler, RequestHandler {

    Map<Integer, Operation> operations = new HashMap<>();

    private class Operation { // could have a generic type here 
        List<Object> partial_results;
        int clientTaskID;
        int id; // The same as task id
        int type;
        int size;
        public Operation( int id, int type, int clientTaskID) {;
            this.id = id;
            this.type = type;
            this.size = 0;
            this.clientTaskID = clientTaskID;
            this.partial_results = new ArrayList<>();
        }

        public void addResult(List<Object> partial_result) {
            this.partial_results.addAll(partial_result);
            this.size++;
            if (this.size == GlobalConfig.WORKERS_NUMBER) { 
                 List<Object> final_result = getFinalResult();
                Task task = new Task(this.clientTaskID, type, false, 
                    Map.of("result", (Serializable) final_result),
                    id
                );
                System.out.println("Reducer: Sending to Master: "+GlobalConfig.getCommandName(type)+" result, with id:"+id+", for client task id "+clientTaskID);
                sendToMaster(task);
            }
        }

        public List<Object> getFinalResult() {
            
            if (
                this.type == GlobalConfig.GET_SALES_PER_FOOD_CATEGORY || this.type == GlobalConfig.GET_SALES_PER_PRODUCT_CATEGORY
            ){
                // Calculate the sum
                int total = partial_results.stream()
                                .mapToInt( p -> ( (Pair<String, Integer>) p).second)
                                .sum();

                partial_results.add(new Pair<>("total", total));
                return partial_results;
            } 
            else if (this.type == GlobalConfig.GET_SALES_PER_PRODUCT) {
                // Step 1: Merge into Map<String, Integer>
                Map<String, Integer> mergedMap = ( partial_results).stream()
                    .collect(Collectors.toMap(
                        p -> ( (Pair<String, Integer>) p).first,
                        p -> ( (Pair<String, Integer>) p).second,
                        Integer::sum // merge function for duplicates
                    ));

                // Step 2: Convert Map back to List<Pair<String, Integer>>
                List<Object> mergedList = mergedMap.entrySet().stream()
                    .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
                return mergedList;
            } 

            else {
                return partial_results;
            }
        }


    }

    @Override
    public Answer handleRequestFromClient(Task req) {
        System.out.println("Reducer: "+GlobalConfig.getCommandName(req.type)+" request, with id:"+req.ID);
        int id = req.ID;
        synchronized (operations) {
            if (!operations.containsKey(id)) {
				operations.put(id, new Operation(id, req.type, req.clientTaskID ));
			} 
            List<Object> partial_result = (List<Object>) req.arguments.get("result");
            operations.get(id).addResult(partial_result);
        }

        return null;
    }

    @Override
    public void handleResponseFromServer(Answer res) {
        System.out.println("Response From Master: "+res);
        return;
    }

    public void sendToMaster(Task task)  {
        (new Client<Reducer>(task, GlobalConfig.MASTER_HOST_IP, GlobalConfig.MASTER_PORT_FOR_REDUCER_AS_CLIENT, this))
        .start();
    }

    public void createServer() {
         new Server<Reducer>()
        .openServer(this, GlobalConfig.REDUCER_PORT_WORKER_AS_CLIENT);
    }

    public static void main(String[] args) {
        Reducer reducer = new Reducer();
        new Thread(
            ()->{
                reducer.createServer();
            }
        ).start();

        //reducer.sendToMaster(new Task(0, GlobalConfig.ADD_STORE, true, Map.of("storeName", "json") ));

    }
    
}
