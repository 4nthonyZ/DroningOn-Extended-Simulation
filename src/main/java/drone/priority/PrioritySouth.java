package drone.priority;

import drone.DispatchCentre;
import drone.Drone;
import drone.Location;
import suburb.Suburb;
// Priority rule: Drones coming from the south get third-highest priority.
// Encourages fairness to drones that have traveled further (counterclockwise rule).
public class PrioritySouth extends PriorityManager {
    @Override
    public int getPriority(Drone drone, Location location, DispatchCentre dispatchCentre) {
        boolean fromSouth = drone.getLocation() != null &&
                drone.getLocation().getRoad(Suburb.Direction.NORTH) == location;
        return fromSouth ? 3 : Integer.MAX_VALUE;
    }
}