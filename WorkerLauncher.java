import java.io.IOException;

public class WorkerLauncher {
    public static void main(String[] args) {
        for (int i = 0; i < GlobalConfig.WORKERS_NUMBER; i++) {
            ProcessBuilder pb = new ProcessBuilder(
                "java", "WorkerNode", String.valueOf(i)
            );
            pb.inheritIO(); // to output on the same cmd
            try {
                pb.start(); 
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}