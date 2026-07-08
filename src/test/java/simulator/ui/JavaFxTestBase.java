package simulator.ui;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

// Boots the JavaFX toolkit once and runs UI work on the FX application thread.
abstract class JavaFxTestBase {
    private static boolean toolkitStarted;

    protected static synchronized void initToolkit() {
        if (toolkitStarted) {
            return;
        }
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await(10, TimeUnit.SECONDS);
        } catch (IllegalStateException alreadyRunning) {
            // Toolkit was initialized by another test; reuse it.
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
        // Keep the toolkit alive after stages close so later tests can reuse it.
        Platform.setImplicitExit(false);
        toolkitStarted = true;
    }

    // Runs the action on the FX thread and blocks until it finishes.
    protected static void runFx(Runnable action) {
        initToolkit();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.runLater(
                () -> {
                    try {
                        action.run();
                    } catch (Throwable throwable) {
                        failure.set(throwable);
                    } finally {
                        latch.countDown();
                    }
                });
        awaitLatch(latch, 15);
        if (failure.get() != null) {
            throw new AssertionError("FX action failed", failure.get());
        }
    }

    protected static void awaitLatch(CountDownLatch latch, long seconds) {
        try {
            if (!latch.await(seconds, TimeUnit.SECONDS)) {
                throw new AssertionError("Timed out waiting for FX work");
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting for FX work", interrupted);
        }
    }

    protected static List<Button> findButtons(Parent parent) {
        List<Button> buttons = new ArrayList<>();
        collect(parent, Button.class, buttons);
        return buttons;
    }

    protected static List<TextField> findTextFields(Parent parent) {
        List<TextField> fields = new ArrayList<>();
        collect(parent, TextField.class, fields);
        return fields;
    }

    private static <T extends Node> void collect(Parent parent, Class<T> type, List<T> out) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            if (type.isInstance(child)) {
                out.add(type.cast(child));
            }
            if (child instanceof Parent nested) {
                collect(nested, type, out);
            }
        }
    }
}
