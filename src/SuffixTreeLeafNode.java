import java.util.ArrayList;
import java.util.List;

public class SuffixTreeLeafNode extends SuffixTreeNode {
    private List<Integer> suffixNumbers = null;
    private List<Document> documents = null;
    
    public SuffixTreeLeafNode (int letterDepth, Substring substring) {
        super(letterDepth, substring);
        this.suffixNumbers = new ArrayList<Integer>();
        this.documents = new ArrayList<Document>();
    }

    public List<Integer> getSuffixNumbers() {
        return suffixNumbers;
    }

    public void setSuffixNumbers(List<Integer> suffixNumbers) {
        this.suffixNumbers = suffixNumbers;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public void addDocument(Document document, int suffixNumber) {
        this.documents.add(document);
        this.suffixNumbers.add(suffixNumber);
    }

    @Override
    public String toString() {
        String op = super.toString();
        op = op.substring(0, op.length() - 1) + ", sn=" + this.suffixNumbers + ", " + documents + "]";
        return op;
    }
}
