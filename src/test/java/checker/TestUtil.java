package checker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

public final class TestUtil {
    private static boolean fxInitialized = false;

    public static void initFx() {
        if (fxInitialized) return;
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await(5, TimeUnit.SECONDS);
            fxInitialized = true;
        } catch (IllegalStateException e) {
            // Already initialized
            fxInitialized = true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void runOnFxAndWait(Runnable r) {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                r.run();
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
