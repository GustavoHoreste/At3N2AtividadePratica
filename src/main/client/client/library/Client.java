package client.library;

import shared.library.StateEnum;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static volatile boolean running;

    public static void main(String[] args) {
        try {
            running = true;
            socket = new Socket("localhost", 7777);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            new Thread(Client::recieveMessage).start();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String writeMessage() {
        Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();
        return message;
    }

    public static void sendMessage() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();
        out.writeObject(message);
        running = false;
    }

    public static void recieveMessage() {
        while (true) {
            try {
                Object mensager = in.readObject();
                System.out.println(mensager);
                if (mensager.toString().contains("Saindo")) {
                    System.out.print("Saindo");
                    stopClient();
                    break;
                } else if (mensager != null && !mensager.toString().contains("=") && !mensager.toString().contains("Title")) {
                    sendMessage();
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void stopClient() throws IOException {
        in.close();
        out.close();
        socket.close();
        System.exit(0);
    }
}
