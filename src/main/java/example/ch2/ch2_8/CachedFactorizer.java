package example.ch2.ch2_8;

import net.jcip.annotations.GuardedBy;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.math.BigInteger;

public class CachedFactorizer extends GenericServlet implements Servlet {
    @GuardedBy("this")
    private volatile BigInteger lastNumber;
    @GuardedBy("this")
    private volatile BigInteger[] lastFactors;
    @GuardedBy("this")
    private long hits;
    @GuardedBy("this")
    private long cacheHits;

    public synchronized long getHits() {
        return hits;
    }

    public synchronized double getCacheHitRatio() {
        return (double) cacheHits / (double) hits;
    }

    public BigInteger[] service(BigInteger inputNumber) {
        BigInteger i = inputNumber;
        BigInteger[] factors = null;
        synchronized (this) {
            ++hits;
            if (i.equals(lastNumber)) {
                ++cacheHits;
                encodeIntoResponse(null, factors);
                return lastFactors.clone();
            }
        }
        if (factors == null) {
            factors = factor(i);
            synchronized (this) {
                lastNumber = i;
                lastFactors = factors.clone();
            }
        }
        encodeIntoResponse(null, factors);
        return factors;
    }

//    public BigInteger[] service(BigInteger inputNumber) {
//        BigInteger[] factors = null;
//        boolean fromCache = false;
//        synchronized (this) {
//            ++hits;
//            if (inputNumber.equals(lastNumber)) {
//                ++cacheHits;
//                factors = lastFactors.clone();
//                fromCache = true;
//            }
//        }
//
//        if (factors == null) {
//            factors = factor(inputNumber);
//            synchronized (this) {
//                lastNumber = inputNumber;
//                lastFactors = factors.clone();
//            }
//        }
//        encodeIntoResponse(null, factors);
//        return factors;
//    }

    @Override
    public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = null;
        ++hits;
        synchronized (this) {
            if (i.equals(lastNumber)) {
                ++cacheHits;
                factors = lastFactors.clone();
            }
        }
        if (factors == null) {
            factors = factor(i);
            synchronized (this) {
                lastNumber = i;
                lastFactors = factors.clone();
            }
        }
        encodeIntoResponse(resp, factors);
    }

    private BigInteger extractFromRequest(ServletRequest req) {
        return new BigInteger("5");
    }

    void encodeIntoResponse(ServletResponse res, BigInteger[] factors) {
    }

    BigInteger[] factor(BigInteger i) {
        return new BigInteger[]{i};
    }
}
