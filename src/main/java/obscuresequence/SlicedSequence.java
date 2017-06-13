package obscuresequence;

import java.math.BigInteger;
import java.util.Iterator;

/**
 * Slices up a sequence into multiple sections.
 *
 * Each time you request a new Iterator you will get a section of the full sequence
 * between one k-bit number and the next k-bit number it encounters.
 *
 * Note that these slices can be distributed and can run independently.
 *
 * How we select the k-bit start value is inspired by
 * https://math.stackexchange.com/questions/467212/generate-all-k-weight-n-bit-numbers-in-pseudo-random-sequence
 *
 * the maths behind it
 * https://en.wikipedia.org/wiki/Combinatorial_number_system#Finding_the_k-combination_for_a_given_number
 */
public class SlicedSequence implements Iterable<ObscureSequence> {
    // The required width of the sequence.
    private final int n;
    // How many bits are set in the number at the start and end of a slice.
    private final int k;
    // The LFSR used to select the next slice start.
    private final ObscureSequence slicer;

    public SlicedSequence(int n, int k) {
        this.n = n;
        this.k = k;
        // Build the LFSR to pseudo-randomly select the slices.
        // There will be nChoosek(n,k) slices.
        BigInteger limit = nChooseK(n, k);
        // And therefore I need an LFSR with that bit length.
        int bitLength = limit.bitLength();
        // Build me a big-enough lfsr.
        slicer = new GaloisLFSRSequence(bitLength);
    }

    @Override
    public Iterator<ObscureSequence> iterator() {
        return new Iterator<ObscureSequence>() {
            // The next sequence to issue.
            ObscureSequence next = null;

            @Override
            public boolean hasNext() {
                while (next == null && slicer.hasNext()) {
                    // Calculate next slice.
                    BigInteger next = slicer.next();
                    /*
                     * NB: As an LFSR never generates the value 0 the combinadic result will never pick
                     * the first lexicographically ordered slice.
                     *
                     * Subtracting 1 from the LFSR value pulls the first slice back in but may miss
                     * the last slice but since the combinadic is usually longer than the number of
                     * slices this is less likely to be a problem.
                     */
                    BigInteger combinadic = combinadic(n, k, next.subtract(BigInteger.ONE));

                    // Since the LFSR has been chosen to be too big it can generate too big numbers.
                    if (combinadic.bitCount() == k) {
                        // It's a good'n
                        GaloisLFSRSequence sequence = new GaloisLFSRSequence(n, combinadic, k);
                        // Make sure there is at least one value in the sequence.
                        if (sequence.hasNext()) {
                            // Use it.
                            this.next = sequence;
                            /*
                             * TODO - May be worth removing the first emission because it will be the seed
                             * and we can then discard trivial sequences that contain only one entry.
                             */
                        }
                    }
                }
                return next != null;
            }

            @Override
            public ObscureSequence next() {
                ObscureSequence next = hasNext() ? this.next : null;
                // Don't deliver that again.
                this.next = null;
                return next;
            }
        };
    }

    /**
     * Returns the combinadic of n, k and m
     *
     * Returns all bits set if m is too big.
     *
     * @param n - The number of bits
     * @param k - How many bits must be set
     * @param m - The position in the lexicographic sequence
     * @return the m'th (n, k)
     */
    private static BigInteger combinadic(int n, int k, BigInteger m) {
        BigInteger out = BigInteger.ZERO;
        for (; n > 0; n--) {
            BigInteger y = nChooseK(n - 1, k);
            if (m.compareTo(y) >= 0) {
                m = m.subtract(y);
                out = out.setBit(n - 1);
                k -= 1;
            }
        }
        return out;
    }

    /**
     * Calculates (n,k)
     *
     * Algorithm borrowed (and tweaked) from: http://stackoverflow.com/a/15302448/823393
     *
     * @param n - the bit width of the number.
     * @param k - How many bits must be set.
     * @return the value of (n,k)
     */
    private static BigInteger nChooseK(int n, int k) {
        if (k > n) {
            return BigInteger.ZERO;
        }
        if (k <= 0 || k == n) {
            return BigInteger.ONE;
        }
        // ( n * ( nChooseK(n-1,k-1) ) ) / k;
        return BigInteger.valueOf(n).multiply(nChooseK(n - 1, k - 1)).divide(BigInteger.valueOf(k));
    }

    // Playing around.
    public static void main(String[] args) {
        int n = 7;
        int k = 4;
        System.out.println("The sequence");
        GaloisLFSRSequence s = new GaloisLFSRSequence(n);
        while (s.hasNext()) {
            System.out.println(s.next().toString(2));
        }
        System.out.println("Sliced by " + k + " bits");
        int count = 1;
        for (ObscureSequence i : new SlicedSequence(n, k)) {
            boolean first = true;
            System.out.print("Sequence " + count + "\t");
            count += 1;
            while (i.hasNext()) {
                System.out.println((first ? "" : "\t") + i.next().toString(2));
                first = false;
            }
        }
    }

}
