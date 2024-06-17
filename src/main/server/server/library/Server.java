package server.library;

import com.google.gson.Gson;
import shared.library.StateEnum;
import shared.library.JsonModel;

import java.awt.event.TextEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        try {
            serverSocket = new ServerSocket(7777);
            System.out.println("Server started on port: " + serverSocket.getLocalPort());
            startServer();

        }catch (IOException e) {
            System.out.println("Server failed to start on port: " + serverSocket.getLocalPort());
            e.printStackTrace();
        }

    }

    public static void startServer() throws IOException {
//        while (true){
        Socket connectionSocket = serverSocket.accept();
        new Thread(() -> {
            System.out.println("Thraed criado\nServer accepted connection from: " + connectionSocket.getRemoteSocketAddress());
            receiveMessenger(connectionSocket);
        }).start();
//        }
    }

    public static  void stopServer() throws IOException {
        serverSocket.close();
    }


    //recebe os dados do client
    public static void receiveMessenger(Socket connectionSocket){
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(connectionSocket.getInputStream());
            while (true) {
                Object receivedObject = objectInputStream.readObject();
                if (receivedObject == null) {
                    break; // Encerra o loop se não houver mais dados para ler
                }
                System.out.println("Mensagem recebida: " + receivedObject.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
