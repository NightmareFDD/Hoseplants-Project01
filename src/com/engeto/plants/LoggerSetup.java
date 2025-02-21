package com.engeto.plants;

import java.io.IOException;
import java.util.logging.*;

public class LoggerSetup {
    public static void setupLogger() {
        try {
            Logger rootLogger = Logger.getLogger("");
            rootLogger.setUseParentHandlers(false); // Disable console output

            FileHandler fileHandler = new FileHandler("app.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            // Remove all existing handlers before adding a new one
            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            rootLogger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }
}



