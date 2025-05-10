package drone.priority;

import drone.DispatchCentre;
import drone.Drone;
import drone.Location;

// Abstract base class for priority calculation strategy.
// Each subclass implements a different priority rule (e.g., fragile, heavy).
public abstract class PriorityManager {
    public abstract int getPriority(Drone drone, Location location, DispatchCentre dispatchCentre);
}