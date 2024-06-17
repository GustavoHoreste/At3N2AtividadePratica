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
            socket = new Socket("localhost", 3333);
            recieveMessage();
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
            out.writeObject(mensager);//Enviar mensagem para o servidor.
        }
    }

    public static void recieveMessage() throws IOException {
        new Thread(() -> { // Thread que recebe os dados do server.
            while (true){
                try {
                    in = new ObjectInputStream(socket.getInputStream());
                    System.out.println(in.readObject().toString());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}


