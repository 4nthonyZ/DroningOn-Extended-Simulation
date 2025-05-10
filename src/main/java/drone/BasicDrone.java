package drone;

import parcel.ParcelManager;
import suburb.SuburbManager;


// Basic drone with no special behavior.
public class BasicDrone extends Drone {
    public BasicDrone(DispatchCentre dispatchCentre, SuburbManager manager, ParcelManager parcelManager) {
        super(dispatchCentre, manager, parcelManager); // Pass ParcelManager to parent constructor
    }
}