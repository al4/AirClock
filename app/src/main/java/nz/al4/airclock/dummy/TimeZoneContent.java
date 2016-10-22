package nz.al4.airclock.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class TimeZoneContent {

    /**
     * An array of Time Zone items
     */
    public static final List<TimeZoneItem> ITEMS = new ArrayList<>();

    /**
     * A map of Time Zone items, by ID
     */
    public static final Map<String, TimeZoneItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createTzItem(i));
        }
    }

    private static void addItem(TimeZoneItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static TimeZoneItem createTzItem(int position) {
        return new TimeZoneItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A Time Zone
     */
    public static class TimeZoneItem {
        public final String id;
        public final String displayName;
        public final String offset;

        public TimeZoneItem(String id, String displayName, String offset) {
            this.id = id;
            this.displayName = displayName;
            this.offset = offset;
        }

        @Override
        public String toString() {
            return offset;
        }
    }
}
