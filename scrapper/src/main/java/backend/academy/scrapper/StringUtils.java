package backend.academy.scrapper;

public class StringUtils {
    private StringUtils() {
        // Utility class, prevent instantiation
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
} 