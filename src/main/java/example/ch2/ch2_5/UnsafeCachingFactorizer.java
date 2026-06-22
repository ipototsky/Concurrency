package example.ch2.ch2_5;

import net.jcip.annotations.NotThreadSafe;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

@NotThreadSafe
public class UnsafeCachingFactorizer extends GenericServlet implements Servlet {
    private final AtomicReference<BigInteger> lastNumber = new AtomicReference<>();
    private final AtomicReference<BigInteger[]> lastFactors = new AtomicReference<>();

    @Override
    public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
        BigInteger i = extractFromRequest(req);
        if (i.equals(lastNumber.get())) {
            encodeIntoResponse(resp, lastFactors.get());
        } else {
            BigInteger[] factors = factor(i);
            lastNumber.set(i);;
            lastFactors.set(factors);
            encodeIntoResponse(resp, factors);
        }
    }

    public BigInteger[] service(BigInteger inputNumber) {
        if (inputNumber.equals(lastNumber.get())) {
            encodeIntoResponse(null, lastFactors.get());
        } else {
            BigInteger[] factors = factor(inputNumber);
            lastNumber.set(inputNumber);;
            lastFactors.set(factors);
            encodeIntoResponse(null, factors);
        }
        return lastFactors.get();
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
