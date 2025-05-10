package parcel;

// Represents fragile or heavy parcels
public class SpecialParcel extends Parcel {
    public SpecialParcel(int street, int house, int arrival, int weight, boolean isFragile) {
        super(street, house, arrival, weight, isFragile);
    }
}
