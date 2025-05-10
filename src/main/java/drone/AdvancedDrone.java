package drone;

import parcel.ParcelManager;
import suburb.SuburbManager;

// Concrete drone subclass for more advanced logic (can be extended further).
public class AdvancedDrone extends Drone {
    public AdvancedDrone(DispatchCentre dispatchCentre, SuburbManager manager, ParcelManager parcelManager) {
        super(dispatchCentre, manager, parcelManager);
    }
}