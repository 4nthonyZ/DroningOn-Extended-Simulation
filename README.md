# Droning On — Enhanced Drone Delivery Simulation

This project extends the **Droning On** simulation system using Java. It was developed for the final group project of **COMP20007** at the University of Melbourne.

The system simulates parcel delivery by drones from a dispatch centre to suburban homes, with time-stepped movement and location access control.

## 🔧 Key Enhancements Implemented

1. **Fragile Parcel Handling**  
   - Drones carrying fragile parcels move at half speed (skipping every other tick) until delivery.

2. **Contended Location Access Coordination**  
   - All drone movement requests per tick are cached and resolved in **reverse trip order** (clockwise), improving handling of contention.

3. **Priority-Based Access Resolution**  
   - Drones are prioritized based on parcel type and location:
     - Fragile > Heavy > South-of-target > Others  
   - Within each priority group, the earliest request is granted.

## 🧱 Technical Highlights

- **State-based tick-driven simulation** (Drone lifecycle modeled as a finite state machine)
- **Thread-safe shared location access management**
- **Preserves original simulation output when no enhancements are triggered**
- All changes integrate cleanly with the existing framework (no UI/logging disruption)


## 🧪 Sample Configs

- `test.properties`: Basic test  
- `test_fragile.properties`: Fragile parcel simulation  
- `test_contention.properties`: High-contention scenario

