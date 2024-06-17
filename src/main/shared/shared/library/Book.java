package shared.library;

public class Book {
    private String title;
    private String author;
    private String genre;
    private int copies;

    private static final String reset = "\u001B[0m";
    private static final String bold = "\u001B[1m";
    private static final String red = "\u001B[31m";
    private static final String green = "\u001B[32m";
    private static final String yellow = "\u001B[33m";
    private static final String cyan = "\u001B[36m";

    public Book(String title, String author, String gender, int copies) {
        this.title = title;
        this.author = author;
        this.genre = gender;
        this.copies = copies;
    }

    @Override
    public String toString() {
        return bold + "Title: " + red + this.title + reset + ", " +
                bold + "Author: " + green + this.author + reset + ", " +
                bold + "Genre: " + yellow + this.genre + reset + ", " +
                bold + "Copies: " + cyan + this.copies + reset + "\n";
    }

    public String toStringName() {
        return bold + "Title: " + red + this.title + reset + ", " +
                bold + "Copies: " + cyan + this.copies + reset + "\n";
    }



    //TODO: Gets e Setts
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGender() {
        return genre;
    }

    public void setGender(String gender) {
        this.genre = gender;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }
}
