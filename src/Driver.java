import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class Driver {
    public static void main(String args[]) throws IOException {
        // String pathStr = args[0];
        String pathStr = "./data/AesopTales.txt";
        // String pathStr = "./data/CustomInput.txt";

        // For Debugging
        // Substring.shouldRepr = true;

        System.out.println("Reading documents from: " + pathStr);

        // Read input file
        Path aesopTalesPath = Paths.get(pathStr);
        List<Document> documents = Parser.parse(aesopTalesPath);

        // Print no of documents found
        System.out.println("Documents found: " + documents.size());

        // Create new Suffix Tree, initially empty
        System.out.println("Building Suffix Tree...");
        SuffixTree suffixTree = new SuffixTree();

        // Add each document into suffix tree
        for (Document document : documents) {
            suffixTree.add(document);
        }

        String query = "lion";

        // Query the suffix tree
        System.out.println("Searching for \"" + query + "\"...");
        List<Result> results = suffixTree.findAll(query);

        // Print no of matches
        System.out.println("Matches Found: " + results.size());

        // Print each match
        for (Result result : results) {
            System.out.println(result);
        }
    }
}
