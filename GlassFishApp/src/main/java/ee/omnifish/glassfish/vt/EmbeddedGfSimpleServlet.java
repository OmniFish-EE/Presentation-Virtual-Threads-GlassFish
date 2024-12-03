package ee.omnifish.glassfish.vt;


import static java.time.Duration.ofMillis;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/")
public class EmbeddedGfSimpleServlet extends HttpServlet {

    static AtomicReference<LocalDateTime> lastTimeRef = new AtomicReference<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        runInDeeperStack(() -> sleepWithVariation(50L, 150L), 100);

        response.setContentType("text/plain");
        final PrintWriter writer = response.getWriter();
        writer.println("Hello from Embedded GlassFish ! Running on " + (Thread.currentThread().isVirtual() ? "virtual" : " platform") + " threads.");
    }

    private void sleepWithVariation(long minSleepMillis, long maxSleepMillis) throws RuntimeException {
        try {
            Thread.sleep(ofMillis(random(minSleepMillis, maxSleepMillis)));
        } catch (InterruptedException ex) {
            Logger.getLogger(EmbeddedGfSimpleServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    final Random random = new Random(0);

    private long random(long min, long max) {
        return random.nextLong(min, max + 1);
    }

    private void runInDeeperStack(Runnable runnable, int depth) {
        if (depth > 0) {
            runInDeeperStack(runnable, depth-1);
        } else {
            runnable.run();
        }
    }

}
