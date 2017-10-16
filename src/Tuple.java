public class Tuple<E0, E1, E2> {
    private E0 element0 = null;
    private E1 element1 = null;
    private E2 element2 = null;

    public Tuple(E0 element0, E1 element1, E2 element2) {
        this.element0 = element0;
        this.element1 = element1;
        this.element2 = element2;
    }

    public E0 getElement0() {
        return element0;
    }

    public void setElement0(E0 element0) {
        this.element0 = element0;
    }

    public E1 getElement1() {
        return element1;
    }

    public void setElement1(E1 element1) {
        this.element1 = element1;
    }

    public E2 getElement2() {
        return element2;
    }

    public void setElement2(E2 element2) {
        this.element2 = element2;
    }
}
