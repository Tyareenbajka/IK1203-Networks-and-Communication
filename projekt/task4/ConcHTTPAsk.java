import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConcHTTPAsk {
    public static void main(String[] args) throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(Integer.parseInt(args[0]));
        while(true) {
            Socket connectionSocket = welcomeSocket.accept();
            MyRunnable concClient = new MyRunnable(connectionSocket);
            Thread conClientThread = new Thread(concClient);
            conClientThread.start();
        }
    }
}
