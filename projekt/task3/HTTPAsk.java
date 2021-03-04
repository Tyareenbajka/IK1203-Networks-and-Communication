import tcpclient.TCPClient;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class HTTPAsk {
    static int BUFFERSIZE = 1024;
    public static void main( String[] args) throws IOException {
        byte[] fromClientBuffer = new byte[BUFFERSIZE];
        byte[] toClientBuffer = new byte[BUFFERSIZE];
        ServerSocket welcomeSocket = new ServerSocket(Integer.parseInt(args[0]));

        int portFromClient = 0;
        String host = null;
        String stringFromClient = null;
        String result = null;
        String statusMsg = null;

        while(true){
            Socket connectionSocket = welcomeSocket.accept();
            InputStream inFromClient = connectionSocket.getInputStream();
            OutputStream outToClient = connectionSocket.getOutputStream();
            StringBuilder outToClientData = new StringBuilder();
            String decodeString = "";

            int charChecker = inFromClient.read(fromClientBuffer);
            while(charChecker != -1){
                decodeString = new String(fromClientBuffer, 0, charChecker);
                String[] splitString = decodeString.split("[?&= ]", 10);


                if(splitString[0].equals("GET") && splitString[1].equals("/ask") && decodeString.contains("HTTP/1.1")){
                    statusMsg = ("HTTP/1.1 200 OK \r\n\r\n");
                    for(int i = 0; i < splitString.length; i++){
                        if(splitString[i].equals("hostname"))
                            host = splitString[i+1];
                        else if(splitString[i].equals("port"))
                            portFromClient = Integer.parseInt(splitString[i+1]);
                        else if(splitString[i].equals("string"))
                            stringFromClient = splitString[i+1];
                    }
                }
                else{
                    statusMsg = ("HTTP/1.1 400 Bad Request \r\n");
                }

                if(decodeString.contains("\n"))
                    break;
                charChecker = inFromClient.read(fromClientBuffer);
            }
            if(!(statusMsg.contains("HTTP/1.1 400 Bad Request"))){

                try{
                    if(stringFromClient != null){
                        result = TCPClient.askServer(host, portFromClient, stringFromClient);
                    }
                    else{
                        result = TCPClient.askServer(host, portFromClient);
                    }
                    outToClientData.append(statusMsg + result + "\r\n");

                } catch (IOException e) {
                    statusMsg = ("HTTP/1.1 404 Not Found \r\n");
                    outToClientData.append(statusMsg + "\n");
                }
            }
            else
                outToClientData.append(statusMsg + "\n");

            byte [] sendDataToClient = outToClientData.toString().getBytes();
            outToClient.write(sendDataToClient);
            connectionSocket.close();

        }
    }
}

