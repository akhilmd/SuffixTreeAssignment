import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Parser {
    public static List<Document> parse(Path path) throws IOException {
        // System.out.println("Reading file: " + args[0]);
        // Path path = Paths.get(args[0]);
        
        List<Document> documents = new ArrayList<Document>();

        try (Scanner scanner = new Scanner(path, StandardCharsets.UTF_8.name())) {
            int i = 0;
            String title = "";
            String text = "";
            String line = null;
            int nlc = 0;
            while(true) {
                text = "";
                nlc = 0;
                if (scanner.hasNextLine()) {
                    title = scanner.nextLine().trim();
                    // System.out.println("{"+title+"}");
                } else break;
                scanner.nextLine();
                while (nlc<2 && scanner.hasNextLine()) {
                    line = scanner.nextLine().trim();
                    
                    if (line.equals("")) {
                        ++nlc;
                    } else {
                        nlc = 0;
                    }
                    
                    text += " " + line;
                    //System.out.println("["+line+"]");
                    ++i;
                }
                //System.out.println("XXXXXXXXXXXXXXXXXXXXXXXX");
                // System.out.println(new Document(title, text.trim()));
                documents.add(new Document(title, text.trim()));
            }
        }
        return documents;
    }
}
