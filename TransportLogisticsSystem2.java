import java.util.*;

public class TransportLogisticsSystem2 {
    static List<Route> routes = new ArrayList<>();
    static List<Vehicle> vehicles = new ArrayList<>();
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Transport Logistics System ===");
        
        // Add sample data automatically (no CSV needed)
        addSampleData();
        
        while(true) {
            // Updated menu with option 5
            System.out.print("\n1.View Data  2.Add Route  3.Add Vehicle    4.Calculate Best Matches:   5.Exit ");
            if (sc.hasNextInt()) { // Check if the next input is an integer
                int choice = sc.nextInt();
                
                if(choice == 1) viewData();
                else if(choice == 2) addRoute(sc);
                else if(choice == 3) addVehicle(sc);
                else if(choice == 4) calculateBestMatches();
                else if(choice == 5) break;
                else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number (1-5).");
                sc.next(); // Consume the invalid token
            }
        }
        sc.close();
        System.out.println("System terminated.");
    }
    
    static void addSampleData() {
        // Sample routes
        routes.add(new Route("R1", 200.0, 100.0, "Delhi", "Mumbai"));
        routes.add(new Route("R2", 350.0, 200.0, "Mumbai", "Bangalore"));
        routes.add(new Route("R3", 80.0, 800.0, "Delhi", "Kolkata"));
        // Route R4 requires 1100kg, only T002 can handle it
        routes.add(new Route("R4", 500.0, 1100.0, "Pune", "Chennai"));
        
        // Sample vehicles
        vehicles.add(new Truck("T001", 1000.0, 8.5, 95.0)); // Capacity 1000kg
        vehicles.add(new Van("V001", 300.0, 15.0, 92.0));   // Capacity 300kg
        vehicles.add(new Truck("T002", 1200.0, 7.0, 98.0)); // Capacity 1200kg
        
        System.out.println("Sample data loaded!");
    }
    
    static void viewData() {
        System.out.println("\nRoutes:");
        System.out.printf("%-5s | %-10s -> %-10s | %-8s | %-8s%n", "ID", "Source", "Destination", "Distance", "Cargo");
        System.out.println("---------------------------------------------------------");
        for(Route r : routes) {
            System.out.printf("%-5s | %-10s -> %-10s | %-8.1f | %-8.1f%n", 
                r.id, r.source, r.destination, r.distance, r.cargoAmount);
        }
        System.out.println("\nVehicles:");
        System.out.printf("%-5s | %-8s | %-8s | %-8s | %-8s%n", "ID", "Type", "Capacity", "Mileage", "Rate/L");
        System.out.println("----------------------------------------------");
        for(Vehicle v : vehicles) {
            System.out.printf("%-5s | %-8s | %-8.1f | %-8.1f | %-8.1f%n", 
                v.id, v.getType(), v.capacity, v.mileage, v.rate);
        }
    }
    
    static void addRoute(Scanner sc) {
        // Added nextLine() cleanup to handle scanner input issues
        sc.nextLine(); 
        System.out.print("ID: "); String id = sc.nextLine();
        System.out.print("Distance (km): "); double dist = sc.nextDouble();
        System.out.print("Cargo (kg): "); double cargo = sc.nextDouble();
        sc.nextLine(); // Consume newline
        System.out.print("Source: "); String src = sc.nextLine();
        System.out.print("Destination: "); String dest = sc.nextLine();
        routes.add(new Route(id, dist, cargo, src, dest));
        System.out.println("Route added!");
    }
    
    static void addVehicle(Scanner sc) {
        // Added nextLine() cleanup to handle scanner input issues
        sc.nextLine();
        System.out.print("Type (Truck/Van): "); String type = sc.nextLine();
        System.out.print("ID: "); String id = sc.nextLine();
        System.out.print("Capacity (kg): "); double cap = sc.nextDouble();
        System.out.print("Mileage (km/L): " ); double mile = sc.nextDouble();
        System.out.print("Rate (/L fuel): "); double rate = sc.nextDouble();
        
        if(type.equalsIgnoreCase("Truck"))
            vehicles.add(new Truck(id, cap, mile, rate));
        else
            vehicles.add(new Van(id, cap, mile, rate));
        System.out.println("Vehicle added!");
    }
    
    /**
     * Calculates the cost for a given route and vehicle.
     * Cost = (Total Distance / Vehicle Mileage) * Rate per liter of fuel
     */
    static double calculateCost(Route r, Vehicle v) {
        // Total fuel needed (Liters) = Distance (km) / Mileage (km/L)
        double fuelNeeded = r.distance / v.mileage;
        return fuelNeeded * v.rate; 
    }
    
    /**
     * Finds the best-suited, lowest-cost vehicle for each route.
     */
    static void calculateBestMatches() {
        System.out.println("\n=== Route Planning (Best Match) ===");
        
        // Loop through every route
        for (Route r : routes) {
            // Use a PriorityQueue to automatically keep track of the lowest cost match
            // The CostPair class ensures sorting by the lowest cost first.
            PriorityQueue<CostPair> bestMatches = new PriorityQueue<>();
            
            // Loop through every available vehicle
            for (Vehicle v : vehicles) {
                // Capacity Constraint Check:
                if (v.capacity >= r.cargoAmount) {
                    double cost = calculateCost(r, v);
                    bestMatches.add(new CostPair(cost, r, v));
                }
            }
            
            System.out.println("---");
            System.out.printf("Route %s (%s -> %s | Cargo: %.1f kg)%n", 
                r.id, r.source, r.destination, r.cargoAmount);
            
            if (bestMatches.isEmpty()) {
                System.out.println("  > ERROR: No vehicle has sufficient capacity for this route.");
            } else {
                // The head of the PriorityQueue is the lowest cost match
                CostPair best = bestMatches.poll();
                
                // Display the best match
                System.out.printf("  > Best Match: %s (%s)%n", best.vehicle.id, best.vehicle.getType());
                System.out.printf("  > Total Cost: $%.2f%n", best.cost);
                System.out.printf("  > Surplus Capacity: %.1f kg%n", (best.vehicle.capacity - r.cargoAmount));
                
                // Display the next best match (if available)
                if (!bestMatches.isEmpty()) {
                     CostPair nextBest = bestMatches.poll();
                     System.out.printf("  > Next Best Match: %s (Cost: $%.2f)%n", nextBest.vehicle.id, nextBest.cost);
                }
            }
        }
    }
    
    
}

// Data classes (same as before)
class Route {
    String id, source, destination;
    double distance, cargoAmount;
    Route(String id, double d, double c, String s, String dest) {
        this.id = id; this.distance = d; this.cargoAmount = c;
        this.source = s; this.destination = dest;
    }
    
    @Override public boolean equals(Object obj) {
        if(obj instanceof Route) return ((Route)obj).id.equals(id);
        return false;
    }
}

abstract class Vehicle {
    String id; double capacity, mileage, rate;
    Vehicle(String id, double c, double m, double r) {
        this.id = id; this.capacity = c; this.mileage = m; this.rate = r;
    }
    abstract String getType();
}

class Truck extends Vehicle {
    Truck(String id, double c, double m, double r) { super(id,c,m,r); }
    String getType() { return "Truck"; }
}

class Van extends Vehicle {
    Van(String id, double c, double m, double r) { super(id,c,m,r); }
    String getType() { return "Van"; }
}

class CostPair implements Comparable<CostPair> {
    double cost; Route route; Vehicle vehicle;
    CostPair(double c, Route r, Vehicle v) { 
        cost = c; route = r; vehicle = v; 
    }
    // Compares based on cost for the PriorityQueue: lowest cost comes first (min-heap)
    public int compareTo(CostPair other) {
        return Double.compare(cost, other.cost);
    }
}