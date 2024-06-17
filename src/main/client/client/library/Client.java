package client.library;

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

    public static void main(String[] args) {
        try{
            socket = new Socket("localhost", 7777);
            sendMessage();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String writeMessage() {
        Scanner scanner = new Scanner(System.in);
        String message;

        do {
            System.out.print("Escreva uma mensagem: ");
            message = scanner.nextLine();

            if (message.isEmpty()) {
                System.out.println("Mensagem inválida, por favor, escreva uma mensagem válida.");
            }
        }while (message.isEmpty());

        return message;
    }

    public static void sendMessage() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        while (true){
            String mensager = writeMessage();
            out.writeObject(mensager);
        }
    }

    public static void recieveMessage(String message) {

    }
}


