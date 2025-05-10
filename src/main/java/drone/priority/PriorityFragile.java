package drone.priority;

import drone.DispatchCentre;
import drone.Drone;
import drone.Location;

// Priority rule: Drones delivering fragile parcels get highest priority.
// Uses hadFragile flag to indicate whether the drone is/was carrying a fragile item.
public class PriorityFragile extends PriorityManager {
    @Override
    public int getPriority(Drone drone, Location location, DispatchCentre dispatchCentre) {
        // Use hadFragile field from Drone which indicates if it's carrying/carried a fragile parcel
        boolean carryingFragile = drone.hadFragile;
        return carryingFragile ? 1 : Integer.MAX_VALUE; // Fragile parcels have highest priority
    }
}