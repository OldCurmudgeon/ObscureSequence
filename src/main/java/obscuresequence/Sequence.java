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
 * Some base methods that all sequences exhibit.
 */
public abstract class Sequence implements Iterator<BigInteger> {

    /**
     * You cannot remove elements from a sequence.
     */
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove elements from a sequence.");
    }
}
