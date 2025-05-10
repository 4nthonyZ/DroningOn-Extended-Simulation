package drone;

import drone.priority.PriorityManager;
import parcel.Parcel;
import parcel.ParcelManager;
import suburb.SuburbManager;

import java.util.*;
import java.util.ServiceLoader;
// Acts as a Controller (GRASP): manages drones, parcel dispatch, and access coordination.
public class DispatchCentre implements Simulation.Tickable {
    final int numdrones;
    public final int timeToSuburb;
    public final int weightThreshold;
    final Queue<Parcel> waitingForDelivery;
    final Set<Drone> drones;
    final Map<Location, List<Drone>> requests = new LinkedHashMap<>();
    private final List<PriorityManager> priorityManagers = new ArrayList<>();
    private final ParcelManager parcelManager; // Add ParcelManager instance

    public DispatchCentre(SuburbManager mgr, int timeToSuburb, int numdrones, int weightThreshold) {
        this.timeToSuburb = timeToSuburb;
        this.weightThreshold = weightThreshold;
        this.numdrones = numdrones;
        this.parcelManager = new ParcelManager(); // Initialize ParcelManager

        waitingForDelivery = new LinkedList<>();
        drones = new HashSet<>();

        loadPriorityManagers();
        initRequestMap(mgr);

        // Create and register drones
        for (int i = 0; i < numdrones; i++) {
            String droneType = (i % 2 == 0) ? "Basic" : "Advanced";
            drones.add(DroneFactory.createDrone(this, mgr, droneType, parcelManager)); // Use the ParcelManager instance
        }

        Simulation.register(this);
    }

    private void loadPriorityManagers() {
        ServiceLoader<PriorityManager> loader = ServiceLoader.load(PriorityManager.class);
        loader.forEach(priorityManagers::add);
    }

    private void initRequestMap(SuburbManager mgr) {
        // Back Avenue from north to south
        for (Location l = mgr.getBackAvenue(); l != null; l = l.getRoad(mgr.getDirection("SOUTH"))) {
            requests.put(l, new ArrayList<>());
        }
        // Go east to the end of each street and then go back west.
        for (int i = 0; i < mgr.getNumStreets(); i++) {
            Location e = mgr.getStreet(i);
            for (int j = 0; j < mgr.getNumHouses(); j++) {
                e = e.getRoad(mgr.getDirection("EAST"));
            }
            for (int j = 0; j < mgr.getNumHouses(); j++) {
                e = e.getRoad(mgr.getDirection("WEST"));
                requests.put(e, new ArrayList<>());
            }
        }
        // Out Avenue from south to north
        Location e = mgr.getOutAvenue();
        while (e.getRoad(mgr.getDirection("SOUTH")) != null) {
            e = e.getRoad(mgr.getDirection("SOUTH"));
        }
        for (Location l = e; l != null; l = l.getRoad(mgr.getDirection("NORTH"))) {
            requests.put(l, new ArrayList<>());
        }
    }

    public void arrive(List<Parcel> parcels) {
        for (Parcel parcel : parcels) {
            waitingForDelivery.add(parcel);
            String s = "Arrived: " + parcel;
            System.out.println(s);
            Simulation.logger.logEvent("%5d: %s\n", Simulation.now(), s);
        }
    }

    public boolean isHeavy(Parcel parcel) {
        return parcel.myWeight() > weightThreshold;
    }
    public boolean isFragile(Parcel parcel) {
        return parcel.isFragile();
    }

    public void requestDispatch(Drone drone) {
        if (!waitingForDelivery.isEmpty()) {
            drone.dispatch(waitingForDelivery.remove());
            drones.remove(drone);
        } else {
            drones.add(drone);
        }
    }

    public boolean allDronesBack() {
        return drones.size() == numdrones;
    }

    void requestAccess(Drone drone, Location location) {
        if (requests.containsKey(location)) {
            requests.get(location).add(drone);
        }
    }

    @Override
    public void tick() {
        for (Map.Entry<Location, List<Drone>> entry : requests.entrySet()) {
            Location location = entry.getKey();
            List<Drone> droneList = entry.getValue();
            if (location.drone == null && !droneList.isEmpty()) {
                Drone winner = droneList.get(0);
                int bestPriority = getPriority(winner, location);
                for (Drone d : droneList) {
                    int pr = getPriority(d, location);
                    if (pr < bestPriority) {
                        bestPriority = pr;
                        winner = d;
                    }
                }
                winner.grantAccess(location);
            }
            droneList.clear();
        }
    }

    public int getPriority(Drone drone, Location location) {
        int best = Integer.MAX_VALUE;
        for (PriorityManager pm : priorityManagers) {
            best = Math.min(best, pm.getPriority(drone, location, this));
        }
        return best;
    }

    public boolean someItems() {
        return !waitingForDelivery.isEmpty();
    }
}