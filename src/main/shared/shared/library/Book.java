package shared.library;

public class Book {
    private String title;
    private String author;
    private String genre;
    private int copies;

    public Book(String title, String author, String gender, int copies) {
        this.title = title;
        this.author = author;
        this.genre = gender;
        this.copies = copies;
    }


    @Override
    public String toString() {
        String reset = "\u001B[0m";
        String bold = "\u001B[1m";
        String red = "\u001B[31m";
        String green = "\u001B[32m";
        String yellow = "\u001B[33m";
        String cyan = "\u001B[36m";

        return bold + "Title: " + red + this.title + reset + ", " +
                bold + "Author: " + green + this.author + reset + ", " +
                bold + "Genre: " + yellow + this.genre + reset + ", " +
                bold + "Copies: " + cyan + this.copies + reset + "\n";
    }

    public String toStringName() {
        String reset = "\u001B[0m";
        String bold = "\u001B[1m";
        String red = "\u001B[31m";
        String cyan = "\u001B[36m";

        return bold + "Title: " + red + this.title + reset + ", " +
                bold + "Copies: " + cyan + this.copies + reset + "\n";
    }

    public boolean quantityCopies() {
        if (this.copies > 0) {
            this.copies--;
            return true;
        } else {
            return false;
        }
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
