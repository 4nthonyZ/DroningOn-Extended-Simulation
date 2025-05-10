package parcel;

import drone.DispatchCentre;
import java.util.*;

// Manages all currently active parcels
// [GRASP: Information Expert] Responsible for parcel lookup, tracking, and delivery status
public class ParcelManager {
    private final List<Parcel> parcels; // All tracked parcels
    private final Map<Integer, Parcel> parcelMap; // [Added] ID-to-Parcel mapping

    public ParcelManager() {
        this.parcels = new ArrayList<>();
        this.parcelMap = new HashMap<>();
    }

    // [Called by Drone] Add parcel to manager
    public void addParcel(Parcel parcel) {
        parcels.add(parcel);
        parcelMap.put(parcel.getId(), parcel); // [Modified] Enable ID-based lookup
    }

    // [Called after delivery] Remove parcel
    public boolean removeParcel(Parcel parcel) {
        parcelMap.remove(parcel.getId());
        return parcels.remove(parcel);
    }

    // [Optional] Filter by street
    public List<Parcel> getParcelsByStreet(int street) {
        List<Parcel> result = new ArrayList<>();
        for (Parcel parcel : parcels) {
            if (parcel.myStreet() == street) {
                result.add(parcel);
            }
        }
        return result;
    }

    // [Optional] Bulk dispatch for testing
    public void dispatchParcels(DispatchCentre dispatchCentre) {
        for (Parcel parcel : parcels) {
            dispatchCentre.arrive(List.of(parcel));
        }
        parcels.clear();
        parcelMap.clear();
    }

    public int getParcelCount() {
        return parcels.size();
    }

    // [Used by Drone] Get parcel by ID
    public Parcel getParcelById(int id) {
        return parcelMap.get(id);
    }

    public int getParcelStreet(int id) {
        Parcel parcel = parcelMap.get(id);
        return parcel != null ? parcel.myStreet() : 0;
    }

    public int getParcelHouse(int id) {
        Parcel parcel = parcelMap.get(id);
        return parcel != null ? parcel.myHouse() : 0;
    }

    public boolean isParcelFragile(int id) {
        Parcel parcel = parcelMap.get(id);
        return parcel != null && parcel.isFragile();
    }
}
