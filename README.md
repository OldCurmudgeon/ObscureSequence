# ObscureSequence

In this context an obscure<sup>[&dagger;](#obscure)</sup> sequence is a sequence of numbers that is difficult to predict and never repeats.

## Usage

I believe there are many uses of sequences such as this. Here is just a sample.

#### Credit card PANs

This package was inspired by my work with credit cards and the realisation that the fundamental problem with credit card security is that the PAN is the same for every transaction. See [why I think we could use a 95-bit 19-digit base32 unique number sequence](http://uniquenumbers95.appspot.com/) for a rant on the issue and what it could achieve.

> Here's what you get.
  
> You get a unique sequence of numbers - and I mean **unique**! Not only will no one else ever get the same sequence but **no sequence will ever generate a number that appears in any other sequence - ever**. By my calculation, for a `95-bit` sequence, the method I use produces about `20 quadrillion` (that's about **2 * 10<sup>16</sup>**) **sequences** with a total count of generated numbers of around `40 octillion` (that's about **40 * 10<sup>28</sup>**) . With the world population currently standing at about `7 billion` that means that every person on the planet could take `2 million` sequences and still never repeat a single number. Each sequence will be about `2 trillion` (that's about **2 * 10<sup>12</sup>**)numbers long. If you consume one number every millisecond it would take you over `60` years to reach the end of **one** sequence.

#### Other uses

I believe this mechanism could easily be used as part of a one-time-pad encryption solution applicable in many fields. 

It could provide an ID system that would make it quite difficult to impersonate another person.

## How it works

The core sequence is generated by a [Linear-feedback shift register (LFSR)](https://en.wikipedia.org/wiki/Linear-feedback_shift_register) which generates a [Maximum length sequence (MLS)](https://en.wikipedia.org/wiki/Maximum_length_sequence). This provides the *never repeats* characteristic of the sequences.

```java
/**
 * Some demonstrations of obscure sequences.
 */
public class ObscureSequenceDemo {
    public static void main(String[] args) {
        new DemoSimpleSequence().run();
    }

    static class DemoSimpleSequence {

        void run() {
            printSequence(new GaloisLFSRSequence(3),
                    "A simple 3-bit sequence with default taps");

            printSequence(new GaloisLFSRSequence(3, 1),
                    "A simple 3-bit sequence with different taps");
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

```

```
A simple 3-bit sequence with default taps:
1 6 3 7 5 4 2 
A simple 3-bit sequence with different taps:
1 5 7 6 3 4 2 
```

### Adding obscurity

A base sequence can be manipulated by a number of techniques that preserve that uniqueness while adding obscurity. 

Most of the obscuring tools can be stacked to produce quite complex sequences, for example, it is perfectly reasonable to add multiple staggers to a sequence.

---
#### Obscure Bit

This allows you to split an MLS of `n` bits into `n` MLS's of width `n-1`.
  
If you take an MLS, pick one bit and perform the following changes to it:

1. If the specified bit is a `0`, discard the value.
2. If the specified bit is a `1`, remove that bit from the value.

It is provable that this generates an `n-1` width MLS.
 
This trick was inspired by the realisation that with a normal LFSR, any even number is always followed by the same number shifted right by one bit. This predictability can be removed using the `ObscureSequence.obscureBit(0)` method. Obviously any bit can be used to add obscurity in this way.

```java
    static class DemoObscureBit {

        void run() {
            printSequence(new GaloisLFSRSequence(4),
                    "The base 4-bit sequence");

            printSequence(new GaloisLFSRSequence(4).obscureBit(0),
                    "A 3-bit sequence generated from the 4-bit sequence with even numbers discarded and the lowest bit removed");
        }
    }
```

```
The base 4-bit sequence:
1 12 6 3 13 10 5 14 7 15 11 9 8 4 2 
A 3-bit sequence generated from the 4-bit sequence with even numbers discarded and the lowest bit removed:
0 1 6 2 3 7 5 4 
```

Interestingly, an LFSR will never generate the value `0` but passing it through the `obscureBit` filter can produce a `0` in the sequence.

---
#### Slicing


This method slices the sequence into runs. This allows one sequence to be partitioned into sub-sequences that can then run independently, even remote from each other with no contact between them, without any slice repeating a value from **any other slice**.

An LFSR can be started at any point in the sequence. This technique slices the sequence up into sub-sequences that start with a value having only `k` bits set and stops when it encounters the next value with only `k` bits set.

There is some useful combinatorics that allows us to find the n<sup>th</sup> number with `k` bits set (call them `k-bit` numbers). To further obscure the sequences it also makes that choice using another internal LFSR.

```java
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
                        "Slice "+(count++));
            }
        }
    }

```

```
The base 7-bit sequence:
1 96 48 24 12 6 3 97 80 40 20 10 5 98 49 120 60 30 15 103 83 73 68 34 17 104 52 26 13 102 51 121 92 46 23 107 85 74 37 114 57 124 62 31 111 87 75 69 66 33 112 56 28 14 7 99 81 72 36 18 9 100 50 25 108 54 27 109 86 43 117 90 45 118 59 125 94 47 119 91 77 70 35 113 88 44 22 11 101 82 41 116 58 29 110 55 123 93 78 39 115 89 76 38 19 105 84 42 21 106 53 122 61 126 63 127 95 79 71 67 65 64 32 16 8 4 2 
Slice 1:
63 127 
Slice 2:
125 94 47 
Slice 3:
111 87 75 69 66 33 112 56 28 14 7 99 81 72 36 18 9 100 50 25 108 54 27 109 86 43 117 90 45 118 59 
Slice 4:
126 
Slice 5:
123 93 78 39 115 89 76 38 19 105 84 42 21 106 53 122 61 
Slice 6:
119 91 77 70 35 113 88 44 22 11 101 82 41 116 58 29 110 55 
Slice 7:
95 79 71 67 65 64 32 16 8 4 2 1 96 48 24 12 6 3 97 80 40 20 10 5 98 49 120 60 30 15 103 83 73 68 34 17 104 52 26 13 102 51 121 92 46 23 107 85 74 37 114 57 124 62 31 
```

Obviously the lengths of these sequences vary but with wider sequences this should not be a significant issue.

---
#### Staggering

This technique reorders the output of the sequence using permutations.

```java
    static class DemoStaggeredSequence {

        void run() {
            printSequence(new PlainSequence(4).stagger(867),
                    "Plain sequence staggered by [5,4,3,2,1,0]");
        }
    }
```

```
Plain sequence staggered by [5,4,3,2,1,0]:
6 5 4 3 2 1 12 11 10 9 8 7 13 14 15 16 
```

---
<a name="obscure"><sup>&dagger;</sup></a> Here I mean *obscure* as in *difficult to discern* rather than *weird*.
