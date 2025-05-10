package suburb;

import drone.Location;

// Represents the structure of the delivery suburb: streets, avenues, and address logic
public class Suburb {
    public enum StreetName {
        First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth;
        public static final StreetName[] values = values();
    }

    final static int maxStreets = StreetName.values().length;

    public enum Direction {
        NORTH, SOUTH, EAST, WEST;

        // [Added] Utility for linking roads
        static Direction opposite(Direction d) {
            return switch (d) {
                case NORTH -> SOUTH;
                case SOUTH -> NORTH;
                case EAST  -> WEST;
                case WEST  -> EAST;
            };
        }
    }

    public enum Avenue {Out, Back}

    // ========== Address Identifiers ==========

    // [Used for delivery locations] Street address = (street number, odd house number)
    static public class StreetId implements Location.Id {
        final int street;
        final int oddHouse;

        StreetId(int street, int oddHouse) {
            this.street = street;
            this.oddHouse = oddHouse;
        }

        public Location.Id makeId(int position) {
            return new StreetId(this.street, position * 2 - 1);
        }

        public String toString() {
            return oddHouse + " " + StreetName.values[street - 1] + " Street";
        }

        boolean sameStreet(int street) {
            return street == this.street;
        }

        public boolean deliveryAddress(int street, int house) {
            int oddHouse = (house % 2 == 0) ? house - 1 : house;
            return this.street == street && this.oddHouse == oddHouse;
        }
    }

    // [Used internally] Avenue locations (no delivery addresses)
    static public class AvenueId implements Location.Id {
        final Avenue avenue;
        final int numSouth;

        AvenueId(Avenue avenue, int numSouth) {
            this.avenue = avenue;
            this.numSouth = numSouth;
        }

        public Location.Id makeId(int position) {
            return new AvenueId(this.avenue, position);
        }

        public String toString() {
            return numSouth + " " + avenue + " Avenue";
        }

        public boolean deliveryAddress(int street, int house) {
            return false;
        }
    }

    // ========== Layout Data ==========

    Location[] streets;
    Location outAvenue, backAvenue;

    Location getEntry() { return outAvenue; }
    Location getExit() { return backAvenue; }

    final public int NUMSTREETS;
    final public int NUMHOUSES;

    // ========== Layout Construction ==========

    Suburb(int numStreets, int numHouses) {
        assert 1 <= numStreets && numStreets <= maxStreets;
        assert 1 <= numHouses;

        this.NUMSTREETS = numStreets;
        this.NUMHOUSES = numHouses;

        // Create each street as an eastward linked list
        streets = new Location[NUMSTREETS];
        for (int i = 1; i <= NUMSTREETS; i++) {
            streets[i - 1] = constructRoad(Direction.EAST, NUMHOUSES, new StreetId(i, 1));
        }

        // Create avenues (vertical connections)
        outAvenue = constructRoad(Direction.SOUTH, 3 * NUMSTREETS, new AvenueId(Avenue.Out, 1));
        backAvenue = constructRoad(Direction.SOUTH, 3 * NUMSTREETS, new AvenueId(Avenue.Back, 1));

        // Connect streets to avenues
        Location start = outAvenue.getRoad(Direction.SOUTH);
        Location end = backAvenue.getRoad(Direction.SOUTH);
        int i = 0;
        while (true) {
            streets[i].setRoad(Direction.WEST, start);
            start.setRoad(Direction.EAST, streets[i]);

            Location streetEnd = streets[i].end(Direction.EAST);
            streetEnd.setRoad(Direction.EAST, end);
            end.setRoad(Direction.WEST, streetEnd);

            start = start.getRoad(Direction.SOUTH).getRoad(Direction.SOUTH);
            if (start == null) break;
            start = start.getRoad(Direction.SOUTH);
            end = end.getRoad(Direction.SOUTH).getRoad(Direction.SOUTH).getRoad(Direction.SOUTH);
            i++;
        }
    }

    // Construct a linear road (linked list of Location)
    private Location constructRoad(Direction direction, int length, Location.Id id) {
        Location road = new Location(id);
        Location previous = road;
        for (int j = 2; j <= length; j++) {
            Location current = new Location(id.makeId(j));
            previous.setRoad(direction, current);
            current.setRoad(Direction.opposite(direction), previous);
            previous = current;
        }
        return road;
    }
}
