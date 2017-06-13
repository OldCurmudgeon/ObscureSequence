package obscuresequence;

import java.math.BigInteger;

/**
 * The word Obscure here denotes "difficult to predict" rather than "weird".
 */
public abstract class ObscureSequence extends Sequence {

    /**
     * TODO: This is probably redundant.
     */
    enum Type {
        GaloisLFSR {
            ObscureSequence create(int bits) {
                return new GaloisLFSRSequence(bits);
            }
        };

        abstract ObscureSequence create(int bits);
    }

    /**
     * Create a sequence of the specified type.
     *
     * @param type - One of the Types enum.
     * @param bits - How many bits wide.
     * @return the specified sequence.
     */
    static ObscureSequence create(Type type, int bits) {
        return type.create(bits);
    }

    /**
     * Obscure one bit by:
     *
     * 1. Discard all values with the specified bit clear.
     * 2. Remove that bit from the results.
     *
     * Inspired by the fact that for a normal LFSR, any even result predicts the next
     * result because it is just the same value shifted right one.
     *
     * These can be removed by discarding all even results and removing the lowest bit
     * (now left at 1). This scheme can be extended to any bit.
     *
     * NB: The resulting sequence is one bit narrower than the original.
     *
     * @param bit - The bit to remove (0 -> lowest).
     * @return the new sequence.
     */
    public ObscureSequence obscureBit(int bit) {
        final ObscureSequence source = this;

        return new ObscureSequence() {
            // Mask to select bits to keep (those to the right of the one to remove).
            private final BigInteger mask = BigInteger.ONE.shiftLeft(bit).subtract(BigInteger.ONE);
            // The next one to deliver.
            private BigInteger next = null;

            @Override
            public boolean hasNext() {
                // Keep looking 'till we find one or the source runs out.
                while (next == null && source.hasNext()) {
                    BigInteger candidate = source.next();
                    // That bit must be 1
                    if (candidate.testBit(bit)) {
                        // Retain the bits behind the mask.
                        BigInteger masked = candidate.and(mask);
                        // Remove that bit.
                        next = candidate
                                // Clear the bit we want to remove
                                .clearBit(bit)
                                // Mask out the lower bits
                                .xor(masked)
                                // Shift it right one.
                                .shiftRight(1)
                                // Recover the masked bits.
                                .or(masked);
                    }
                }
                return next != null;
            }

            @Override
            public BigInteger next() {
                BigInteger next = hasNext() ? this.next : null;
                // Don't deliver that one again.
                this.next = null;
                return next;
            }
        };
    }

    /**
     * Shortcut to add stagger.
     *
     * @param permutation - The permutation to use.
     * @return my sequence staggered.
     */
    public ObscureSequence stagger(int permutation) {
        return new StaggeredSequence(this, permutation);
    }

}
