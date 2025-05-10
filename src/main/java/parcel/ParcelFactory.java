package parcel;

// Factory to create parcel instances based on their attributes
// [GRASP: Creator] Factory centralizes creation logic
public class ParcelFactory {
    public static Parcel createParcel(int street, int house, int arrival, int weight, boolean isFragile) {
        if (isFragile || weight > 100) {
            return new SpecialParcel(street, house, arrival, weight, isFragile); // [Modified] dynamic logic
        } else {
            return new BasicParcel(street, house, arrival, weight);
        }
    }
}
