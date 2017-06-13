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

package obscuresequence

import spock.lang.Shared
import spock.lang.Specification

/**
 * Test the GaloisLFSRSequence
 */
class ObscureSequenceTest extends Specification {
    // Sequence expected from a 3-bit LFSR using the default taps.
    @Shared
    List<BigInteger> correct3BitLFSRSequence = [1, 6, 3, 7, 5, 4, 2]

    def "Test a simple 3-bit tap"() {
        given: "the default 3-bit tap"
        def lfsr = new GaloisLFSRSequence(3)

        when: "enumerated"
        def seq = lfsr.toList()

        then: "known expected values"
        seq == correct3BitLFSRSequence
    }

    def "Test the obscureBit() method"() {
        expect:
        new GaloisLFSRSequence(3).obscureBit(b).toList() == r

        where:
        b || r
        // correct3BitLFSRSequence seq with that bit removed.
        0 || [0, 1, 3, 2]
        1 || [2, 1, 3, 0]
        2 || [2, 3, 1, 0]

    }

    def "Test the stagger() method"() {
        expect:
        new GaloisLFSRSequence(3).stagger(10).toList() == [1, 7, 6, 3, 5, 4, 2]

    }

    def "Test a SlicedSequence"() {
        expect:
        new SlicedSequence(7, 6).iterator().next().toList() == [63, 127]
    }

    def "Test a big sequence"() {
        given: "a wide sequence hacked about and partially consumed"
        def lfsr = new GaloisLFSRSequence(1024)
                .obscureBit(0)
                .obscureBit(4)
                .stagger(10)
                .discard(1000)
                .limit(10)

        expect:
        ++lfsr == 617440762262617418585766818484820182577022890014279742717383810444882676814328808944769111050137324869633402418590349700869216854833244323110477477213712577353067678261559775466667758063076190042598947291844664946973037982406098018575391433553059591944758169277528651358043825561461750911952991500079079410
    }
}
