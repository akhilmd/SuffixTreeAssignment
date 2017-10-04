public class SuffixTreeNode {
    protected int letterDepth = -1;
    protected Substring substring = null;
    protected SuffixTreeNode[] children = null;

    public SuffixTreeNode (int letterDepth, Substring substringRange) {
        this.letterDepth = letterDepth;
        this.substring = substringRange;

        // All will be null
        this.children = new SuffixTreeNode[128];
    }

    public int getLetterDepth() {
        return letterDepth;
    }

    public void setLetterDepth(int letterDepth) {
        this.letterDepth = letterDepth;
    }

    public SuffixTreeNode[] getChildren() {
        return children;
    }

    public void setChildren(SuffixTreeNode[] children) {
        this.children = children;
    }

    public Substring getSubstring() {
        return substring;
    }

    public void setSubstring(Substring substring) {
        this.substring = substring;
    }

    public void setChild(byte ind, SuffixTreeNode child) {
        this.children[ind] = child;
    }

    public SuffixTreeNode getChild(byte ind) {
        return this.children[ind];
    }

    @Override
    public String toString() {
        String op = "["+this.letterDepth+", "+this.substring+", {";
        String childrenStr = "";
        for (int i=0;i<this.children.length;++i) {
            if (this.children[i] != null) {
                if (i == 0) {
                    i = '$';
                }
                childrenStr += "'"+(char)i+"', ";
            }
        }
        childrenStr = childrenStr.trim();
        if (childrenStr.length() == 0) {
            childrenStr = "NO CHILDREN";
        }
        op = op.trim();
        op += childrenStr;
        if(op.endsWith(",")) {
            op = op.substring(0, op.length() - 1);
        }

        op += "}]";
        return op;
    }
}
