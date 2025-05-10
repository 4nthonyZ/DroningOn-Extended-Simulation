package parcel;

// Represents a simple non-fragile, non-heavy parcel
public class BasicParcel extends Parcel {
    public BasicParcel(int street, int house, int arrival, int weight) {
        super(street, house, arrival, weight, false); // Always not fragile
    }
}
