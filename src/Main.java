import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UsernameService {

    private final ConcurrentHashMap<String, Integer> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> attemptFrequency = new ConcurrentHashMap<>();

    public boolean checkAvailability(String username) {
        attemptFrequency
                .computeIfAbsent(username, k -> new AtomicInteger(0))
                .incrementAndGet();
        return !users.containsKey(username);
    }

    public void register(String username, int userId) {
        users.put(username, userId);
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int counter = 1;

        while (suggestions.size() < 3) {
            String candidate = username + counter;
            if (!users.containsKey(candidate)) {
                suggestions.add(candidate);
            }
            counter++;
        }

        String dotted = username.replace("_", ".");
        if (!users.containsKey(dotted)) {
            suggestions.add(dotted);
        }

        return suggestions;
    }

    public String getMostAttempted() {
        String maxUser = null;
        int maxCount = 0;

        for (Map.Entry<String, AtomicInteger> entry : attemptFrequency.entrySet()) {
            int count = entry.getValue().get();
            if (count > maxCount) {
                maxCount = count;
                maxUser = entry.getKey();
            }
        }

        return maxUser;
    }
}
