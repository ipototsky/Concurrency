package example.ch2.ch2_6;

import net.jcip.annotations.GuardedBy;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.math.BigInteger;

public class SynchronizedFactorizer extends GenericServlet implements Servlet {
    @GuardedBy("this")
    private BigInteger lastNumber;
    @GuardedBy("this")
    private BigInteger[] lastFactors;

    @Override
    public synchronized void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
        BigInteger i = extractFromRequest(req);
        if (i.equals(lastNumber)) {
            encodeIntoResponse(resp, lastFactors);
        } else {
            BigInteger[] factors = factor(i);
            lastNumber = i;
            lastFactors = factors;
            encodeIntoResponse(resp, factors);
        }
    }

    public synchronized BigInteger[] service(BigInteger inputNumber) {
        if (inputNumber.equals(lastNumber)) {
            encodeIntoResponse(null, lastFactors);
        } else {
            BigInteger[] factors = factor(inputNumber);
            lastNumber = inputNumber;
            lastFactors = factors;
            encodeIntoResponse(null, factors);
        }
        return  lastFactors;
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
