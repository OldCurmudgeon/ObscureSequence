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
class GaloisLFSRTest extends Specification {
    // Sequence expected from a 3-bit LFSR using the default taps.
    @Shared
            correct3BitLFSRSequence = [1, 6, 3, 7, 5, 4, 2]

    def "Test a simple 3-bit tap"() {
        given: "the default 3-bit tap"
        def lfsr = new GaloisLFSRSequence(3)

        when: "enumerated"
        def seq = lfsr.toList()

        then: "known expected values"
        seq.equals(correct3BitLFSRSequence)
    }

    def "Test a simple 3-bit tap created by the factory"() {
        given: "the default 3-bit tap"
        def lfsr = ObscureSequence.create(ObscureSequence.Type.GaloisLFSR, 3)

        when: "enumerated"
        def seq = lfsr.toList()

        then: "known expected values"
        seq.equals(correct3BitLFSRSequence)
    }

    def "Test the obscureBit() method"() {
        expect:
        new GaloisLFSRSequence(3).obscureBit(b).toList().equals(r)

        where:
        b || r
        // correct3BitLFSRSequence seq with that bit removed.
        0 || [0, 1, 3, 2]
        1 || [2, 1, 3, 0]
        2 || [2, 3, 1, 0]

    }


}
