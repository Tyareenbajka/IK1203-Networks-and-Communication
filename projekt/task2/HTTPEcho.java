import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class HTTPEcho {
    static int BUFFERSIZE = 1024;
    public static void main( String[] args) throws IOException {
        byte[] fromClientBuffer = new byte[BUFFERSIZE];
        byte[] toClientBuffer = new byte[BUFFERSIZE];
        ServerSocket welcomeSocket = new ServerSocket(Integer.parseInt(args[0]));

        while(true){
            Socket connectionSocket = welcomeSocket.accept();
            InputStream inFromClient = connectionSocket.getInputStream();
            OutputStream outToClient = connectionSocket.getOutputStream();
            StringBuilder outToClientData = new StringBuilder();
            String decodeString = "";

            String msg = ("HTTP/1.1 200 OK \r\n\r\n");
            outToClientData.append(msg);
            int charChecker = inFromClient.read(fromClientBuffer);
            while(charChecker != -1){
                decodeString = new String(fromClientBuffer, 0, charChecker);
                if(decodeString.contains("\n")){
                    outToClientData.append(decodeString);
                    break;
                }
                charChecker = inFromClient.read(fromClientBuffer);
                outToClientData.append(decodeString);
            }
            byte [] sendDataToClient = outToClientData.toString().getBytes();
            outToClient.write(sendDataToClient);
            connectionSocket.close();
        }
    }
}

