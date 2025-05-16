package com.books.utils.helper;

import com.books.exception.RetryOperationException;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class RetryHelper {
    private static final Logger logger = Logger.getLogger(RetryHelper.class.getName());
    private static final int MAX_RETRIES = 20;
    private static final long RETRY_DELAY_MS = 2000; // 2 секунды

    private RetryHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> T executeWithRetry(Supplier<T> action) throws RetryOperationException {
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return action.get();  // Выполняем запрос
            } catch (Exception ex) {
                lastException = ex;

                logger.info("Attempt " + attempt + " was resulted with exception: " + ex.getMessage());

                if (attempt < MAX_RETRIES) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS); // Задержка перед новой попыткой
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        throw new RetryOperationException("Request was unable to success after " + MAX_RETRIES + " attempts.",
                lastException);
    }
}
