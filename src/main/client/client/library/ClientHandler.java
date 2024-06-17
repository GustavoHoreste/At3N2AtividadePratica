package client.library;

import shared.library.StateEnum;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientHandler {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(String host, int port) throws UnknownHostException, IOException {
        this.InitClass(host, port);
    }

    private void InitClass(String host, int port) throws IOException {
        try {
            this.socket = new Socket(host, port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            System.out.println("Conexão falhou: Host desconhecido");
            throw e;
        } catch (IOException e) {
            System.out.println("Conexão falhou: Erro de I/O");
            throw e;
        }
    }

    /* Método para iniciar a thread de recebimento de mensagens */
    public void start() {
        new Thread(this::receiveMessage).start();
    }

    /* Método para ler mensagem do usuário */
    public String writeMessage() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    /* Método para enviar mensagem ao servidor */
    public void sendMessage() throws IOException {
        String message = writeMessage();
        out.writeObject(message);
    }

    /* Método para receber mensagens do servidor */
    public void receiveMessage() {
        while (true) {
            try {
                Object message = in.readObject();
                System.out.println(message);
                if (message.toString().contains("Saindo")) {
                    stopClient();
                    break;
                } else if (message != null && !message.toString().contains("=") && !message.toString().contains("Title")) {
                    sendMessage();
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* Método para encerrar a conexão com o servidor */
    public void stopClient() {
        try {
            in.close();
            out.close();
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
