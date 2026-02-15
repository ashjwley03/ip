package boba.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Loads and provides random motivational quotes from a text file.
 */
public class CheerLoader {
    private static final String CHEER_FILE = "/cheer.txt";
    private ArrayList<String> quotes;
    private Random random;

    /**
     * Creates a new CheerLoader and loads quotes from the resource file.
     */
    public CheerLoader() {
        this.quotes = new ArrayList<>();
        this.random = new Random();
        loadQuotes();
    }

    /**
     * Loads quotes from the cheer.txt resource file.
     */
    private void loadQuotes() {
        try {
            InputStream inputStream = getClass().getResourceAsStream(CHEER_FILE);
            if (inputStream == null) {
                // Fallback quotes if file not found
                addFallbackQuotes();
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    quotes.add(line.trim());
                }
            }
            reader.close();
        } catch (IOException e) {
            addFallbackQuotes();
        }

        if (quotes.isEmpty()) {
            addFallbackQuotes();
        }
    }

    /**
     * Adds the given quotes to the list.
     *
     * @param newQuotes The quotes to add.
     */
    private void addQuotes(String... newQuotes) {
        for (String quote : newQuotes) {
            quotes.add(quote);
        }
    }

    /**
     * Adds fallback quotes in case the file cannot be loaded.
     */
    private void addFallbackQuotes() {
        addQuotes(
                "Keep going – even the best programmers started out writing 'Hello World'!",
                "You've got this! Your future self will thank you for not giving up today.",
                "Every expert was once a beginner. Keep coding!"
        );
    }

    /**
     * Returns a random motivational quote.
     *
     * @return A random quote string.
     */
    public String getRandomQuote() {
        int index = random.nextInt(quotes.size());
        return quotes.get(index);
    }
}
