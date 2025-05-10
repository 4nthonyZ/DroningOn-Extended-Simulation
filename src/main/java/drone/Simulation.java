package drone;

import parcel.Parcel;
import parcel.ParcelFactory;
import suburb.SuburbManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

// [Main Controller] Time-stepped simulation for drone-based parcel delivery
public class Simulation {
    static Logger logger;

    // [Tickable Interface] All simulation entities that update each tick
    interface Tickable {
        void tick();
    }

    static final List<Tickable> entities = new ArrayList<>();
    static void register(Tickable entity) { entities.add(entity); }

    static final Map<Integer, List<Parcel>> waitingToArrive = new HashMap<>();
    static int time = 0;
    final int endArrival;
    final DispatchCentre dispatchCentre;
    static int timeout;

    static int deliveredCount = 0;
    static int deliveredTotalTime = 0;

    // [Called by Drone] Logs a successful delivery and updates delivery stats
    public static void deliver(Parcel parcel) {
        String s = "Delivered: " + parcel;
        System.out.println(s);
        logger.logEvent("%5d: %s\n", Simulation.now(), s);
        deliveredCount++;
        deliveredTotalTime += now() - parcel.myArrival();
    }

    // [Add generated parcel to arrival map]
    public void addToArrivals(int arrivalTime, Parcel item) {
        if (waitingToArrive.containsKey(arrivalTime)) {
            waitingToArrive.get(arrivalTime).add(item);
        } else {
            LinkedList<Parcel> items = new LinkedList<>();
            items.add(item);
            waitingToArrive.put(arrivalTime, items);
        }
    }

    // [Simulation Constructor] Loads all parameters from config and initializes objects
    public Simulation(Properties properties) {
        int seed = Integer.parseInt(properties.getProperty("seed", "30006"));
        this.endArrival = Integer.parseInt(properties.getProperty("parcel.endarrival", "5"));
        int numParcels = Integer.parseInt(properties.getProperty("parcel.parcels", "4"));
        int maxWeight = Integer.parseInt(properties.getProperty("parcel.maxweight", "15"));
        int weightThreshold = Integer.parseInt(properties.getProperty("parcel.weightthreshold", "12"));
        int fragile1in = Integer.parseInt(properties.getProperty("parcel.fragile1in", "1000")); // [Added] fragile parcel probability
        int timeToSuburb = Integer.parseInt(properties.getProperty("parcel.timetosuburb", "4"));
        int numStreets = Integer.parseInt(properties.getProperty("suburb.streets", "2"));
        int numHouses = Integer.parseInt(properties.getProperty("suburb.housesperstreet", "3"));
        int numdrones = Integer.parseInt(properties.getProperty("drone.number", "1"));
        timeout = Integer.parseInt(properties.getProperty("timeout", "600"));
        String logfile = properties.getProperty("logfile", "logfile.txt");

        logger = new Logger(logfile);
        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        logger.logEvent("%5d: Simulation parameters - time=%s  seed=%d\n", now(), formattedDate, seed);

        Random random = new Random(seed);
        SuburbManager manager = new SuburbManager(numStreets, numHouses);

        // [Modified in your design] DispatchCentre receives ParcelManager internally
        dispatchCentre = new DispatchCentre(manager, timeToSuburb, numdrones, weightThreshold);

        // [Added] Observer to track delivery progress (optional)
        new Operation(dispatchCentre);

        // [Parcel Generation] Randomly create parcels, some may be fragile
        for (int i = 0; i < numParcels; i++) {
            int arrivalTime = random.nextInt(endArrival) + 1;
            int street = random.nextInt(manager.getNumStreets()) + 1;
            int house = random.nextInt(manager.getNumHouses()) + 1;
            int weight = random.nextInt(maxWeight) + 1;
            boolean fragile = random.nextInt(fragile1in) == 0; // [fragile logic for requirement (1)]
            addToArrivals(arrivalTime, ParcelFactory.createParcel(street, house, arrivalTime, weight, fragile));
        }
    }

    public static int now() { return time; }

    // [One Simulation Step] Processes arrivals and updates all entities
    public void step() {
        // External parcel arrivals
        if (waitingToArrive.containsKey(time))
            dispatchCentre.arrive(waitingToArrive.get(time));

        // Tick all active entities (e.g., drones, Operation, DispatchCentre)
        for (Tickable entity : entities) {
            entity.tick();
        }
    }

    // [Main Simulation Loop]
    public void run() {
        while (time++ <= endArrival || dispatchCentre.someItems() || !dispatchCentre.allDronesBack()) {
            step();
            try {
                TimeUnit.MILLISECONDS.sleep(timeout); // Optional slowdown
            } catch (InterruptedException e) {
                // Ignore interruption
            }
        }

        // Final delivery summary
        logger.logEvent("%5d Finished: Items delivered = %d; Average time for delivery = %.2f%n",
                now(), deliveredCount, (float) deliveredTotalTime / deliveredCount);
    }
}
