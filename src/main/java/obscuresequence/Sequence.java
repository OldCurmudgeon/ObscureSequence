package obscuresequence;

import java.math.BigInteger;
import java.util.Iterator;

/**
 * Some base methods that all sequences exhibit.
 */
public abstract class Sequence implements Iterator<BigInteger>{

    /**
     * You cannot remove elements from a sequence.
     */
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove elements from a sequence.");
    }
}
