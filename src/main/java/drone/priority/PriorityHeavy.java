package drone.priority;

import drone.DispatchCentre;
import drone.Drone;
import drone.Location;

// Priority rule: Drones delivering heavy parcels get second-highest priority.
// Uses hadHeavy flag to check if the drone carried a heavy item.
public class PriorityHeavy extends PriorityManager {
    @Override
    public int getPriority(Drone drone, Location location, DispatchCentre dispatchCentre) {
        // Use hadHeavy field from Drone which indicates if it's carrying/carried a heavy parcel
        boolean carryingHeavy = drone.hadHeavy;
        return carryingHeavy ? 2 : Integer.MAX_VALUE; // Heavy parcels have second-highest priority
    }
}