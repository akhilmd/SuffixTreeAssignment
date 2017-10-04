import java.util.ArrayList;
import java.util.List;

public class SuffixTree {
    private SuffixTreeNode root = null;
    // private byte[] text = null;

    public SuffixTree () {
        root = new SuffixTreeNode(0, new Substring(0, 0, null));
    }

    public void add(Document document) {
        String pre = document.getText() + "\0";
        byte[] text = pre.getBytes();

        // System.out.println("Adding document to Suffix Tree: " + document);

        for (int i = 0; i < text.length; ++i) {
            Substring substring = new Substring(i, text.length - 1, text);
            // System.out.print(i + ": ");
            // for (int j = i; j < text.length; ++j)
            // System.out.print((char) text[j]);
            // System.out.println();

            this.insert(root, substring, i, document);
        }
        // System.out.println("Pre-order traversal of built tree:");
        // traverse(root, true);
        // System.out.println("Search for \"a\":");
        // findAll("a");
    }

    private void insert(SuffixTreeNode node, Substring substring, int suffixNumber, Document document) {
        int b = substring.getBegin();
        // byte[] text = substring.getText();
        SuffixTreeNode child = node.getChild(substring.get(b));

        if (child == null) {
            SuffixTreeLeafNode newChild = new SuffixTreeLeafNode(
                    substring.getLength() + node.getLetterDepth(),
                    substring);
            newChild.addDocument(document, suffixNumber);
            node.setChild(substring.get(b), newChild);
            return;
        }

        // move down edge based on child's substring range
        Substring childSubstring = child.getSubstring();
        // System.out.println(childSubstring);
        
        // i is from text being inserted
        // j is from text already inserted
        int i = 0, j = 0, c = 0;
        for (
            i = b, j = childSubstring.getBegin(), c = 1;
            i <= substring.getEnd() && j <= childSubstring.getEnd();
            ++i, ++j, ++c) {
            // System.out.println((char)substring.get(i) +" != "+ (char)childSubstring.get(j));
            if (substring.get(i) != childSubstring.get(j)) {
                // mismatch in the middle
                SuffixTreeNode newInternalNode = new SuffixTreeNode(
                        node.getLetterDepth() + c - 1,
                        new Substring(b, i - 1, substring.getText()));

                childSubstring.setBegin(j);
                child.setSubstring(childSubstring);
                newInternalNode.setChild(childSubstring.get(j), child);

                SuffixTreeLeafNode newLeafNode = new SuffixTreeLeafNode(
                        substring.getLength(),
                        new Substring(i, substring.getEnd(), substring.getText()));
                newLeafNode.addDocument(document, suffixNumber);
                newInternalNode.setChild(substring.get(i), newLeafNode);
                node.setChild(substring.get(b), newInternalNode);
                // System.out.println(newLeafNode);
                return;
            }
        }

        if (i <= substring.getEnd() && j > childSubstring.getEnd()) {
            insert(child, new Substring(i, substring.getEnd(), substring.getText()), suffixNumber, document);
        }
        
        if (i == 1 + substring.getEnd() && j == 1 + childSubstring.getEnd()) {
            ((SuffixTreeLeafNode)child).addDocument(document, suffixNumber);
        }
        return;
    }

    // For Debug
    private void traverse(SuffixTreeNode node, boolean printAll) {
        if (node == null) return;
        if (printAll || (!printAll && node instanceof SuffixTreeLeafNode)) {
            System.out.println(node);
        }

        for (int i = 0; i < node.getChildren().length; ++i) {
            traverse(node.getChild((byte)i), printAll);
        }
        return;
    }

    private List<SuffixTreeLeafNode> getDescendantLeafNodes(SuffixTreeNode node) {
        List<SuffixTreeLeafNode> descendants = new ArrayList<SuffixTreeLeafNode>(); 
        if (node == null)
            return descendants;
        
        if (node instanceof SuffixTreeLeafNode) {
            descendants.add((SuffixTreeLeafNode) node);
        }

        for (int i = 0; i < node.getChildren().length; ++i) {
            List<SuffixTreeLeafNode> childDescendants = getDescendantLeafNodes(node.getChild((byte)i));
            descendants.addAll(childDescendants);
        }
        return descendants;
    }

    public List<Result> findAll(String query) {
        List<Result> results = new ArrayList<Result>();
        byte[] bytesQuery = query.getBytes();
        SuffixTreeNode child = root.getChild(bytesQuery[0]);
        int i = 0, j = 0;
        while(child != null) {
            // System.out.println("child = " + child);
            byte[] text = child.getSubstring().getText();
            for (
                i = child.getSubstring().getBegin();
                i <= child.getSubstring().getEnd() && j < bytesQuery.length;
                ++i, ++j) {
                if (text[i] != bytesQuery[j]) {
                    // System.out.println((char)text[i]+" != " + (char)bytesQuery[j]);
                    return results;
                }
            }
            // System.out.println(i+","+j);
            if (i > child.getSubstring().getEnd() && j < bytesQuery.length) {
                child = child.getChild(bytesQuery[j]);
            } else if (i <= child.getSubstring().getEnd() && j >= bytesQuery.length) {
                List<SuffixTreeLeafNode> descendants = getDescendantLeafNodes(child);
                int queryLength = query.length();
                for (SuffixTreeLeafNode descendant : descendants) {
                    for (i = 0; i < descendant.getDocuments().size(); ++i) {
                        results.add(new Result(descendant.getSuffixNumbers().get(i), descendant.getDocuments().get(i), queryLength));
                    }
                }
                return results;
            }
            
        }
        return results;
    }
}
