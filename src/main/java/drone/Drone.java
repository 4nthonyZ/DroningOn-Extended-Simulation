package drone;

import parcel.Parcel;
import parcel.ParcelManager;
import suburb.SuburbManager;

// Abstract drone base class. Handles movement, delivery states, and ticking logic.
// Applies GRASP: Information Expert (knows own state and behavior)
public abstract class Drone implements Simulation.Tickable {
    static int count = 1;
    final String id;
    final DispatchCentre dispatchCentre;
    final SuburbManager manager;
    final ParcelManager parcelManager; // [Added] Handle parcel information through manager
    Location location;
    private int parcelStreet;
    private int parcelHouse;
    private boolean isParcelFragile;
    private int parcelId; // [Modified] Track parcel by ID instead of object reference
    private boolean hasParcel = false;
    private boolean skipTick = false;
    public boolean hadFragile = false; // Used for priority decision later
    public boolean hadHeavy = false;

    enum State {
        WaitingForDispatch, TransitToSuburb, TransitToDelivery, Delivering,
        TransitToExit, TransitToCentre, Recharge
    }

    State state = State.WaitingForDispatch;
    int transDuration;

    Drone(DispatchCentre dispatchCentre, SuburbManager manager, ParcelManager parcelManager) {
        this.dispatchCentre = dispatchCentre;
        this.manager = manager;
        this.parcelManager = parcelManager; // [Added] Use ParcelManager for parcel access
        this.id = "D" + count++;
        location = null;
        Simulation.register(this);
    }

    // [Added] Simulate slow movement for fragile parcels (move every 2nd tick)
    public boolean shouldSkip() {
        return hasParcel && isParcelFragile &&
                (state == State.TransitToDelivery ||
                        state == State.TransitToSuburb ||
                        state == State.Delivering);
    }

    public void tick() {
        if (shouldSkip()) {
            skipTick = !skipTick;
        } else {
            skipTick = false;
        }

        Location nextLocation;
        switch (state) {
            case WaitingForDispatch:
                dispatchCentre.requestDispatch(this);
                break;

            case TransitToSuburb:
                if (skipTick) break;
                if (transDuration > 0) {
                    transDuration--;
                } else {
                    dispatchCentre.requestAccess(this, manager.getEntry());
                }
                break;

            case TransitToDelivery:
                if (skipTick) break;
                nextLocation = location.getRoad(manager.getDirection("EAST"));
                if (nextLocation == null || !manager.isSameStreet(nextLocation.id, parcelStreet)) {
                    nextLocation = location.getRoad(manager.getDirection("SOUTH"));
                }
                assert nextLocation != null : "Reached " + location.id + " without finding street: " + parcelStreet;
                dispatchCentre.requestAccess(this, nextLocation);
                break;

            case Delivering:
                if (!hasParcel) {
                    location.endDelivery();
                    state = State.TransitToExit;
                } else {
                    if (!skipTick) {
                        location.startDelivery();
                        // [Modified] Use parcelManager to find and remove the delivered parcel
                        Parcel parcel = parcelManager.getParcelById(parcelId);
                        if (parcel != null) {
                            Simulation.deliver(parcel);
                            parcelManager.removeParcel(parcel);
                        }
                        hasParcel = false;
                    }
                }
                break;

            case TransitToExit:
                nextLocation = location.getRoad(manager.getDirection("EAST"));
                if (nextLocation == null) {
                    nextLocation = location.getRoad(manager.getDirection("NORTH"));
                }
                assert nextLocation != null : "Can't go east or north from " + location.id;
                dispatchCentre.requestAccess(this, nextLocation);
                break;

            case TransitToCentre:
                if (location != null) location.departDrone();
                if (transDuration > 0) {
                    transDuration--;
                } else {
                    state = State.Recharge;
                }
                break;

            case Recharge:
                state = State.WaitingForDispatch;
                break;
        }
    }

    void dispatch(Parcel parcel) {
        assert state == State.WaitingForDispatch : id + " dispatched when not waiting for dispatch";

        // [Modified] Store parcel ID and delegate parcel access to ParcelManager
        parcelId = parcel.getId();
        parcelManager.addParcel(parcel);

        // [Modified] Use ParcelManager to get delivery details
        parcelStreet = parcelManager.getParcelStreet(parcelId);
        parcelHouse = parcelManager.getParcelHouse(parcelId);

        // [Modified] Moved fragility/heaviness check to DispatchCentre (Controller)
        isParcelFragile = dispatchCentre.isFragile(parcel);
        hadFragile = isParcelFragile;
        hadHeavy = dispatchCentre.isHeavy(parcel);

        hasParcel = true;
        transDuration = dispatchCentre.timeToSuburb;
        state = State.TransitToSuburb;
    }

    void grantAccess(Location location) {
        switch (state) {
            case TransitToSuburb:
                if (this.location != null) this.location.departDrone();
                location.arriveDrone(this);
                state = State.TransitToDelivery;
                break;

            case TransitToDelivery:
                if (this.location != null) this.location.departDrone();
                location.arriveDrone(this);
                if (location.id.deliveryAddress(parcelStreet, parcelHouse)) {
                    state = State.Delivering;
                }
                break;

            case TransitToExit:
                if (this.location != null) this.location.departDrone();
                location.arriveDrone(this);
                if (location == manager.getExit()) {
                    state = State.TransitToCentre;
                    transDuration = dispatchCentre.timeToSuburb;
                }
                break;

            default:
                assert false : id + " access granted to " + location.id + " in non-requesting state " + state;
        }
    }

    public String toString() {
        return id;
    }

    void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
