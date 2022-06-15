import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("enter source code path");
        Scanner scanner = new Scanner(System.in);
        String filePath = scanner.nextLine();
        File srcCode = new File(filePath);
        RecursiveDescent recursiveDescent = null;
        try {
            recursiveDescent = new RecursiveDescent(srcCode);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(recursiveDescent != null) {
            recursiveDescent.compile();
        }

    }
}
