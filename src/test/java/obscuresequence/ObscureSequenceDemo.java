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
        for (Demo demo : Demo.values()) {
            demo.run();
        }
    }

    enum Demo implements Runnable {
        DemoSimpleSequence {
            @Override
            public void run() {
                printSequence(new GaloisLFSRSequence(3),
                        "A simple 3-bit sequence with default taps");

                printSequence(new GaloisLFSRSequence(3, 1),
                        "A simple 3-bit sequence with different taps");
            }
        },
        DemoObscureBit {
            @Override
            public void run() {
                printSequence(new GaloisLFSRSequence(4),
                        "The base 4-bit sequence");

                printSequence(new GaloisLFSRSequence(4).obscureBit(0),
                        "A 3-bit sequence generated from the 4-bit sequence with even numbers discarded and the lowest bit removed");
            }
        },
        DemoSlicedSequence {
            // The width of the sequences.
            final int n = 7;
            // Start/stop at positions in the sequence that have just 6 bits set.
            final int k = 6;

            @Override
            public void run() {
                printSequence(new GaloisLFSRSequence(n),
                        "The base 7-bit sequence");
                int count = 1;
                // Slice it up. Note that a SlicedSequence is an Iterable<ObscureSequence>.
                for (ObscureSequence slice : new SlicedSequence(n, k)) {
                    printSequence(slice,
                            "Slice " + (count++));
                }
            }
        },
        DemoStaggeredSequence {
            @Override
            public void run() {
                printSequence(new PlainSequence(4).stagger(867),
                        "Plain sequence staggered by [5,4,3,2,1,0]");
            }
        },
        Demo1024BitSequence {
            @Override
            public void run() {
                ObscureSequence s = new GaloisLFSRSequence(1024).obscureBit(0).stagger(10);
                // Consume some
                for (int i = 0; i < Integer.MAX_VALUE / 16384; i++) {
                    s.next();
                }
                printSequence(s, "1024 bit sequence with bit(0) obscured and staggered", 16, 10, "\n");
            }

        }
    }

    private static void printSequence(ObscureSequence sequence, String description) {
        printSequence(sequence, description, 10, 0, " ");
    }

    private static void printSequence(ObscureSequence sequence, String description, int radix, int limit, String between) {
        System.out.println(description + ":");
        int count = 0;
        while (sequence.hasNext() && (limit == 0 || count++ < limit)) {
            System.out.print(sequence.next().toString(radix) + between);
        }
        System.out.println();
    }

}
