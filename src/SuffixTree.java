import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuffixTree {
    private SuffixTreeNode root = null;
    private List<Document> documents = null;
    private boolean isRelevanceSorting = false;

    public SuffixTree() {
        root = new SuffixTreeNode(0, new Substring(0, 0, null));
        this.documents = new ArrayList<Document>();
    }

    public void add(Document document) {
        String pre = document.getText() + "\0";
        byte[] text = pre.getBytes();

        for (int i = 0; i < text.length; ++i)
            this.insert(root, new Substring(i, text.length - 1, text), i, document);

        documents.add(document);
    }

    private SuffixTreeLeafNode insert(SuffixTreeNode node, Substring substring, int suffixNumber, Document document) {
        int b = substring.getBegin();
        SuffixTreeNode child = node.getChild(substring.get(b));
        if (child == null) {
            SuffixTreeLeafNode newChild = new SuffixTreeLeafNode(substring.getLength() + node.getLetterDepth(),
                    substring);
            newChild.addDocument(document, suffixNumber);
            node.addFirstOccurrence(document, newChild);
            newChild.addFirstOccurrence(document, newChild);
            node.setChild(substring.get(b), newChild);
            return newChild;
        }

        // move down edge based on child's substring range
        Substring childSubstring = child.getSubstring();

        int i = 0, j = 0, c = 0;
        for (i = b, j = childSubstring.getBegin(), c = 1; i <= substring.getEnd()
                && j <= childSubstring.getEnd(); ++i, ++j, ++c) {
            if (substring.get(i) != childSubstring.get(j)) {
                // mismatch in the middle
                SuffixTreeNode newInternalNode = new SuffixTreeNode(node.getLetterDepth() + c - 1,
                        new Substring(b, i - 1, substring.getText()));

                childSubstring.setBegin(j);
                child.setSubstring(childSubstring);
                newInternalNode.setChild(childSubstring.get(j), child);

                SuffixTreeLeafNode newLeafNode = new SuffixTreeLeafNode(substring.getLength(),
                        new Substring(i, substring.getEnd(), substring.getText()));
                newLeafNode.addDocument(document, suffixNumber);
                newLeafNode.addFirstOccurrence(document, newLeafNode);
                for (Map.Entry<Document, SuffixTreeLeafNode> entry : child.getFirstOccurrencesMap().entrySet()) {
                    newInternalNode.addFirstOccurrence(entry.getKey(), entry.getValue());
                }
                newInternalNode.addFirstOccurrence(document, newLeafNode);

                newInternalNode.setChild(substring.get(i), newLeafNode);
                node.setChild(substring.get(b), newInternalNode);
                return newLeafNode;
            }
        }

        if (i <= substring.getEnd() && j > childSubstring.getEnd()) {
            SuffixTreeLeafNode newInsertedDdescendant = insert(child,
                    new Substring(i, substring.getEnd(), substring.getText()), suffixNumber, document);
            child.addFirstOccurrence(document, newInsertedDdescendant);
            return newInsertedDdescendant;
        }

        if (i == 1 + substring.getEnd() && j == 1 + childSubstring.getEnd()) {
            ((SuffixTreeLeafNode) child).addDocument(document, suffixNumber);
            node.addFirstOccurrence(document, (SuffixTreeLeafNode) child);
            ((SuffixTreeLeafNode) child).addFirstOccurrence(document, (SuffixTreeLeafNode) child);
            return (SuffixTreeLeafNode) child;
        }
        return null;
    }

    private List<SuffixTreeLeafNode> getDescendantLeafNodes(SuffixTreeNode node) {
        if (node == null)
            return new ArrayList<SuffixTreeLeafNode>();

        List<SuffixTreeLeafNode> descendants = node.getDescendants();
        if (descendants != null) {
            return descendants;
        }

        descendants = new ArrayList<SuffixTreeLeafNode>();

        if (node instanceof SuffixTreeLeafNode) {
            descendants.add((SuffixTreeLeafNode) node);
        }

        for (int i = 0; i < node.getChildren().length; ++i) {
            List<SuffixTreeLeafNode> childDescendants = getDescendantLeafNodes(node.getChild((byte) i));
            descendants.addAll(childDescendants);
        }
        node.setDescendants(descendants);
        return descendants;
    }

    private Result findAllInDocument(String query, Document document) {
        Result result = new Result(query.length());
        byte[] bytesQuery = query.getBytes();
        SuffixTreeNode child = root.getChild(bytesQuery[0]);
        int i = 0, j = 0;
        while (child != null) {
            byte[] text = child.getSubstring().getText();
            for (i = child.getSubstring().getBegin(); i <= child.getSubstring().getEnd()
                    && j < bytesQuery.length; ++i, ++j) {
                if (text[i] != bytesQuery[j]) {
                    return result;
                }
            }
            if (i > child.getSubstring().getEnd() && j < bytesQuery.length) {
                child = child.getChild(bytesQuery[j]);
            } else if (i <= child.getSubstring().getEnd() && j >= bytesQuery.length) {
                List<SuffixTreeLeafNode> descendants = getDescendantLeafNodes(child);
                Integer ind = null;
                if (this.isRelevanceSorting) {
                    BitSet docba = new BitSet(document.getText().length());
                    for (SuffixTreeLeafNode descendant : descendants) {
                        ind = descendant.getSuffixNumber(document);
                        if (ind == null)
                            continue;
                        for (i = ind; i < ind + bytesQuery.length; ++i) {
                            docba.set(i);
                        }
                    }
                    result.setPercentageMatched((double) docba.cardinality() / document.getText().length() * 100.0);
                    result.setNumMatched(descendants.size());
                }

                for (SuffixTreeLeafNode descendant : descendants) {
                    for (Map.Entry<Document, Integer> entry : descendant.getSuffixNumberMap().entrySet()) {
                        result.add(entry.getKey(), entry.getValue());
                    }
                }
                return result;
            }
        }
        return result;
    }

    public Result findAll(String query) {
        return findAllInDocument(query, null);
    }

    private Map<Document, Tuple<Integer, Integer, String>> updateLongestSubstringMap(
            List<SuffixTreeLeafNode> descendants, Map<Document, Tuple<Integer, Integer, String>> longestSubstringMap,
            int j, SuffixTreeNode child, String substring) {
        Document document = null;
        Tuple<Integer, Integer, String> t = null, currT = null;
        for (SuffixTreeLeafNode descendant : descendants) {
            for (Map.Entry<Document, Integer> entry : descendant.getSuffixNumberMap().entrySet()) {
                currT = longestSubstringMap.get(entry.getKey());
                if (j > currT.getElement0()) {
                    document = entry.getKey();
                    t = new Tuple<Integer, Integer, String>(j,
                            child.getFirstOccurrencesMap().get(document).getSuffixNumber(document), substring);
                    longestSubstringMap.put(document, t);
                }
            }
        }
        return longestSubstringMap;
    }

    private Map<Document, Tuple<Integer, Integer, String>> findAllLongestSubs(String query,
            Map<Document, Tuple<Integer, Integer, String>> longestSubstringMap) {
        byte[] bytesQuery = query.getBytes();
        SuffixTreeNode child = root.getChild(bytesQuery[0]);
        int i = 0, matchesInQuery = 0, queryLength = query.length();
        List<SuffixTreeLeafNode> descendants = null;
        while (child != null) {
            descendants = getDescendantLeafNodes(child);
            byte[] text = child.getSubstring().getText();
            for (i = child.getSubstring().getBegin(); i <= child.getSubstring().getEnd()
                    && matchesInQuery < queryLength; ++i, ++matchesInQuery) {
                if (text[i] != bytesQuery[matchesInQuery]) {
                    return updateLongestSubstringMap(descendants, longestSubstringMap, matchesInQuery, child,
                            query.substring(0, matchesInQuery));
                }
            }
            longestSubstringMap = updateLongestSubstringMap(descendants, longestSubstringMap, matchesInQuery, child,
                    query.substring(0, matchesInQuery));
            if (i > child.getSubstring().getEnd() && matchesInQuery < queryLength) {
                child = child.getChild(bytesQuery[matchesInQuery]);
            } else if (i <= child.getSubstring().getEnd() && matchesInQuery >= queryLength) {
                return longestSubstringMap;
            }
        }

        return longestSubstringMap;
    }

    public Result findFirstLongestSubstring(String query) {
        Result result = new Result(query.length());
        Tuple<Integer, Integer, String> t = null;

        Map<Document, Tuple<Integer, Integer, String>> longestSubstringMap = new HashMap<Document, Tuple<Integer, Integer, String>>();
        for (Document d : this.documents) {
            longestSubstringMap.put(d, new Tuple<Integer, Integer, String>(0, 0, null));
        }
        for (int i = 0; i < query.length(); ++i) {
            String suffix = query.substring(i, query.length());
            // find longest prefix that matches of substring
            longestSubstringMap = this.findAllLongestSubs(suffix, longestSubstringMap);
        }

        if (this.isRelevanceSorting) {
            result.setLongestSubstringMap(longestSubstringMap);
            return result;
        }

        for (Document d : this.documents) {
            t = longestSubstringMap.get(d);
            if (t.getElement0() == 0)
                continue;

            result.add(d, t.getElement1(), "Longest Substring: [" + t.getElement2() + "]    ");
        }
        return result;
    }

    public Result findAllRelevant(String query, int resultLen) {
        this.isRelevanceSorting = true;

        Result result = findFirstLongestSubstring(query);
        Tuple<Integer, Integer, String> t = null;

        Map<Document, Tuple<Integer, Integer, String>> longestSubstringMap = result.getLongestSubstringMap();

        Map<Document, String> lengthQuerySubstringMatched = new HashMap<Document, String>();
        Map<Document, Double> percentageQuerySubstringMatched = new HashMap<Document, Double>();
        Map<Document, Integer> numQuerySubstringMatched = new HashMap<Document, Integer>();

        for (Document d : this.documents) {
            t = longestSubstringMap.get(d);
            if (t.getElement0() == 0) {
                lengthQuerySubstringMatched.put(d, "");
                percentageQuerySubstringMatched.put(d, 0.0);
                numQuerySubstringMatched.put(d, 0);
                continue;
            }

            lengthQuerySubstringMatched.put(d, t.getElement2());

            result = findAllInDocument(t.getElement2(), d);
            percentageQuerySubstringMatched.put(d, result.getPercentageMatched());
            numQuerySubstringMatched.put(d, result.getNumMatched());
        }
        result = null;

        List<Document> opDocs = new ArrayList<>(this.documents);
        Collections.sort(opDocs, new Comparator<Document>() {
            @Override
            public int compare(Document d1, Document d2) {
                int matchedD1 = lengthQuerySubstringMatched.get(d1).length();
                int matchedD2 = lengthQuerySubstringMatched.get(d2).length();
                if (matchedD1 > matchedD2) {
                    return -1;
                } else if (matchedD1 < matchedD2) {
                    return 1;
                }

                double percD1 = Math.round(percentageQuerySubstringMatched.get(d1) * 100.0) / 100.0;
                double percD2 = Math.round(percentageQuerySubstringMatched.get(d2) * 100.0) / 100.0;
                if (percD1 > percD2) {
                    return -1;
                } else if (percD1 < percD2) {
                    return 1;
                }

                matchedD1 = numQuerySubstringMatched.get(d1);
                matchedD2 = numQuerySubstringMatched.get(d2);
                if (matchedD1 > matchedD2) {
                    return -1;
                } else if (matchedD1 < matchedD2) {
                    return 1;
                }
                return 0;
            }
        });

        if (opDocs.size() > resultLen)
            opDocs = opDocs.subList(0, resultLen);

        result = new Result(query.length());

        for (Document d : opDocs) {
            result.add(d, 0,
                    "[" + lengthQuerySubstringMatched.get(d) + ", "
                            + String.format("%.2f", percentageQuerySubstringMatched.get(d)) + "%, "
                            + numQuerySubstringMatched.get(d) + "]    ");
        }

        this.isRelevanceSorting = false;
        return result;
    }
}
