package tcpclient;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class TCPClient {

    private static int BUFFERSIZE=1024;

    public static String askServer(String hostname, int port, String ToServer) throws IOException {
        byte[] fromUserBuffer = (ToServer + "\r\n").getBytes(StandardCharsets.UTF_8);
        byte[] fromServerBuffer = new byte[BUFFERSIZE];
        Socket clientSocket = new Socket(hostname, port);
        clientSocket.setSoTimeout(5000);
        InputStream inFromServer = clientSocket.getInputStream();
        OutputStream outToServer = clientSocket.getOutputStream();
        StringBuilder dataFromServer = new StringBuilder();
        String decodeString = "";

        try{
            outToServer.write(fromUserBuffer);
            int charChecker = inFromServer.read(fromServerBuffer);
            while(charChecker != -1){
                decodeString = new String(fromServerBuffer, 0, charChecker, StandardCharsets.UTF_8);
                dataFromServer.append(decodeString + "\r\n");
                charChecker = inFromServer.read(fromServerBuffer);
            }
        } catch (SocketTimeoutException ste) {
        }
        clientSocket.close();
        return dataFromServer.toString();
    }

    public static String askServer(String hostname, int port) throws IOException {
        byte[] fromServerBuffer = new byte[BUFFERSIZE];
        Socket cliSocket = new Socket(hostname, port);
        cliSocket.setSoTimeout(5000);

        int fromServerLength = cliSocket.getInputStream().read(fromServerBuffer);
        String decodeString = new String(fromServerBuffer, 0, fromServerLength-1, StandardCharsets.UTF_8);
        cliSocket.close();
        return decodeString;
    }
}

