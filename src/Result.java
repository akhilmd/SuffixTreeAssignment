public class Result {
    Document document = null;
    int index = -1;
    int queryLength = -1;

    private static final int PADDING = 10;

    public Result(int index, Document document, int queryLength) {
        this.index = index;
        this.document = document;
        this.queryLength = queryLength;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getQueryLength() {
        return queryLength;
    }

    public void setQueryLength(int queryLength) {
        this.queryLength = queryLength;
    }

    @Override
    public String toString() {
        String op = "Title: [" + document.getTitle() + "]    Text: [";
        
        int start = index - PADDING;
        int end = index + queryLength + PADDING;
        
        String prefix = "...";
        String suffix = "...";
        
        start = start < 0 ? 0 : start;
        end = end > document.getText().length() ? document.getText().length() : end;
        
        if (start == 0) {
            prefix = "";
        }

        if (end == document.getText().length()) {
            suffix = "";
        }

        op += prefix + document.getText().substring(start, end) + suffix + "]";

        return op;
    }
}
