public class Document {
    private String title;
    private String text;

    public Document(String title, String text) {
        this.title = title;
        this.text = text;
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
        String op = "Title: [" + this.title + "], Text: [" + this.text.substring(0, 20 > this.text.length()? this.text.length() : 10) + "...]";
        return op;
    }

    public String toString(int ind, int len) {
        int padding = 10;
        int before = (ind - padding) < 0 ? 0 : (ind - padding);
        int after = (ind + padding + len) > this.text.length() ? this.text.length() : (ind + padding + len); 
        String op = "Title: [" + this.title + "] \n Text: [" + this.text.substring(before, after)+ "]";
        return op;
    }
    
    
}
