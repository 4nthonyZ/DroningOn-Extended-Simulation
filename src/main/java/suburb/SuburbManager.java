package suburb;

import drone.Location;

// Acts as a facade/wrapper to provide controlled access to Suburb and View
// [GRASP: Controller] Central interface for other modules to interact with the suburb
public class SuburbManager {
    private final Suburb suburb;
    private final SuburbView view;

    public SuburbManager(int numStreets, int numHouses) {
        this.suburb = new Suburb(numStreets, numHouses);
        this.view = new SuburbView(numStreets, numHouses); // Observer registered to view changes
    }

    // ========== Suburb Delegates ==========

    public Location getEntry() { return suburb.getEntry(); }
    public Location getExit() { return suburb.getExit(); }
    public int getNumStreets() { return suburb.NUMSTREETS; }
    public int getNumHouses() { return suburb.NUMHOUSES; }

    public Location getOutAvenue() { return suburb.outAvenue; }
    public Location getBackAvenue() { return suburb.backAvenue; }
    public Location getStreet(int index) {
        if (index < 0 || index >= suburb.NUMSTREETS) {
            throw new IndexOutOfBoundsException("Street index out of range: " + index);
        }
        return suburb.streets[index];
    }

    public Suburb.StreetId createStreetId(int street, int oddHouse) {
        return new Suburb.StreetId(street, oddHouse);
    }

    public boolean isStreetId(Location.Id id) {
        return id instanceof Suburb.StreetId;
    }

    public boolean isSameStreet(Suburb.StreetId id, int streetNum) {
        return id.sameStreet(streetNum);
    }

    public boolean isDeliveryAddress(Suburb.StreetId id, int street, int house) {
        return id.deliveryAddress(street, house);
    }

    // Safe street matching (used in drone logic)
    public boolean isSameStreet(Location.Id id, int street) {
        if (id instanceof Suburb.StreetId sid) {
            return sid.sameStreet(street);
        }
        return false;
    }

    public Suburb.Direction getDirection(String name) {
        return Suburb.Direction.valueOf(name.toUpperCase());
    }

    // View access isn't required here; handled by Observer pattern
}
