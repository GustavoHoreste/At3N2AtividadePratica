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

    public void makeQuestion() throws IOException, ClassNotFoundException {
        System.out.println("Making question...");

        String choice;
        StringBuilder questions = new StringBuilder();
        questions.append("Escolha uma ação a ser executada:\n")
                .append("1. Listar livros\n")
                .append("2. Alugar livro\n")
                .append("3. Devolver livro\n")
                .append("4. Cadastrar livro\n")
                .append("5. livros Aluagados\n")
                .append("6. Sair\n")
                .append("Opção: ");

        sendMessenger(questions.toString());

        do {
            choice = this.reciveMensager();

            if (choice.isEmpty() || !choice.matches("[1-6]")) {
                System.out.println("Opção inválida: Repetindo pergunta");
                this.sendMessenger("Opção inválida, por favor, escreva uma Opção válida.");
            }
        } while (choice.isEmpty() || !choice.matches("[1-6]"));

        int valurConverted = Integer.parseInt(choice);
        this.startAction(StateEnum.values()[valurConverted - 1]);
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
                System.out.println("Sair");
                sendMessenger("Saindo");
                System.exit(0);
            }
        }

        this.makeQuestion(); //Renicia o fluxo
    }


    //Print de todos os objetos no json
    public void updateBooks(){
        books = (ArrayList<Book>) jsonModel.getDataFromJson(BOOKS_PATH);
        rentedBooks = (ArrayList<Book>) jsonModel.getDataFromJson(USER_RENT_PATH);
    }

    public void removeBook(Book book){
        System.out.println("Removing book " + book.getTitle());
        books.remove(book);
    }

    public void addBook() throws IOException, ClassNotFoundException {
        sendMessenger("Titulo: ");
        String title = reciveMensager();
        sendMessenger("Genero: ");
        String genre = reciveMensager();
        sendMessenger("Autor: ");
        String author = reciveMensager();
        sendMessenger("Quantidade: ");
        String copies = reciveMensager();

        Book book = new Book(title, author, genre, Integer.parseInt(copies));
        books.add(book);

        jsonModel.saveDataToJson(this.books, BOOKS_PATH);
    }

    public void sendMessenger(String message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    public String reciveMensager() throws IOException, ClassNotFoundException {
        String mensager = (String) in.readObject();
        System.out.println(mensager);
        return mensager;
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
            name = this.reciveMensager().trim();

            boolean bookFound = false;
            for (Book book : books) {
                if (book.getTitle().equalsIgnoreCase(name)) {
                    currentBook = book;
                    bookFound = true;
                    break;
                }
            }

            if (!bookFound) {
                System.out.println("Nome inválido: " + name);
                sendMessenger("[!] Nome inválido");
            }

        }while (currentBook == null);

        if (currentBook.getCopies() <= 0) {
            sendMessenger("Livro indisponivel");
            return;
        }

        do {
            sendMessenger("Qual a quantidade: ");
            quantity = Integer.parseInt(reciveMensager());

            if (quantity > currentBook.getCopies() || quantity <= 0) {
                this.sendMessenger("Quantidade: " + quantity + " inválida");
            }

        } while (quantity > currentBook.getCopies() || quantity <= 0);

        currentBook.setCopies(currentBook.getCopies() - quantity);
        updateBook(currentBook);
        updateUserBook(currentBook, quantity);

        sendMessenger("Livro alugado com sucesso: " + currentBook.toStringName());
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

    public void giveBack() throws IOException, ClassNotFoundException {
        if (rentedBooks.isEmpty()) {
            sendMessenger("Você não tem livros alugados.");
            return;
        }

        StringBuilder rentedBooksList = new StringBuilder("Livros alugados:\n");
        for (Book book : rentedBooks) {
            rentedBooksList.append(book.toStringName()).append(" - Quantidade: ").append(book.getCopies()).append("\n");
        }
        sendMessenger(rentedBooksList.toString());

        // Perguntar qual livro devolver
        Book bookToReturn = null;
        String bookTitle;
        do {
            sendMessenger("Digite o nome do livro que deseja devolver: ");
            bookTitle = reciveMensager().trim();

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

        // Perguntar a quantidade a devolver
        int quantityToReturn;
        do {
            sendMessenger("Digite a quantidade que deseja devolver: ");
            quantityToReturn = Integer.parseInt(reciveMensager());

            if (quantityToReturn > bookToReturn.getCopies() || quantityToReturn <= 0) {
                sendMessenger("Quantidade inválida: " + quantityToReturn);
            }
        } while (quantityToReturn > bookToReturn.getCopies() || quantityToReturn <= 0);

        // Atualizar a quantidade no arquivo de livros alugados
        bookToReturn.setCopies(bookToReturn.getCopies() - quantityToReturn);
        if (bookToReturn.getCopies() == 0) {
            rentedBooks.remove(bookToReturn);
        }
        jsonModel.saveDataToJson(rentedBooks, USER_RENT_PATH);

        // Atualizar a quantidade no arquivo de livros da biblioteca
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(bookTitle)) {
                book.setCopies(book.getCopies() + quantityToReturn);
                updateBook(book);
                break;
            }
        }

        sendMessenger("Livro devolvido com sucesso: " + bookToReturn.toStringName());
    }


    public void getBooks() throws IOException {
        this.sendMessenger(books.toString());
    }

    public void getUserBooks() throws IOException {
        this.sendMessenger(rentedBooks.toString());
    }

    public String choiceDetail(String choice){
        String value = ("+========================+\n" + ("+\t\t  " + choice + "\t\t  +\n") + ("+========================+\n"));
        return value;
    }
}
