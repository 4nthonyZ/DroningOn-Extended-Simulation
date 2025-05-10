package drone.priority;

import drone.DispatchCentre;
import drone.Drone;
import drone.Location;
// Default fallback priority level.
public class PriorityBasic extends PriorityManager {
    @Override
    public int getPriority(Drone drone, Location location, DispatchCentre dispatchCentre) {
        return 4;
    }
}