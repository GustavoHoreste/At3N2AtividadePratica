package server.library;

import shared.library.Library;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ServerSocket serverSocket;
    private static Library library;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(7777);
            System.out.println("Server started on port: " + serverSocket.getLocalPort());
            startServer();
        } catch (IOException e) {
            System.out.println("Server failed to start on port: " + serverSocket.getLocalPort());
            e.printStackTrace();
        }
    }

    public static void startServer() throws IOException {
        while (true) {
            Socket connectionSocket = serverSocket.accept();
            new Thread(() -> {
                try {
                    System.out.println("Thread criada\nServer accepted connection from: " + connectionSocket.getRemoteSocketAddress());
                    ObjectOutputStream out = new ObjectOutputStream(connectionSocket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream());
                    library = new Library(out, in);
                    library.menu();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}
