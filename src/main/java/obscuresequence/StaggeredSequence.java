package obscuresequence;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Staggers the sequence in a predictable way.
 */
public class StaggeredSequence extends ObscureSequence {
    // The permutation to use.
    final Permutation<BigInteger> p;

    /**
     * Staggers the source sequence using the specified permutation.
     *
     * The specified permutation just picks a permutation behing the scenes.
     *
     * @param source      - The sequence to permute.
     * @param permutation - Which permutation to use.
     */
    public StaggeredSequence(ObscureSequence source, int permutation) {
        // Make my permutation.
        p = new Permutation<>(source, permutation);
    }

    @Override
    public boolean hasNext() {
        return p.hasNext();
    }

    @Override
    public BigInteger next() {
        return p.next();
    }

    /**
     * There's probably a better way to do this but this seems to work.
     *
     * A permutation of k entries is chosen and applied to each k entries in the sequence.
     *
     * The last permutation may not be applied if the sequence does not end exactly on a permutation boundary.
     *
     * See: https://www.quora.com/How-would-you-explain-an-algorithm-that-generates-permutations-using-lexicographic-ordering
     */
    private static class Permutation<T> implements Iterator<T> {
        // The permute offsets (0 based).
        final ArrayList<Integer> p = new ArrayList<>();
        // The source I need to permute.
        final Iterator<T> source;
        // My temp buffer.
        final ArrayList<T> buffer;
        // Where I am in that buffer.
        int pos = 0;
        // The next one to deliver.
        T next = null;

        @Override
        public boolean hasNext() {
            while (next == null && (source.hasNext() || pos < buffer.size())) {
                if (pos >= buffer.size()) {
                    // Clear the buffer.
                    buffer.clear();
                    pos = 0;
                    // Fill it from source.
                    for (int i = 0; i < p.size() && source.hasNext(); i++) {
                        buffer.add(source.next());
                    }
                    if (buffer.size() != p.size()) {
                        // Ending! No permutation on the last one.
                        // Flatten p.
                        for (int i = 0; i < p.size(); i++) {
                            p.set(i, i);
                        }
                    }
                }
                if (pos < buffer.size()) {
                    next = buffer.get(p.get(pos++));
                }
            }
            return next != null;
        }

        @Override
        public T next() {
            // Give them that one
            T next = hasNext() ? this.next : null;
            // but only once.
            this.next = null;
            return next;
        }

        Permutation(Iterator<T> source, int permutation) {
            // Note my source.
            this.source = source;

            // Start the p containing just [1] - i.e. leave it alone.
            p.add(0);
            for (int i = 0; i < permutation; i++) {
                // Find the largest x such that P[x]<P[x+1].
                int x;
                for (x = p.size() - 2; x >= 0 && p.get(x).compareTo(p.get(x + 1)) > 0; x--) ;
                if (x >= 0) {
                    // Find the largest y such that P[x]<P[y].
                    int maxY = x;
                    for (int y = x + 1; y < p.size(); y++) {
                        if (p.get(x).compareTo(p.get(y)) < 0) {
                            maxY = y;
                        }
                    }
                    // Swap P[x] and P[y].
                    Integer temp = p.get(x);
                    p.set(x, p.get(maxY));
                    p.set(maxY, temp);
                    // Reverse P[x+1 .. n]
                    Collections.reverse(p.subList(x + 1, p.size()));
                } else {
                    // If there is no such x, P is the last permutation.
                    // Set them all back in sequence.
                    for (int y = 0; y < p.size(); y++) {
                        p.set(y, y);
                    }
                    // Increase the length of the array.
                    p.add(p.size());
                    // And try again.
                    i -= 1;
                }
            }
            // Allocate my buffer.
            buffer = new ArrayList<>(p.size());
        }

        @Override
        public String toString() {
            return p.toString();
        }

    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            Permutation<BigInteger> p = new Permutation<>(new PlainSequence(4), i);
            System.out.print(i + " = " + p + " -> ");
            while (p.hasNext()) {
                System.out.print(p.next() + " ");
            }
            System.out.println();
        }
    }
}
