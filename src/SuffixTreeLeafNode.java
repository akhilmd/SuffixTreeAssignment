import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SuffixTreeLeafNode extends SuffixTreeNode {
    java.util.Map<Document, Integer> suffixNumberMap = null;
    
    public SuffixTreeLeafNode (int letterDepth, Substring substring) {
        super(letterDepth, substring);
        this.suffixNumberMap = new HashMap<Document, Integer>();
    }

    public void addDocument(Document document, int suffixNumber) {
        suffixNumberMap.put(document, suffixNumber);
    }
    
    public int getSuffixNumber(Document document) {
        return suffixNumberMap.get(document);
    }

    public java.util.Map<Document, Integer> getSuffixNumberMap() {
        return suffixNumberMap;
    }

    public void setSuffixNumberMap(java.util.Map<Document, Integer> suffixNumberMap) {
        this.suffixNumberMap = suffixNumberMap;
    }

    @Override
    public String toString() {
        String op = super.toString();
        op = op.substring(0, op.length() - 1) + ", snmap=" + this.suffixNumberMap + "]";
        return op;
    }
}
