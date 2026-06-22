package example.ch2.ch2_8;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainCacheTest {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 10_000; // Количество параллельных запросов
        CachedFactorizer factorizer = new CachedFactorizer();

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        // Флаг, который зафиксирует, если мы поймали ошибку в кэше
        AtomicBoolean anomalyCaught = new AtomicBoolean(false);

        for (int i = 0; i < threadCount; i++) {
            // Чередуем запросы: половина потоков шлет 7, половина шлет 13
            final BigInteger inputNumber = (i % 2 == 0) ? new BigInteger("7") : new BigInteger("13");

            new Thread(() -> {
                try {
                    startLatch.await(); // Ждем одновременного старта

                    // Вызываем сервлет и получаем результат из его "кэша"
                    BigInteger[] resultFactors = factorizer.service(inputNumber);

                    // ПРОВЕРКА КЭША: Множитель внутри массива должен строго соответствовать входному чиону!
                    // Если для числа 13 вернулся массив с числом 7 — кэш рассинхронизирован.
                    if (!resultFactors[0].equals(inputNumber)) {
                        anomalyCaught.set(true);
                        System.out.printf("❌ АНОМАЛИЯ КЭША! Запрошено число %s, но из кэша вернулся множитель %s\n",
                                inputNumber, Arrays.toString(resultFactors));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }

        System.out.println("🚀 Запускаем одновременные запросы к сервлету с кэшем...");
        startLatch.countDown(); // Одновременный залп
        endLatch.await();       // Ждем завершения всех потоков

        if (!anomalyCaught.get()) {
            System.out.println("✅ В этом раунде кэш устоял. Попробуйте перезапустить тест.");
        }

        System.exit(0);
    }
}
