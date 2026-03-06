package boba.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an event that is tentatively scheduled in multiple time slots.
 * Can be confirmed to one specific slot, converting it into a regular Event.
 */
public class TentativeEvent extends Task {
    private List<String[]> slots;

    /**
     * Creates a tentative event with multiple possible time slots.
     *
     * @param description The description of the event.
     * @param slots The list of time slots, each as a [from, to] pair.
     */
    public TentativeEvent(String description, List<String[]> slots) {
        super(description);
        assert slots != null && slots.size() >= 2 : "Need at least 2 slots";
        this.slots = new ArrayList<>(slots);
    }

    /**
     * Returns the list of tentative time slots.
     *
     * @return The list of [from, to] pairs.
     */
    public List<String[]> getSlots() {
        return slots;
    }

    /**
     * Confirms a specific slot and returns a regular Event.
     *
     * @param slotIndex The 0-based index of the slot to confirm.
     * @return A new Event with the confirmed time slot.
     */
    public Event confirm(int slotIndex) {
        assert slotIndex >= 0 && slotIndex < slots.size()
                : "Slot index out of bounds";
        String[] slot = slots.get(slotIndex);
        return new Event(description, slot[0], slot[1]);
    }

    /**
     * Returns the number of tentative slots.
     *
     * @return The number of slots.
     */
    public int getSlotCount() {
        return slots.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[TE][\u2026] " + description + "\n");
        for (int i = 0; i < slots.size(); i++) {
            sb.append("      Slot " + (i + 1) + ": "
                    + slots.get(i)[0] + " - " + slots.get(i)[1]);
            if (i < slots.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
