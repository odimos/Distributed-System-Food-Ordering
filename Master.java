import java.util.HashMap;
import java.util.Map;

import data.Answer;
import data.Task;

public class Master implements RequestHandler, ResponseHandler {

    
    private class WorkerInfo {
        public String host;
        public int port;
        public int id;

        public WorkerInfo(String host, int port, int id){
            this.id = id;
            this.host = host;
            this.port = port;
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return super.toString();
        }
    
        
    }

    private WorkerInfo[] workersArray = new WorkerInfo[ GlobalConfig.workers.size() ];

    private final Map<Integer, Pending> pendings = new HashMap<Integer, Pending>();
    private int taskIDcount=0;
    private InnerMaster innerMaster = new InnerMaster();
    

    public Pending getPending(int id) {
        synchronized (pendings) {
            return pendings.get(id);
        }
    }

    public void addPending(int id, Pending p) {
        synchronized (pendings) {
            pendings.put(id, p);
        }
    }

    public void createServer(){
        new Server<Master>()
        .openServer(this, GlobalConfig.MASTER_PORT_FOR_CLIENTS, "Master");
    }

    public synchronized int nextID(){
        taskIDcount++;
        return taskIDcount;
    }

    // For WorkerNode
    @Override
    public void handleResponseFromServer(Answer res) {
        //System.out.println("Master: Receives response from Worer ");

        // If immediate answer or not
        if (! res.imediateAnswer){
            // the answer we send to Pelati will come from reducer not worker
            return;
        }

        int taskID = res.ID;
        // try {
        //     Thread.sleep(2000);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }

        Pending pending = getPending(taskID);
        //printPendings(taskID);
        pending.setAnswer(res);
        return;
    }

    @Override
    public Answer handleRequestFromClient(Task req) {
        System.out.println("Master: Received Request "+ GlobalConfig.getCommandName(req.type) +" from Pelati");
    
        int taskID = nextID();
        Pending pending = new Pending();
        addPending(taskID, pending);
        req.ID = taskID;
        
        if (! req.imediateAnswer){
            // send to all
            System.out.println("Master sending "+ GlobalConfig.getCommandName(req.type)+" request, id:"+taskID+", to ALL workers");
            for (int i = 0; i < GlobalConfig.workers.size(); i++) {
                WorkerInfo worker = workersArray[i];
                sendToWorker(req, worker.host, worker.port);
            }

        } else {
            // send to one 
            int workeers_index = selectWorker((String) req.arguments.get("storeName"));
            WorkerInfo worker = workersArray[workeers_index];
            System.out.println("Master sending "+ GlobalConfig.getCommandName(req.type) + " request, id:"+taskID+", to worker "+worker.port);
            sendToWorker(req, worker.host, worker.port);
        }
        // Decide where to send the request
        
        Answer answer = pending.waitForAnswer();
        System.out.println("Master: Sending Response to Pelatis for "  +GlobalConfig.getCommandName(req.type)+" request, with id:"+taskID);
        return answer;
    }

    public void sendToWorker(Task task, String host, int port) {
        (new Client<Master>(task, host, port, this))
       .start();
    }

    public int selectWorker(String storeName){
        return  (Math.abs(storeName.hashCode()) % GlobalConfig.workers.size());
    }

    public void printPendings(int id){
        synchronized (pendings){
        System.out.println("Pengings: "+id);
        for (Map.Entry<Integer, Pending> entry : pendings.entrySet()) {
            System.out.println(id+" Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        }

    }

    public class InnerMaster implements RequestHandler {
        @Override
        public Answer handleRequestFromClient(Task req) {
            int taskID = req.ID;
            // try {
            //     Thread.sleep(2000);
            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            // }

            Pending pending = getPending(taskID);
            System.out.println("InnerMaster: Received result from Reducer "+GlobalConfig.getCommandName(req.type)+", id:"+taskID+", for client task id "+req.clientTaskID);
            //printPendings(taskID);
            Answer answer = new Answer(req, "Reducer Result");
            answer.arguments.put("result", req.arguments.get("result"));
            pending.setAnswer(answer); // could remove from pendings with the key

            return null;
        }

        public void openServer() {
            new Server<InnerMaster>()
            .openServer(this, GlobalConfig.MASTER_PORT_FOR_REDUCER_AS_CLIENT,"(InnerMaster for Reducer)");
        }


    }

    public void initialiseWorkers(){
        int count=0;
        for (Pair<String, Integer> worker : GlobalConfig.workers) {
            String host = worker.first;
            int port = worker.second;
            WorkerInfo wInfo = new WorkerInfo(host, port, count);
            workersArray[count] = wInfo;
            count++;
            // Could store wInfo in a map if needed
        }
    }

    public static void main(String args[]) {
        Master master = new Master();
        new Thread(()->{
            master.createServer();
            
        }).start();
        new Thread(()->{
            master.innerMaster.openServer();
        }).start();
        master.initialiseWorkers();

        

        // create subMaster to use as client with the Pelatis
	}
    
}
