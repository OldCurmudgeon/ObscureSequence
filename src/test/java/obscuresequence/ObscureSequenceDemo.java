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
            printSequence(new StaggeredSequence(new PlainSequence(4), 867),
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
