import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Parser {
    public static List<Document> parse(Path path) throws IOException {
        List<Document> documents = new ArrayList<Document>();

        try (Scanner scanner = new Scanner(path, StandardCharsets.UTF_8.name())) {
            int i = 0;
            String title = "";
            String text = "";
            String line = null;
            int nlc = 0;
            while (true) {
                text = "";
                nlc = 0;

                // Read title
                if (scanner.hasNextLine()) {
                    title = scanner.nextLine().trim();
                } else
                    break;

                // Read empty line after title
                scanner.nextLine();

                // while there are no consecutive new lines, read the text
                while (nlc < 2 && scanner.hasNextLine()) {
                    line = scanner.nextLine().trim();

                    if (line.equals("")) {
                        ++nlc;
                    } else {
                        nlc = 0;
                    }

                    text += " " + line;
                }

                // Create new document and add to list
                documents.add(new Document(title, text.trim(), i));
                ++i;
            }
        }
        return documents;
    }
}
