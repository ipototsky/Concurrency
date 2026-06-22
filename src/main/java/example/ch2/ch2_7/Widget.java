package example.ch2.ch2_7;

public class Widget {

    public synchronized void doSomething() {
    }

    class LoggingWidget extends Widget {
        public synchronized void doSomething() {
            System.out.println(toString() + ": calling doSomething");
            super.doSomething();
        }
    }
}
