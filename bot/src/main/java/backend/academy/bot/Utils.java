package backend.academy.bot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Utility class providing common helper methods for the bot application.
 */
public final class Utils {
    
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$"
    );
    
    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private Utils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Validates if the given string is a valid URL.
     * 
     * @param url the URL string to validate
     * @return true if the URL is valid, false otherwise
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return URL_PATTERN.matcher(url.trim()).matches();
    }
    
    /**
     * Formats the current timestamp for logging purposes.
     * 
     * @return formatted timestamp string
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(LOG_FORMATTER);
    }
    
    /**
     * Sanitizes user input by trimming whitespace and converting to lowercase.
     * 
     * @param input the input string to sanitize
     * @return sanitized string, or null if input is null
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().toLowerCase();
    }
    
    /**
     * Checks if a string is null, empty, or contains only whitespace.
     * 
     * @param str the string to check
     * @return true if the string is null, empty, or whitespace-only
     */
    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
} 