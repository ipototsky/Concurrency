package example.ch2.ch2_7;

import java.util.concurrent.locks.StampedLock;

public class PosixWidgetSimulation {
    // Non-Reentrant (strictly POSIX-style)
    private final StampedLock lock = new StampedLock();

    public void doSomething() {
        long stamp = lock.writeLock(); // Acquire the lock for the first time
        try {
            System.out.println("🤖 Step1: Successfully entered doSomething()");

            // Invoke the second method which requires the exact same lock
            doSomethingElse();

        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public void doSomethingElse() {
        System.out.println("⏳ Step 2: Attempting to enter doSomethingElse()...");

        long stamp = lock.writeLock(); // 2. THE THREAD WILL DEADLOCK!
        try {
            System.out.println("This code will never be executed.");
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public static void main(String[] args) {
        PosixWidgetSimulation widget = new PosixWidgetSimulation();

        System.out.println("🚀 Starting POSIX deadlock simulation...");
        widget.doSomething();

        System.out.println("Program finished."); // This line will never be printed
    }
}
