/*
 * Copyright 2017 OldCurmudgeon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package obscuresequence;

import java.math.BigInteger;
import java.util.Iterator;

/**
 * The word Obscure here denotes "difficult to predict" rather than "weird".
 */
public abstract class ObscureSequence extends Sequence {

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

    /**
     * Discard some.
     *
     * @param n - How many to discard.
     * @return the same sequence.
     */
    public ObscureSequence discard(Integer n) {
        for (int i = 0; i < n; i++) {
            next();
        }
        return this;
    }

    public ObscureSequence limit(int limit) {
        final ObscureSequence source = this;

        return new ObscureSequence() {
            int count = 0;

            @Override
            public boolean hasNext() {
                return source.hasNext() && count < limit;
            }

            @Override
            public BigInteger next() {
                if ( hasNext() ) {
                    count += 1;
                    return source.next();
                }
                return null;
            }
        };
    }

}
