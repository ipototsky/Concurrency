package example.ch2.ch2_2;

import java.lang.reflect.Proxy;
import java.util.concurrent.CountDownLatch;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class FactorizerTest {
    public static void main(String[] args) throws Exception {
        int totalRequests = 100_000; // 100_000 requests imitation

        UnsafeCountingFactorizer factorizer = new UnsafeCountingFactorizer();

        //Create mockRequest
        ServletRequest mockRequest = (ServletRequest) Proxy.newProxyInstance(
                ServletRequest.class.getClassLoader(),
                new Class<?>[]{ServletRequest.class},
                (proxy, method, methodArgs) -> null
        );

        //Create mockResponse
        ServletResponse mockResponse = (ServletResponse) Proxy.newProxyInstance(
                ServletResponse.class.getClassLoader(),
                new Class<?>[]{ServletResponse.class},
                (proxy, method, methodArgs) -> null
        );

        // countdown for start and end
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(totalRequests);

        // Start threads
        for (int i = 0; i < totalRequests; i++) {
            new Thread(() -> {
                try {
                    startLatch.await(); // wait for the signal to start

                    // imitate servlet call
                    factorizer.service(mockRequest, mockResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }

        System.out.println("🚀 WebServer imitation initiated. Start sending requests...");

        long startTime = System.currentTimeMillis();
        startLatch.countDown(); // 100_000 requests simultaneously
        endLatch.await();       // wait for the completion of all requests
        long endTime = System.currentTimeMillis();

        // Check counter after run
        long actualCount = factorizer.getCount();

        System.out.println("\n--- Result of Servlet Test ---");
        System.out.printf("Processing Time: %d ms\n", (endTime - startTime));
        System.out.printf("Total Requests Sent: %,d\n", totalRequests);
        System.out.printf("Servlet actual Count (count):  %,d\n", actualCount);

        if (actualCount < totalRequests) {
            System.out.printf("❌ Anomaly caught! Lost %,d visits from Race Condition.\n",
                    (totalRequests - actualCount));
        } else {
            System.out.println("✅ No errors found. Try increasing the number of requests.");
        }
    }
}