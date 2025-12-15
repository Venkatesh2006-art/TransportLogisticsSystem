

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransportLogisticSystem1 {
    static List<Route> routes = new ArrayList<>();
    static List<Vehicle> vehicles = new ArrayList<>();
    static List<Allocation> allocations = new ArrayList<>();
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║      Transport Logistics System        ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        // Add sample data automatically
        addSampleData();
        
        boolean running = true;
        while(running) {
            displayMenu();
            
            if (sc.hasNextInt()) {
                int choice = sc.nextInt();
                sc.nextLine(); // ✅ BUG FIX: Consume newline after choice
                
                switch(choice) {
                    case 1 -> viewData();
                    case 2 -> addRoute(sc);
                    case 3 -> addVehicle(sc);
                    case 4 -> calculateBestMatches(sc);
                    case 5 -> viewAllocations();
                    case 6 -> {
                        System.out.println("✅ System terminated. Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("❌ Invalid choice (1-6). Please try again.");
                }
            } else {
                System.out.println("❌ Invalid input. Please enter a number (1-6).");
                sc.nextLine(); // Consume invalid token
            }
        }
        sc.close();
    }
    
    static void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("1. View Data              2. Add Route");
        System.out.println("3. Add Vehicle            4. Calculate Best Matches");
        System.out.println("5. View Allocations       6. Exit");
        System.out.print("Choice (1-6): ");
        System.out.println("=".repeat(50));
    }
    
    static void addSampleData() {
        // Sample routes
        routes.add(new Route("R1", 200.0, 100.0, "Delhi", "Mumbai"));
        routes.add(new Route("R2", 350.0, 200.0, "Mumbai", "Bangalore"));
        routes.add(new Route("R3", 800.0, 150.0, "Delhi", "Kolkata"));
        routes.add(new Route("R4", 500.0, 1100.0, "Pune", "Chennai")); // Only T002 can handle
        
        // Sample vehicles
        vehicles.add(new Truck("T001", 1000.0, 8.5, 95.0));  // Capacity 1000kg
        vehicles.add(new Van("V001", 300.0, 15.0, 92.0));    // Capacity 300kg
        vehicles.add(new Truck("T002", 1200.0, 7.0, 98.0));  // Capacity 1200kg
        
        System.out.println("✅ Sample data loaded!\n");
    }
    
    static void viewData() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("ROUTES");
        System.out.println("=".repeat(70));
        System.out.printf("%-5s | %-12s -> %-12s | %-10s | %-10s%n", 
            "ID", "Source", "Destination", "Distance", "Cargo (kg)");
        System.out.println("-".repeat(70));
        
        if(routes.isEmpty()) {
            System.out.println("No routes available.");
        } else {
            for(Route r : routes) {
                System.out.printf("%-5s | %-12s -> %-12s | %-10.1f | %-10.1f%n", 
                    r.id, r.source, r.destination, r.distance, r.cargoAmount);
            }
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("VEHICLES");
        System.out.println("=".repeat(70));
        System.out.printf("%-5s | %-10s | %-12s | %-10s | %-12s%n", 
            "ID", "Type", "Capacity (kg)", "Mileage", "Rate (₹/L)");
        System.out.println("-".repeat(70));
        
        if(vehicles.isEmpty()) {
            System.out.println("No vehicles available.");
        } else {
            for(Vehicle v : vehicles) {
                System.out.printf("%-5s | %-10s | %-12.1f | %-10.2f | %-12.2f%n", 
                    v.id, v.getType(), v.capacity, v.mileage, v.rate);
            }
        }
    }
    
    static void addRoute(Scanner sc) {
        System.out.println("\n--- Add New Route ---");
        
        System.out.print("Route ID: ");
        String id = sc.nextLine().trim();
        
        // ✅ BUG FIX: Check for duplicate IDs
        if(routes.stream().anyMatch(r -> r.id.equals(id))) {
            System.out.println("❌ Route ID already exists!");
            return;
        }
        
        System.out.print("Distance (km): ");
        if(!sc.hasNextDouble()) {
            System.out.println("❌ Invalid distance. Must be a number.");
            sc.nextLine();
            return;
        }
        double dist = sc.nextDouble();
        
        if(dist <= 0) {
            System.out.println("❌ Distance must be positive!");
            sc.nextLine();
            return;
        }
        
        System.out.print("Cargo (kg): ");
        if(!sc.hasNextDouble()) {
            System.out.println("❌ Invalid cargo. Must be a number.");
            sc.nextLine();
            return;
        }
        double cargo = sc.nextDouble();
        
        if(cargo <= 0) {
            System.out.println("❌ Cargo must be positive!");
            sc.nextLine();
            return;
        }
        
        sc.nextLine(); // ✅ BUG FIX: Consume newline
        
        System.out.print("Source City: ");
        String src = sc.nextLine().trim();
        
        System.out.print("Destination City: ");
        String dest = sc.nextLine().trim();
        
        routes.add(new Route(id, dist, cargo, src, dest));
        System.out.println("✅ Route added successfully!");
    }
    
    static void addVehicle(Scanner sc) {
        System.out.println("\n--- Add New Vehicle ---");
        
        System.out.print("Vehicle Type (Truck/Van): ");
        String type = sc.nextLine().trim();
        
        if(!type.equalsIgnoreCase("Truck") && !type.equalsIgnoreCase("Van")) {
            System.out.println("❌ Type must be 'Truck' or 'Van'!");
            return;
        }
        
        System.out.print("Vehicle ID: ");
        String id = sc.nextLine().trim();
        
        // ✅ BUG FIX: Check for duplicate IDs
        if(vehicles.stream().anyMatch(v -> v.id.equals(id))) {
            System.out.println("❌ Vehicle ID already exists!");
            return;
        }
        
        System.out.print("Capacity (kg): ");
        if(!sc.hasNextDouble()) {
            System.out.println("❌ Invalid capacity. Must be a number.");
            sc.nextLine();
            return;
        }
        double cap = sc.nextDouble();
        
        if(cap <= 0) {
            System.out.println("❌ Capacity must be positive!");
            sc.nextLine();
            return;
        }
        
        System.out.print("Mileage (km/L): ");
        if(!sc.hasNextDouble()) {
            System.out.println("❌ Invalid mileage. Must be a number.");
            sc.nextLine();
            return;
        }
        double mile = sc.nextDouble();
        
        if(mile <= 0) {
            System.out.println("❌ Mileage must be positive!");
            sc.nextLine();
            return;
        }
        
        System.out.print("Rate (₹/L fuel): ");
        if(!sc.hasNextDouble()) {
            System.out.println("❌ Invalid rate. Must be a number.");
            sc.nextLine();
            return;
        }
        double rate = sc.nextDouble();
        
        if(rate <= 0) {
            System.out.println("❌ Rate must be positive!");
            sc.nextLine();
            return;
        }
        
        sc.nextLine(); // ✅ BUG FIX: Consume newline
        
        if(type.equalsIgnoreCase("Truck"))
            vehicles.add(new Truck(id, cap, mile, rate));
        else
            vehicles.add(new Van(id, cap, mile, rate));
            
        System.out.println("✅ Vehicle added successfully!");
    }
    
    /**
     * Calculates the transportation cost for a route using a specific vehicle.
     * 
     * Formula: Cost = (Distance / Mileage) × Rate per liter
     * Where:
     *   - Distance: Total route distance in km
     *   - Mileage: Vehicle fuel efficiency in km/L
     *   - Rate: Cost per liter of fuel in ₹
     * 
     * @param r Route object containing distance
     * @param v Vehicle object containing mileage and rate
     * @return Total cost in ₹
     */
    static double calculateCost(Route r, Vehicle v) {
        // Fuel needed (Liters) = Distance (km) / Mileage (km/L)
        double fuelNeeded = r.distance / v.mileage;
        // Total cost = Fuel needed × Rate per liter
        return fuelNeeded * v.rate;
    }
    
    /**
     * Finds the best-suited, lowest-cost vehicle for each route.
     * Uses PriorityQueue to efficiently sort by cost (min-heap).
     * Only considers vehicles with sufficient capacity.
     */
    static void calculateBestMatches(Scanner sc) {
        if(routes.isEmpty() || vehicles.isEmpty()) {
            System.out.println("❌ Need at least one route and one vehicle!");
            return;
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("ROUTE PLANNING - BEST MATCHES");
        System.out.println("=".repeat(70));
        
        int allocationCount = 0;
        
        // Loop through every route
        for (Route r : routes) {
            // ✅ BUG FIX: Use PriorityQueue to sort by cost
            PriorityQueue<CostPair> bestMatches = new PriorityQueue<>();
            
            // Check every vehicle for this route
            for (Vehicle v : vehicles) {
                // Capacity Constraint: Vehicle must carry the cargo
                if (v.capacity >= r.cargoAmount) {
                    double cost = calculateCost(r, v);
                    bestMatches.add(new CostPair(cost, r, v));
                }
            }
            
            System.out.println("\n" + "-".repeat(70));
            System.out.printf("Route %s: %s → %s | Distance: %.1f km | Cargo: %.1f kg%n", 
                r.id, r.source, r.destination, r.distance, r.cargoAmount);
            System.out.println("-".repeat(70));
            
            if (bestMatches.isEmpty()) {
                System.out.println("❌ ERROR: No vehicle has sufficient capacity for this route.");
                System.out.printf("   Required Capacity: %.1f kg (but all vehicles have less)%n", r.cargoAmount);
            } else {
                // Extract top 2 matches
                List<CostPair> topMatches = new ArrayList<>();
                while(!bestMatches.isEmpty() && topMatches.size() < 2) {
                    topMatches.add(bestMatches.poll());
                }
                
                // Display Best Match
                CostPair best = topMatches.get(0);
                System.out.printf("✅ BEST MATCH: %s (%s)%n", best.vehicle.id, best.vehicle.getType());
                System.out.printf("   Transportation Cost: ₹%.2f%n", best.cost);
                System.out.printf("   Vehicle Capacity: %.1f kg | Surplus: %.1f kg%n", 
                    best.vehicle.capacity, 
                    best.vehicle.capacity - r.cargoAmount);
                System.out.printf("   Fuel Needed: %.2f L | Mileage: %.2f km/L | Rate: ₹%.2f/L%n",
                    r.distance / best.vehicle.mileage,
                    best.vehicle.mileage,
                    best.vehicle.rate);
                
                // Display Next Best Match (if available)
                if (topMatches.size() > 1) {
                    CostPair nextBest = topMatches.get(1);
                    System.out.printf("   Next Best: %s (%s) - Cost: ₹%.2f%n", 
                        nextBest.vehicle.id, nextBest.vehicle.getType(), nextBest.cost);
                }
                
                allocationCount++;
                
                // Option to confirm allocation
                System.out.print("   Confirm this allocation? (y/n): ");
                String confirm = sc.nextLine().trim().toLowerCase();
                
                if(confirm.equals("y")) {
                    Allocation alloc = new Allocation(best.route, best.vehicle, best.cost);
                    allocations.add(alloc);
                    System.out.printf("   ✅ Allocation saved (ID: %d)%n", alloc.allocationId);
                }
            }
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.printf("Summary: Analyzed %d route(s), Found matches for %d route(s)%n", 
            routes.size(), allocationCount);
    }
    
    /**
     * Displays allocation history and revenue statistics
     */
    static void viewAllocations() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("ALLOCATION HISTORY");
        System.out.println("=".repeat(70));
        
        if(allocations.isEmpty()) {
            System.out.println("No allocations yet.");
        } else {
            System.out.printf("%-6s | %-8s | %-8s | %-12s | %-20s%n", 
                "ID", "Route", "Vehicle", "Cost (₹)", "Timestamp");
            System.out.println("-".repeat(70));
            
            for(Allocation a : allocations) {
                System.out.printf("%-6d | %-8s | %-8s | %-12.2f | %-20s%n",
                    a.allocationId, a.route.id, a.vehicle.id, a.cost, a.timestamp);
            }
            
            System.out.println("-".repeat(70));
            double totalRevenue = allocations.stream()
                    .mapToDouble(a -> a.cost)
                    .sum();
            System.out.printf("Total Revenue: ₹%.2f | Total Allocations: %d%n", 
                totalRevenue, allocations.size());
            System.out.printf("Average Cost per Route: ₹%.2f%n", 
                totalRevenue / allocations.size());
        }
    }
}

// ==================== DATA CLASSES ====================

/**
 * Represents a shipping route with source, destination, distance, and cargo.
 */
class Route {
    String id, source, destination;
    double distance, cargoAmount;
    
    Route(String id, double d, double c, String s, String dest) {
        this.id = id;
        this.distance = d;
        this.cargoAmount = c;
        this.source = s;
        this.destination = dest;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Route) return ((Route)obj).id.equals(id);
        return false;
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

// ==================== VEHICLE HIERARCHY ====================

/**
 * Abstract base class for vehicles in the logistics system.
 */
abstract class Vehicle {
    String id;
    double capacity, mileage, rate;
    
    Vehicle(String id, double c, double m, double r) {
        this.id = id;
        this.capacity = c;
        this.mileage = m;
        this.rate = r;
    }
    
    abstract String getType();
}

/**
 * Truck implementation - high capacity, lower mileage
 */
class Truck extends Vehicle {
    Truck(String id, double c, double m, double r) { 
        super(id, c, m, r); 
    }
    
    @Override
    String getType() { 
        return "Truck"; 
    }
}

/**
 * Van implementation - lower capacity, higher mileage
 */
class Van extends Vehicle {
    Van(String id, double c, double m, double r) { 
        super(id, c, m, r); 
    }
    
    @Override
    String getType() { 
        return "Van"; 
    }
}

// ==================== COST ALLOCATION ====================

/**
 * Represents a potential route-vehicle pairing with associated cost.
 * Implements Comparable for PriorityQueue sorting by cost.
 */
class CostPair implements Comparable<CostPair> {
    double cost;
    Route route;
    Vehicle vehicle;
    
    CostPair(double c, Route r, Vehicle v) { 
        this.cost = c;
        this.route = r;
        this.vehicle = v;
    }
    
    /**
     * Compares by cost: lowest cost comes first (min-heap for PriorityQueue)
     */
    @Override
    public int compareTo(CostPair other) {
        return Double.compare(this.cost, other.cost);
    }
}

// ==================== ALLOCATION RECORD ====================

/**
 * Records a confirmed route-vehicle allocation with cost and timestamp.
 */
class Allocation {
    static int counter = 1001;
    int allocationId;
    Route route;
    Vehicle vehicle;
    double cost;
    String timestamp;
    
    Allocation(Route r, Vehicle v, double c) {
        this.allocationId = counter++;
        this.route = r;
        this.vehicle = v;
        this.cost = c;
        this.timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}