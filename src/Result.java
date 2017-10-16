import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Result {
    private List<Document> documents = null;
    private List<Integer> indices = null;
    private List<String> messages = null;
    private int queryLength = -1;

    private static final int PADDING = 10;

    private double percentageMatched = -1.0;
    private int numMatched = -1;
    private Map<Document, Tuple<Integer, Integer, String>> longestSubstringMap = null;

    public Result(int queryLength) {
        this.indices = new ArrayList<Integer>();
        this.documents = new ArrayList<Document>();
        this.messages = new ArrayList<String>();
        this.queryLength = queryLength;
    }

    public Map<Document, Tuple<Integer, Integer, String>> getLongestSubstringMap() {
        return longestSubstringMap;
    }

    public void setLongestSubstringMap(Map<Document, Tuple<Integer, Integer, String>> longestSubstringMap) {
        this.longestSubstringMap = longestSubstringMap;
    }

    public double getPercentageMatched() {
        return percentageMatched;
    }

    public void setPercentageMatched(double percentageMatched) {
        this.percentageMatched = percentageMatched;
    }

    public int getNumMatched() {
        return numMatched;
    }

    public void setNumMatched(int numMatched) {
        this.numMatched = numMatched;
    }

    public void add(Document document, int index) {
        documents.add(document);
        indices.add(index);
        messages.add("");
    }

    public void add(Document document, int index, String message) {
        documents.add(document);
        indices.add(index);
        messages.add(message);
    }

    public int size() {
        return indices.size();
    }

    @Override
    public String toString() {
        String op = "";
        for (int i = 0; i < indices.size(); ++i) {
            Document document = documents.get(i);
            int index = indices.get(i);
            String message = messages.get(i);
            op += message + "Title: [" + document.getTitle() + "]    Text: [";

            int start = index - PADDING;
            int end = index + queryLength + PADDING;

            String prefix = "…";
            String suffix = "…";

            start = start < 0 ? 0 : start;
            end = end > document.getText().length() ? document.getText().length() : end;

            if (start == 0) {
                prefix = "";
            }

            if (end == document.getText().length()) {
                suffix = "";
            }

            op += prefix + document.getText().substring(start, end) + suffix + "]\n";
        }
        if (op.length() > 0)
            op = op.substring(0, op.length() - 1);
        return op;
    }
}
