package drone;

import java.util.List;

public class Operation implements Simulation.Tickable {
    private final DispatchCentre dispatchCentre;

    public Operation(DispatchCentre dispatchCentre) {
        this.dispatchCentre = dispatchCentre;
        Simulation.register(this);
    }

    @Override
    public void tick() {
        // Check if there are any pending packages
        if (dispatchCentre.someItems()) {
            System.out.println("Processing pending parcels...");
        }

        // Check if all drones have returned
        if (dispatchCentre.allDronesBack()) {
            System.out.println("All drones are back at the DispatchCentre.");
        }
    }
}