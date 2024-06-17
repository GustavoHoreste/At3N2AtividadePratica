package shared.library;

public final class Utilities {
    public void clearConsole(){
        System.out.print("\033[2J");
        System.out.flush();
        System.out.print("Limpado?");
    }
}
