package example.ch1;

import net.jcip.annotations.*;

@NotThreadSafe
public class UnsafeSequence {
    private int value;

    public int getNext() {
        return value++;
    }

    public static void main() throws InterruptedException {
        UnsafeSequence sequence = new UnsafeSequence();
        int iterations = 100_000;
        int expected = iterations * 2;

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < iterations; i++) {
                sequence.getNext();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < iterations; i++) {
                sequence.getNext();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Expected value " + expected);
        System.out.println("Final value: " + sequence.value);
    }
}
