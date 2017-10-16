public class Substring {
    private int begin = -1;
    private int end = -1;
    private byte[] text = null;
    public static boolean shouldRepr = false;

    public Substring(int begin, int end, byte[] text) {
        this.begin = begin;
        this.end = end;
        if (text == null) {
            text = new byte[1];
            text[0] = 0;
        }
        this.text = text;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getLength() {
        return 1 + this.end - this.begin;
    }

    @Override
    public String toString() {
        String op = "";
        byte ch = 0;
        for (int i = this.begin; i <= this.end; ++i) {
            ch = this.text[i];
            if (ch == 0) {
                ch = '$';
            }
            op += (char) ch;
        }
        String repr = "";
        if (shouldRepr) {
            repr = " = \"" + op + "\"";
        }
        return "[(" + this.begin + ", " + this.end + ")" + repr + "]";
    }

    public byte[] getText() {
        return text;
    }

    public void setText(byte[] text) {
        this.text = text;
    }

    public byte get(int ind) {
        return this.text[ind];
    }
}
