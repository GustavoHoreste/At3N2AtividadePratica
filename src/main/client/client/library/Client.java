package client.library;

import java.io.IOException;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) {
        try {
            /* Cria uma instância de ClientHandler e inicia a comunicação */
            ClientHandler clientHandler = new ClientHandler("localhost", 7777);
            clientHandler.start();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
