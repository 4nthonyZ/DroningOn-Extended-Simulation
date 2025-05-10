package drone.priority;

// Enum used to represent priority levels in a readable format.
public enum PriorityLevel {
    FRAGILE(1), HEAVY(2), SOUTH(3), BASIC(4);

    private final int value;
    PriorityLevel(int value) { this.value = value; }
    public int value() { return value; }
}