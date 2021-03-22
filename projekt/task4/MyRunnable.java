import tcpclient.TCPClient;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class MyRunnable implements Runnable {
    static int BUFFERSIZE = 1024;
    private Socket connectionSocket;

    public MyRunnable(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }


    public void run() {
        try{
            connectionSocket.setSoTimeout(10000);
            byte[] fromClientBuffer = new byte[BUFFERSIZE];
            byte[] toClientBuffer = new byte[BUFFERSIZE];

            int portFromClient = 0;
            String host = null;
            String stringFromClient = null;
            String result = null;
            String statusMsg = null;
            StringBuilder outToClientData = new StringBuilder();
            String decodeString = "";

            InputStream inFromClient = connectionSocket.getInputStream();
            OutputStream outToClient = connectionSocket.getOutputStream();

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


        } catch (Exception e) {
        }

    }
}

