import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuffixTree {
    private SuffixTreeNode root = null;
    List<Document> documents = null;
    // private byte[] text = null;

    public SuffixTree () {
        root = new SuffixTreeNode(0, new Substring(0, 0, null));
        this.documents = new ArrayList<Document>();
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
        documents.add(document);
        // System.out.println("Pre-order traversal of built tree:");
        //traverse(root, true);
        // System.out.println("Search for \"a\":");
        // findAll("a");
//        int c = 0;
//        for (SuffixTreeNode child : root.getChildren()) {
//            if (child != null) {
//                c++;
//            }
//        }
//        System.out.println(c);
    }

    private SuffixTreeLeafNode insert(SuffixTreeNode node, Substring substring, int suffixNumber, Document document) {
        // Substring.shouldRepr = true;
        // System.out.println(document.getText() + "," + suffixNumber + " inserting: " +
        // substring);
        // Substring.shouldRepr=false;
        int b = substring.getBegin();
        // byte[] text = substring.getText();
        SuffixTreeNode child = node.getChild(substring.get(b));
//        System.out.println(child instanceof SuffixTreeLeafNode);
        if (child == null) {
            SuffixTreeLeafNode newChild = new SuffixTreeLeafNode(
                    substring.getLength() + node.getLetterDepth(),
                    substring);
            newChild.addDocument(document, suffixNumber);
            node.addFirstOccurrence(document, newChild);
            newChild.addFirstOccurrence(document, newChild);
            node.setChild(substring.get(b), newChild);
            return newChild;
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
                newLeafNode.addFirstOccurrence(document, newLeafNode);
                
                for (Map.Entry<Document,SuffixTreeLeafNode> entry : child.getFirstOccurrencesMap().entrySet()) {
                    newInternalNode.addFirstOccurrence(entry.getKey(), entry.getValue());
                }
                newInternalNode.addFirstOccurrence(document, newLeafNode);
                
                newInternalNode.setChild(substring.get(i), newLeafNode);
                node.setChild(substring.get(b), newInternalNode);
                // System.out.println(newLeafNode);
                return newLeafNode;
            }
        }

        if (i <= substring.getEnd() && j > childSubstring.getEnd()) {
            SuffixTreeLeafNode newInsertedDdescendant = insert(child, new Substring(i, substring.getEnd(), substring.getText()), suffixNumber, document);
            child.addFirstOccurrence(document, newInsertedDdescendant);
            return newInsertedDdescendant;
        }
        
        if (i == 1 + substring.getEnd() && j == 1 + childSubstring.getEnd()) {
            ((SuffixTreeLeafNode)child).addDocument(document, suffixNumber);
            node.addFirstOccurrence(document, (SuffixTreeLeafNode) child);
            ((SuffixTreeLeafNode)child).addFirstOccurrence(document, (SuffixTreeLeafNode)child);
            return (SuffixTreeLeafNode)child;
        }
        return null;
    }

    // For Debug
    private void traverse(SuffixTreeNode node, boolean printAll) {
        if (node == null) return;
        Substring.shouldRepr=true;
        if (printAll || (!printAll && node instanceof SuffixTreeLeafNode)) {
            System.out.println(node);
        }
        Substring.shouldRepr=false;
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

    public Result findAll(String query, boolean doingFirstOccurrence) {
        // System.out.println("findAll:"+query);
        Result result = new Result(query.length());
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
                    result.setMatchedLength(j);
                    // System.out.println((char)text[i]+" != " + (char)bytesQuery[j]);
                    return result;
                }
            }
            result.setMatchedLength(j);
            if (i > child.getSubstring().getEnd() && j < bytesQuery.length) {
                child = child.getChild(bytesQuery[j]);
            } else if (i <= child.getSubstring().getEnd() && j >= bytesQuery.length) {
                if (doingFirstOccurrence) {
                    // System.out.println("child's first occ map:"+child.getFirstOccurrencesMap());
                    Substring.shouldRepr=true;
                    for (Map.Entry<Document,SuffixTreeLeafNode> entry : child.getFirstOccurrencesMap().entrySet()) {
                        result.add(entry.getKey(), entry.getValue().getSuffixNumber(entry.getKey()));
                        // System.out.println(entry.getKey().getIndex()+" ====== "+entry.getValue().getSuffixNumber(entry.getKey()));
                    }
                    Substring.shouldRepr=false;
                } else {
                    List<SuffixTreeLeafNode> descendants = getDescendantLeafNodes(child);
                    int queryLength = query.length();
                    for (SuffixTreeLeafNode descendant : descendants) {
                        for (Map.Entry<Document, Integer> entry : descendant.getSuffixNumberMap().entrySet()) {
                            result.add(entry.getKey(), entry.getValue());
                            // System.out.println(entry.getKey().getIndex()+" ====== "+entry.getValue().getSuffixNumbers());
                        }
                        // for (i = 0; i < descendant.getDocuments().size(); ++i) {
                        // result.add(descendant.getDocuments().get(i),
                        // descendant.getSuffixNumbers().get(i));
                        // }
                    }
                }
                return result;
            }
        }
        result.setMatchedLength(j);
        return result;
    }
    
    public Result findOne(String query, Document document) {
        // System.out.println("findAll:"+query);
        Result result = new Result(query.length());
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
                    result.setMatchedLength(j);
                    // System.out.println((char)text[i]+" != " + (char)bytesQuery[j]);
                    return result;
                }
            }
            result.setMatchedLength(j);
            if (i > child.getSubstring().getEnd() && j < bytesQuery.length) {
                child = child.getChild(bytesQuery[j]);
            } else if (i <= child.getSubstring().getEnd() && j >= bytesQuery.length) {
                // System.out.println("child's first occ map:"+child.getFirstOccurrencesMap());
//                Substring.shouldRepr=true;
                for (Map.Entry<Document,SuffixTreeLeafNode> entry : child.getFirstOccurrencesMap().entrySet()) {
                    if (document.equals(entry.getKey())) {
                        result.add(entry.getKey(), entry.getValue().getSuffixNumber(entry.getKey()));
                        return result;
                    }
                    // System.out.println(entry.getKey().getIndex()+" ====== "+entry.getValue().getSuffixNumber(entry.getKey()));
                }
//                Substring.shouldRepr=false;
                return result;
            }
        }
        result.setMatchedLength(j);
        return result;
    }
    
    public Map<Document, String> findAll_2(String query, Map<Document, String> longestSubstringMap) {
//        System.out.println(query);
        // System.out.println("findAll:"+query);
        Result result = new Result(query.length());
        byte[] bytesQuery = query.getBytes();
        SuffixTreeNode child = root.getChild(bytesQuery[0]);
        int i = 0, j = 0;
        while(child != null) {
//            System.out.println("child = " + child);
//            System.out.println(query.substring(j));
            byte[] text = child.getSubstring().getText();
            for (
                i = child.getSubstring().getBegin();
                i <= child.getSubstring().getEnd() && j < bytesQuery.length;
                ++i, ++j) {
                if (text[i] != bytesQuery[j]) {
                    result.setMatchedLength(j);
//                    System.out.println((char) text[i] + " != " + (char) bytesQuery[j] + "::::"+query.substring(0,j));
                    List<SuffixTreeLeafNode> descendants = getDescendantLeafNodes(child);
                    for (SuffixTreeLeafNode descendant: descendants) {
                        for (Map.Entry<Document, Integer> entry : descendant.getSuffixNumberMap().entrySet()) {
                            String currString = longestSubstringMap.get(entry.getKey());
//                            System.out.println(entry.getKey().getIndex()+": "+query.substring(0,j) + " [VS] " + currString);
                            if (j > currString.length()) {
                                longestSubstringMap.put(entry.getKey(), query.substring(0,j));
                            }
//                            System.out.println(longestSubstringMap);
                            return longestSubstringMap;
                        }
                    }
                }
            }
            
            result.setMatchedLength(j);
            if (i > child.getSubstring().getEnd() && j < bytesQuery.length) {
                List<SuffixTreeLeafNode> descendants = getDescendantLeafNodes(child);
//                System.out.println(descendants);
                for (SuffixTreeLeafNode descendant: descendants) {
                    for (Map.Entry<Document, Integer> entry : descendant.getSuffixNumberMap().entrySet()) {
                        String currString = longestSubstringMap.get(entry.getKey());
//                        System.out.println(entry.getKey().getIndex()+": "+query.substring(0,j) + " [VS] " + currString);
                        if (j > currString.length()) {
                            longestSubstringMap.put(entry.getKey(), query.substring(0,j));
                        }
//                        System.out.println(longestSubstringMap);
                    }
                }
//                System.out.println("going to child: "+(char)bytesQuery[j]);
                child = child.getChild(bytesQuery[j]);
            } else if (i <= child.getSubstring().getEnd() && j >= bytesQuery.length) {
                List<SuffixTreeLeafNode> descendants = getDescendantLeafNodes(child);
//                System.out.println(descendants);
                for (SuffixTreeLeafNode descendant: descendants) {
                    for (Map.Entry<Document, Integer> entry : descendant.getSuffixNumberMap().entrySet()) {
                        String currString = longestSubstringMap.get(entry.getKey());
//                        System.out.println(entry.getKey().getIndex()+": "+query.substring(0,j) + " [VS] " + currString);
                        if (j > currString.length()) {
                            longestSubstringMap.put(entry.getKey(), query.substring(0,j));
                        }
//                        System.out.println(longestSubstringMap);
                    }
                }
//                System.out.println("END!");
//                System.out.println(longestSubstringMap);
                return longestSubstringMap;
            }
        }
//        System.out.println(longestSubstringMap);
        result.setMatchedLength(j);
        return longestSubstringMap;
    }
    
    public Result findFirstLongestSubstring(String query) throws IOException {
        Substring max = new Substring(0, 0, null);
        byte[] bytesQuery = query.getBytes();
        Substring.shouldRepr = true;

        InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader r = new BufferedReader(in);
        
        Map<Document, String> longestSubstringMap = new HashMap<Document, String>();
        for (Document d : this.documents) {
            longestSubstringMap.put(d, "");
        }
         for (int i = 0 ; i < query.length(); ++i) {
//        for (int i = 0 ; i < 2; ++i) {
            String suffix = query.substring(i, query.length());
            // find longest prefix that matches of substring
            longestSubstringMap = this.findAll_2(suffix, longestSubstringMap);
            // System.out.println(longestSubstringMap);
            // System.out.println(i+":"+result.size()+", "+result.getMatchedLength());
            // Substring matched = new Substring(i, i + result.getMatchedLength() - 1, bytesQuery);
            // System.out.println(result.getMatchedLength()+": matched: "+matched);
            // System.out.println(result.size());
            // System.out.println(result);
            // r.readLine();
            
            // if (matched.getLength() >= max.getLength()) {
            // max = matched;
            // } else {
            // break;
            // }
        }

         
         for (Document d : this.documents) {
             Result resultForDocument = findOne(longestSubstringMap.get(d), d);
             System.out.println(resultForDocument + "->" + longestSubstringMap.get(d));
         }
         
        // System.out.println("largest substring found in GST:" + max);
        // Substring.shouldRepr = false;
        // Result result = this.findAll(new String(Arrays.copyOfRange(max.getText(),
        // max.getBegin(), max.getEnd()+1)), true);
        // System.out.println(result);
        return null;
    }
}
