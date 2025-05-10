package parcel;

import suburb.Suburb;

// Abstract parcel class for all parcel types.
// [GRASP: Information Expert] Holds its own data: address, weight, fragility.
public abstract class Parcel implements Comparable<Parcel> {
    private static int nextId = 1; // [Added] Unique parcel ID
    private final int id;
    protected final int street;
    protected final int house;
    protected final int arrival;
    protected final int weight;
    protected final boolean isFragile;

    public Parcel(int street, int house, int arrival, int weight, boolean isFragile) {
        this.id = nextId++; // [Added] Auto-increment ID for tracking
        this.street = street;
        this.house = house;
        this.arrival = arrival;
        this.weight = weight;
        this.isFragile = isFragile;
    }

    // [Getters]
    public int getId() { return id; } // [Added]
    public int myStreet() { return street; }
    public int myHouse() { return house; }
    public int myArrival() { return arrival; }
    public int myWeight() { return weight; }
    public boolean isFragile() { return isFragile; }

    // [Used for sorting]
    @Override
    public int compareTo(Parcel i) {
        int streetDiff = this.street - i.street;
        return (streetDiff == 0) ? this.house - i.house : streetDiff;
    }

    @Override
    public String toString() {
        return "Parcel from time " + arrival +
                " addressed to " + house + " " + Suburb.StreetName.values[street-1] +
                " Street; Weight: " + weight + " Fragile?: " + isFragile;
    }
}
