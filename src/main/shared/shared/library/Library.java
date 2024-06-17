package shared.library;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Library {
    private Gson gson;
    private JsonModel jsonModel;
    private ArrayList<Book> books;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ArrayList<Book> rentedBooks;

    private static final String BOOKS_PATH = "src/main/resources/books.json";
    private static final String USER_RENT_PATH = "src/main/resources/userBooks.json";

    public Library(ObjectOutputStream out, ObjectInputStream in) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.books = new ArrayList<>();
        this.out = out;
        this.in = in;

        this.jsonModel = new JsonModel(this.gson);
    }

    public void startAction(StateEnum choice) throws IOException, ClassNotFoundException {
        this.updateBooks();
        this.sendMessenger(this.choiceDetail(choice.toString()));

        switch (choice) {
            case LISTAR -> {
                System.out.println("Listar livros");
                getBooks();
            }
            case ALUGAR -> {
                System.out.println("Alugar livro");
                this.toRent();
            }
            case DEVOLVER -> {
                System.out.println("Devolver livro");
                this.giveBack();
            }
            case CADASTRAR -> {
                System.out.println("Cadastrar livro");
                addBook();
            }
            case USER_BOOKS -> {
                System.out.println("Listar livros do user");
                getUserBooks();
            }
            case SAIR -> {
                actionExit();
            }
        }

        this.awaitResponse();
        this.menu(); //Renicia o fluxo
    }

    public void actionExit() throws IOException {
        System.out.println("Sair");
        this.sendMessenger("Saindo");
        System.exit(0);
    }

    public void menu() throws IOException, ClassNotFoundException {
        System.out.println("\u001B[36mMaking question...\u001B[0m");

        String choice;
        StringBuilder questions = new StringBuilder();
        questions.append("\u001B[33mEscolha uma ação a ser executada:\n")
                .append("\u001B[35m1. Listar livros\n")
                .append("2. Alugar livro\n")
                .append("3. Devolver livro\n")
                .append("4. Cadastrar livro\n")
                .append("5. livros Alugados\n")
                .append("6. Sair\n")
                .append("\u001B[33mOpção: \u001B[0m");

        sendMessenger(questions.toString());

        do {
            choice = this.receiveMensager();

            if (choice.isEmpty() || !choice.matches("[1-6]")) {
                System.out.println("\u001B[31mOpção inválida: Repetindo pergunta\u001B[0m");
                this.sendMessenger("\u001B[31mOpção inválida, por favor, escreva uma Opção válida.\u001B[0m");
            }
        } while (choice.isEmpty() || !choice.matches("[1-6]"));

        int valueConverted = Integer.parseInt(choice);
        this.startAction(StateEnum.values()[valueConverted - 1]);
    }


    public void awaitResponse() throws IOException, ClassNotFoundException {
        String response;
        do {
            this.sendMessenger("Deseja continuar Execução? [S|N]\n");
            response = this.receiveMensager();

            if (!isValidResponse(response)) {
                this.sendMessenger("Valor inválido. Digite 'S' ou 'N'.");
            }
        } while (!isValidResponse(response));

        if (response.equals("N")) {
            actionExit();
        }
    }

    private boolean isValidResponse(String response) {
        return response != null && (response.equalsIgnoreCase("S") || response.equalsIgnoreCase("N"));
    }

    public void updateBooks(){
        books = (ArrayList<Book>) jsonModel.getDataFromJson(BOOKS_PATH);
        rentedBooks = (ArrayList<Book>) jsonModel.getDataFromJson(USER_RENT_PATH);
    }

    public void addBook() throws IOException, ClassNotFoundException {
        sendMessenger("Titulo: ");
        String title = receiveMensager();
        sendMessenger("Genero: ");
        String genre = receiveMensager();
        sendMessenger("Autor: ");
        String author = receiveMensager();
        sendMessenger("Quantidade: ");
        String copies = receiveMensager();

        Book book = new Book(title, author, genre, Integer.parseInt(copies));
        books.add(book);

        jsonModel.saveDataToJson(this.books, BOOKS_PATH);
    }

    public void toRent() throws IOException, ClassNotFoundException {
        ArrayList<String> filterBooks = new ArrayList<>();
        Book currentBook = null;
        int quantity;
        String name;

        for (Book book : books) {
            filterBooks.add(book.toStringName());
        }

        do{
            sendMessenger(books.toString());

            this.sendMessenger("Digite o nome do livro: ");
            name = this.receiveMensager().trim();

            boolean bookFound = false;

            for (Book book : books) {
                if (book.getTitle().equalsIgnoreCase(name)) {
                    currentBook = book;
                    bookFound = true;
                    break;
                }
            }

            if (!bookFound || currentBook.getCopies() <= 0) {
                System.out.println("Livro inválido: " + currentBook.getCopies() + currentBook.getTitle());
                sendMessenger("[!] Livro inválido" + currentBook.getTitle() + " " + currentBook.getCopies());
            }

        }while (currentBook == null);

        do {
            sendMessenger("Qual a quantidade: ");
            quantity = Integer.parseInt(receiveMensager());

            if (quantity > currentBook.getCopies() || quantity <= 0) {
                this.sendMessenger("Quantidade: " + quantity + " inválida");
            }

        } while (quantity > currentBook.getCopies() || quantity <= 0);

        currentBook.setCopies(currentBook.getCopies() - quantity);
        updateBook(currentBook);
        updateUserBook(currentBook, quantity);

        sendMessenger("Livro alugado com sucesso: " + currentBook.toStringName());
    }

    public void giveBack() throws IOException, ClassNotFoundException {
        if (rentedBooks.isEmpty()) {
            sendMessenger("Você não tem livros Alugados.");
            return;
        }

        StringBuilder rentedBooksList = new StringBuilder("Livros alugados:\n");
        for (Book book : rentedBooks) {
            rentedBooksList
                    .append(book.toStringName());
        }
        sendMessenger(rentedBooksList.toString());

        // Perguntar qual livro devolver
        Book bookToReturn = null;
        String bookTitle;
        do {
            sendMessenger("Digite o nome do livro que deseja devolver: ");
            bookTitle = receiveMensager().trim();

            for (Book book : rentedBooks) {
                if (book.getTitle().equalsIgnoreCase(bookTitle)) {
                    bookToReturn = book;
                    break;
                }
            }

            if (bookToReturn == null) {
                sendMessenger("[!] Nome inválido");
            }
        } while (bookToReturn == null);

        int quantityToReturn;
        do {
            sendMessenger("Digite a quantidade que deseja devolver: ");
            quantityToReturn = Integer.parseInt(receiveMensager());

            if (quantityToReturn > bookToReturn.getCopies() || quantityToReturn <= 0) {
                sendMessenger("Quantidade inválida: " + quantityToReturn);
            }
        } while (quantityToReturn > bookToReturn.getCopies() || quantityToReturn <= 0);

        bookToReturn.setCopies(bookToReturn.getCopies() - quantityToReturn);
        if (bookToReturn.getCopies() == 0) {
            rentedBooks.remove(bookToReturn);
        }
        jsonModel.saveDataToJson(rentedBooks, USER_RENT_PATH);

        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(bookTitle)) {
                book.setCopies(book.getCopies() + quantityToReturn);
                updateBook(book);
                break;
            }
        }

        sendMessenger("Livro devolvido com sucesso: " + bookToReturn.toStringName());
    }

    public void updateBook(Book book) {
        int index = books.indexOf(book);
        if (index >= 0) {
            books.set(index, book);
            jsonModel.saveDataToJson(this.books, BOOKS_PATH);
        } else {
            System.out.println("Erro: Livro não encontrado na biblioteca");
        }
    }

    private void updateUserBook(Book book, int copies) throws IOException {
        book.setCopies(copies);
        rentedBooks.add(book);
        jsonModel.saveDataToJson(rentedBooks, USER_RENT_PATH); // salvando em user
    }

    public void sendMessenger(String message) throws IOException {
        out.writeObject(message);;
        out.flush();
    }

    public String receiveMensager() throws IOException, ClassNotFoundException {
        String mensager = (String) in.readObject();
        System.out.println("Mensgam recebida: " + mensager);
        return mensager;
    }

    public void getBooks() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Book book : books) {
            sb.append(book.toString());
        }
        this.sendMessenger(sb.toString());
    }

    public void getUserBooks() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Book book : rentedBooks) {
            sb.append(book.toString());
        }
        this.sendMessenger(sb.toString());
    }

    public String choiceDetail(String choice) {
        String value = (
                "\u001B[1m\u001B[37m" + "+========================+" + "\u001B[0m\n" +
                        "\u001B[32m" + "|      \t" + "\u001B[1m" + choice + "\u001B[0m\u001B[32m" + "       \t|" + "\u001B[0m\n" +
                        "\u001B[1m\u001B[37m" + "+========================+" + "\u001B[0m\n"
        );
        return value;
    }
}
