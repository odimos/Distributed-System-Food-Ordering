import java.util.HashMap;
import java.util.Map;

import data.Answer;
import data.Task;

public class Master implements RequestHandler, ResponseHandler {

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
        .openServer(this, GlobalConfig.MASTER_PORT_FOR_CLIENTS);
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
        System.out.println("Master: Received Request from Pelati");
        
        int taskID = nextID();
        Pending pending = new Pending();
        addPending(taskID, pending);
        req.ID = taskID;
        

        if (! req.imediateAnswer){
            // send to all
            for (int i = 0; i < GlobalConfig.WORKERS_NUMBER; i++) {
                System.out.println("Will send to ALL"+req);
                sendToWorker(req, (GlobalConfig.INITIAL_PORT_FOR_WORKERS+i));
            }

        } else {
            // send to one 
            int port = selectWorkerPort((String) req.arguments.get("storeName"));
            System.out.println("Will send to "+port+ " "+req);
            sendToWorker(req, port);
        }
        // Decide where to send the request
        
        Answer answer = pending.waitForAnswer();
        
        //System.out.println("Master: Sending Response to Pelatis"+answer);
        return answer;
    }

    public void sendToWorker(Task task, int port) {
        System.out.println("SendToWorker "+port);
        (new Client<Master>(task, GlobalConfig.WORKERNODE_HOST_IP, port, this))
       .start();
    }

    public int selectWorkerPort(String storeName){
        return  (Math.abs(storeName.hashCode()) % GlobalConfig.WORKERS_NUMBER) + GlobalConfig.INITIAL_PORT_FOR_WORKERS;
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
            System.out.println("InnerMaster: Received Request from Reducer "+pending+" "+taskID);
            //printPendings(taskID);
            Answer answer = new Answer(req, "Answer from InnerMaster");
            answer.arguments.put("result", req.arguments.get("result"));
            pending.setAnswer(answer); // could remove from pendings with the key

            return null;
        }

        public void openServer() {
            new Server<InnerMaster>()
            .openServer(this, GlobalConfig.MASTER_PORT_FOR_REDUCER_AS_CLIENT);
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
        

        // create subMaster to use as client with the Pelatis
	}
    
}
