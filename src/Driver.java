import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class Driver {
    public static void main(String args[]) throws IOException {
        // String pathStr = args[0];
        String pathStr = "./data/AesopTales.txt";
        // String pathStr = "./data/CustomInput.txt";

        System.out.println("Reading documents from: " + pathStr);

        // Create file path
        Path aesopTalesPath = Paths.get(pathStr);

        // Get documents from file
        List<Document> documents = Parser.parse(aesopTalesPath);

        // Print no of documents found
        System.out.println("Documents found: " + documents.size());

        // Create new Suffix Tree, initially empty
        System.out.println("Building Suffix Tree...");
        SuffixTree suffixTree = new SuffixTree();

        // For measuring run time
        long startTime = System.nanoTime();

        // Add each document into suffix tree
        for (Document document : documents)
            suffixTree.add(document);

        // For measuring run time
        long endTime = System.nanoTime();
        System.out.println("Built! took: " + (endTime - startTime) / 1000000 + " ms\n");

        // Query for the various functions
        String query = "charger";
        // String query = "ava";

        // Variable to hold the result of the query
        Result result = null;

        // Query the suffix tree
        System.out.println("Searching for \"" + query + "\"...");
        startTime = System.nanoTime();
        result = suffixTree.findAll(query);
        endTime = System.nanoTime();

        // Print each match
        System.out.println(result);
        // Print no of matches
        System.out.println("Found " + result.size() + " matches in " + (endTime - startTime) / 1000000 + " ms\n");

        query = "charger";
        // Query the suffix tree
        System.out.println("Relevance Searching for \"" + query + "\"...");
        startTime = System.nanoTime();
        result = suffixTree.findAllRelevant(query, 20);
        endTime = System.nanoTime();

        // Print each match
        System.out.println(result);
        // Print no of matches
        System.out.println("Found " + result.size() + " matches in " + (endTime - startTime) / 1000000 + " ms\n");

        // for querying first longest substring
        query = "!!!chargers as!d";
        // query = "vnana";

        // Query the suffix tree
        System.out.println("Searching for First Longest Substring of \"" + query + "\"...");

        startTime = System.nanoTime();
        result = suffixTree.findFirstLongestSubstring(query);
        endTime = System.nanoTime();

        System.out.println(result);
        System.out.println("Found " + result.size() + " matches in " + (endTime - startTime) / 1000000 + " ms");
    }
}
