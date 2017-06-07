package obscuresequence;

import java.math.BigInteger;

/**
 * Completely unobscured sequence - 1 ...
 *
 * Used for testing - probably of no other use.
 */
public class PlainSequence extends ObscureSequence {
    private final BigInteger stop;
    BigInteger next = BigInteger.ONE;

    public PlainSequence(int bitLength) {
        stop = BigInteger.ONE.shiftLeft(bitLength);
    }

    @Override
    public boolean hasNext() {
        return next.compareTo(stop) <= 0;
    }

    @Override
    public BigInteger next() {
        BigInteger next =  this.next;
        this.next = this.next.add(BigInteger.ONE);
        return next;
    }
}
