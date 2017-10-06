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

        long startTime = System.nanoTime();
        // Add each document into suffix tree
        for (Document document : documents) {
            suffixTree.add(document);
        }
        long endTime = System.nanoTime();
        System.out.println("Built! took: " + (endTime - startTime) / 1000000 + " ms");

        String query = "charger";
        Result result = null;

        // Query the suffix tree
        System.out.println("\nSearching for \"" + query + "\"...");
        result = suffixTree.findAll(query, false);

        // Print no of matches
        System.out.println("Matches Found: " + result.size());

        // Print each match
        System.out.println(result);
        // System.out.println(result.getMatchedLength()+"\n"+result.size());

         query = "!!!chargers as!d";

        // Query the suffix tree
        System.out.println("searching for First Longest Substring of \"" + query + "\"...");
        result = suffixTree.findFirstLongestSubstring(query);

        // // Print no of matches
        // System.out.println("Matches Found: " + results.size());
        //
        // // Print each match
        // for (Result result : results) {
        // System.out.println(result);
        // }
    }
}
