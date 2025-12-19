import java.io.Serializable;

public class Pair<A, B> implements Serializable {
    private static final long serialVersionUID = 11L;
    public final A first;
    public final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public int getValue(){
        return (int) second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}