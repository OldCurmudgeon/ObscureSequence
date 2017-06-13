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

/**
 * Some demonstrations of obscure sequences.
 */
public class ObscureSequenceDemo {
    public static void main(String[] args) {
        new DemoSimpleSequence().run();
        new DemoObscureBit().run();
        new DemoSlicedSequence().run();
        new DemoStaggeredSequence().run();
    }

    static class DemoSimpleSequence {

        void run() {
            printSequence(new GaloisLFSRSequence(3),
                    "A simple 3-bit sequence with default taps");

            printSequence(new GaloisLFSRSequence(3, 1),
                    "A simple 3-bit sequence with different taps");
        }
    }

    static class DemoObscureBit {

        void run() {
            printSequence(new GaloisLFSRSequence(4),
                    "The base 4-bit sequence");

            printSequence(new GaloisLFSRSequence(4).obscureBit(0),
                    "A 3-bit sequence generated from the 4-bit sequence with even numbers discarded and the lowest bit removed");
        }
    }

    static class DemoSlicedSequence {
        // The width of the sequences.
        final int n = 7;
        // Start/stop at positions in the sequence that have just 6 bits set.
        final int k = 6;

        void run() {
            printSequence(new GaloisLFSRSequence(n),
                    "The base 7-bit sequence");
            int count = 1;
            // Slice it up. Note that a SlicedSequence is an Iterable<ObscureSequence>.
            for (ObscureSequence slice : new SlicedSequence(n, k)) {
                printSequence(slice,
                        "Slice " + (count++));
            }
        }
    }

    static class DemoStaggeredSequence {

        void run() {
            printSequence(new PlainSequence(4).stagger(867),
                    "Plain sequence staggered by [5,4,3,2,1,0]");
        }
    }


    private static void printSequence(ObscureSequence sequence, String description) {
        System.out.println(description + ":");
        while (sequence.hasNext()) {
            System.out.print(sequence.next() + " ");
        }
        System.out.println();
    }

}
