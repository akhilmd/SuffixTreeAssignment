public class Document {
    private String title;
    private String text;
    private int index = 0;

    public Document(String title, String text, int index) {
        this.title = title;
        this.text = text;
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        String op = "DOCUMENT++Title: [" + this.title + "], Text: [" + this.text.substring(0, 20 > this.text.length()? this.text.length() : 10) + "...]++";
        return op;
    }

    public String toString(int ind, int len) {
        int padding = 10;
        int before = (ind - padding) < 0 ? 0 : (ind - padding);
        int after = (ind + padding + len) > this.text.length() ? this.text.length() : (ind + padding + len); 
        String op = "Title: [" + this.title + "] \n Text: [" + this.text.substring(before, after)+ "]";
        return op;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        return this.index;
    }

    @Override
    public boolean equals(Object obj) {
        Document other = (Document) obj;
        return this.index == other.getIndex();
    }
}
