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

/**
 * Completely unobscured sequence - 1, 2, 3, ...
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
        BigInteger next = this.next;
        this.next = this.next.add(BigInteger.ONE);
        return next;
    }
}
