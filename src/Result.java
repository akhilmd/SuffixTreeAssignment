import java.util.ArrayList;
import java.util.List;

public class Result {
    private List<Document> documents = null;
    private List<Integer> indices = null;
    private int queryLength = -1;
    private int matchedLength = -1;

    private static final int PADDING = 10;

    public Result(int queryLength) {
        this.indices = new ArrayList<Integer>();
        this.documents = new ArrayList<Document>();
        this.queryLength = queryLength;
        this.matchedLength = 0;
    }

    public int getMatchedLength() {
        return matchedLength;
    }

    public void setMatchedLength(int matchedLength) {
        this.matchedLength = matchedLength;
    }

    public void add(Document document, int index) {
        documents.add(document);
        indices.add(index);
    }
    
    public int size () {
        return indices.size();
    }
    
    @Override
    public String toString() {
        String op = "";
        for (int i = 0; i < indices.size(); ++i) {
            Document document = documents.get(i);
            int index = indices.get(i);
            op += index+" Title: [" + document.getTitle() + "]    Text: [";

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
        op = op.substring(0, op.length() - 1);
        return op;
    }
}
