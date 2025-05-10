package drone;

import parcel.ParcelManager;
import suburb.SuburbManager;

// Factory class that creates drone instances based on type.
// Follows GRASP: Creator + Polymorphism.
public class DroneFactory {
    public static Drone createDrone(DispatchCentre dispatchCentre, SuburbManager manager, String droneType, ParcelManager parcelManager) {
        switch (droneType) {
            case "Basic":
                return new BasicDrone(dispatchCentre, manager, parcelManager);
            case "Advanced":
                return new AdvancedDrone(dispatchCentre, manager, parcelManager);
            default:
                throw new IllegalArgumentException("Unknown drone type: " + droneType);
        }
    }
}