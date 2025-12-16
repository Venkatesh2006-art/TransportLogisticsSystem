import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.nio.file.*;

public class TransportLogisticSystem2 {
    static List<Route> routes = new ArrayList<>();
    static List<Vehicle> vehicles = new ArrayList<>();
    static List<Allocation> allocations = new ArrayList<>();
    static List<MultiStopDelivery> multiStopDeliveries = new ArrayList<>();
    
    static final String VEHICLES_CSV = "Vehicle.csv";
    static final String ROUTES_CSV = "outes.csv";
    static final String ALLOCATIONS_CSV = "allocations.csv";
    static final String MULTI_DELIVERY_CSV = "multi_stop_deliveries.csv";
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║      Transport Logistics System        ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        loadDataFromCSV();
        
        boolean running = true;
        while(running) {
            displayMenu();
            
            if (sc.hasNextInt()) {
                int choice = sc.nextInt();
                sc.nextLine();
                
                switch(choice) {
                    case 1 -> viewData();
                    case 2 -> addRoute(sc);
                    case 3 -> addVehicle(sc);
                    case 4 -> calculateBestMatches(sc);
                    case 5 -> createMultiStopDelivery(sc);
                    case 6 -> viewAllocations();
                    case 7 -> viewMultiStopDeliveries();
                    case 8 -> {
                        saveAllDataToCSV();
                        System.out.println("✅ Data saved to CSV files. System terminated. Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("❌ Invalid choice (1-8). Please try again.");
                }
            } else {
                System.out.println("❌ Invalid input. Please enter a number (1-8).");
                sc.nextLine();
            }
        }
        sc.close();
    }
    
    static void displayMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("1. View Data                  2. Add Route");
        System.out.println("3. Add Vehicle               4. Calculate Best Matches");
        System.out.println("5. Multi-Stop Delivery       6. View Allocations");
        System.out.println("7. View Multi-Stop History   8. Exit & Save");
        System.out.println("Choice (1-8): ");
        System.out.println("=".repeat(60));
    }
    
    static void viewData() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ROUTES");
        System.out.println("=".repeat(80));
        System.out.printf("%-6s | %-15s -> %-15s | %-10s | %-10s%n", 
            "ID", "Source", "Destination", "Distance", "Cargo (kg)");
        System.out.println("-".repeat(80));
        
        if(routes.isEmpty()) {
            System.out.println("No routes available.");
        } else {
            for(Route r : routes) {
                System.out.printf("%-6s | %-15s -> %-15s | %-10.1f | %-10.1f%n", 
                    r.id, r.source, r.destination, r.distance, r.cargoAmount);
            }
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("VEHICLES");
        System.out.println("=".repeat(80));
        System.out.printf("%-6s | %-10s | %-12s | %-10s | %-12s%n", 
            "ID", "Type", "Capacity (kg)", "Mileage", "Rate (₹/L)");
        System.out.println("-".repeat(80));
        
        if(vehicles.isEmpty()) {
            System.out.println("No vehicles available.");
        } else {
            for(Vehicle v : vehicles) {
                System.out.printf("%-6s | %-10s | %-12.1f | %-10.2f | %-12.2f%n", 
                    v.id, v.getType(), v.capacity, v.mileage, v.rate);
            }
        }
    }
    
    static void addRoute(Scanner sc) {
        System.out.println("\n--- Add New Route ---");
        
        System.out.print("Route ID: ");
        String id = sc.nextLine().trim();
        
        if(routes.stream().anyMatch(r -> r.id.equals(id))) {
            System.out.println("❌ Route ID already exists!");
            return;
        }
        
        System.out.print("Source City: ");
        String src = sc.nextLine().trim();
        
        System.out.print("Destination City: ");
        String dest = sc.nextLine().trim();
        
        System.out.print("Distance (km): ");
        if(!sc.hasNextDouble()) {
            System.out.println("❌ Invalid distance.");
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
            System.out.println("❌ Invalid cargo.");
            sc.nextLine();
            return;
        }
        double cargo = sc.nextDouble();
        sc.nextLine();
        
        if(cargo <= 0) {
            System.out.println("❌ Cargo must be positive!");
            return;
        }
        
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
        
        if(vehicles.stream().anyMatch(v -> v.id.equals(id))) {
            System.out.println("❌ Vehicle ID already exists!");
            return;
        }
        
        System.out.print("Capacity (kg): ");
        if(!sc.hasNextDouble()) {
            System.out.println("❌ Invalid capacity.");
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
            System.out.println("❌ Invalid mileage.");
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
            System.out.println("❌ Invalid rate.");
            sc.nextLine();
            return;
        }
        double rate = sc.nextDouble();
        sc.nextLine();
        
        if(rate <= 0) {
            System.out.println("❌ Rate must be positive!");
            return;
        }
        
        if(type.equalsIgnoreCase("Truck"))
            vehicles.add(new Truck(id, cap, mile, rate));
        else
            vehicles.add(new Van(id, cap, mile, rate));
            
        System.out.println("✅ Vehicle added successfully!");
    }
    
    static void createMultiStopDelivery(Scanner sc) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("MULTI-STOP DELIVERY - ONE VEHICLE, MULTIPLE LOCATIONS");
        System.out.println("=".repeat(70));
        
        if(vehicles.isEmpty()) {
            System.out.println("❌ Add at least one vehicle first!");
            return;
        }
        
        System.out.println("\n📍 Available Vehicles:");
        for(int i = 0; i < vehicles.size(); i++) {
            Vehicle v = vehicles.get(i);
            System.out.printf("%d. %s (%s) - Capacity: %.0f kg, Mileage: %.2f km/L%n", 
                i+1, v.id, v.getType(), v.capacity, v.mileage);
        }
        
        System.out.print("\nSelect vehicle (1-" + vehicles.size() + "): ");
        if(!sc.hasNextInt()) {
            System.out.println("❌ Invalid vehicle selection!");
            sc.nextLine();
            return;
        }
        int vehicleChoice = sc.nextInt();
        sc.nextLine();
        
        if(vehicleChoice < 1 || vehicleChoice > vehicles.size()) {
            System.out.println("❌ Invalid vehicle selection!");
            return;
        }
        
        Vehicle selectedVehicle = vehicles.get(vehicleChoice - 1);
        
        System.out.print("\nDelivery ID: ");
        String deliveryId = sc.nextLine().trim();
        
        if(deliveryId.isEmpty()) {
            System.out.println("❌ Delivery ID cannot be empty!");
            return;
        }
        
        List<Stop> stops = new ArrayList<>();
        double totalCargo = 0;
        double totalDistance = 0;
        
        System.out.println("\n🗺 Add delivery stops (enter empty city name to finish):");
        int stopCount = 1;
        
        while(true) {
            System.out.print("\nStop " + stopCount + " - City: ");
            String city = sc.nextLine().trim();
            
            if(city.isEmpty()) {
                if(stops.isEmpty()) {
                    System.out.println("❌ Must add at least one stop!");
                    continue;
                }
                break;
            }
            
            System.out.print("Distance from previous (km): ");
            if(!sc.hasNextDouble()) {
                System.out.println("❌ Invalid distance!");
                sc.nextLine();
                continue;
            }
            double distance = sc.nextDouble();
            
            System.out.print("Cargo to deliver (kg): ");
            if(!sc.hasNextDouble()) {
                System.out.println("❌ Invalid cargo!");
                sc.nextLine();
                continue;
            }
            double cargo = sc.nextDouble();
            sc.nextLine();
            
            if(distance <= 0 || cargo <= 0) {
                System.out.println("❌ Distance and cargo must be positive!");
                continue;
            }
            
            totalCargo += cargo;
            totalDistance += distance;
            
            if(totalCargo > selectedVehicle.capacity) {
                System.out.printf("❌ Total cargo (%.0f kg) exceeds vehicle capacity (%.0f kg)!%n", 
                    totalCargo, selectedVehicle.capacity);
                totalCargo -= cargo;
                totalDistance -= distance;
                continue;
            }
            
            stops.add(new Stop(city, distance, cargo));
            stopCount++;
            System.out.printf("✅ Stop added | Total: %.0f kg, %.1f km%n", totalCargo, totalDistance);
        }
        
        double cost = calculateMultiStopCost(selectedVehicle, totalDistance);
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📋 MULTI-STOP DELIVERY SUMMARY");
        System.out.println("=".repeat(70));
        System.out.printf("Delivery ID: %s%n", deliveryId);
        System.out.printf("Vehicle: %s (%s)%n", selectedVehicle.id, selectedVehicle.getType());
        System.out.printf("Total Stops: %d%n", stops.size());
        System.out.printf("Total Cargo: %.0f kg / %.0f kg capacity%n", totalCargo, selectedVehicle.capacity);
        System.out.printf("Total Distance: %.1f km%n", totalDistance);
        System.out.printf("Fuel Needed: %.2f L%n", totalDistance / selectedVehicle.mileage);
        System.out.printf("Estimated Cost: ₹%.2f%n", cost);
        System.out.println("=".repeat(70));
        
        System.out.print("\nConfirm this multi-stop delivery? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        
        if(confirm.equals("y")) {
            MultiStopDelivery delivery = new MultiStopDelivery(deliveryId, selectedVehicle, stops, totalCargo, totalDistance, cost);
            multiStopDeliveries.add(delivery);
            System.out.println("✅ Multi-stop delivery saved successfully!");
        }
    }
    
    static double calculateCost(Route r, Vehicle v) {
        double fuelNeeded = r.distance / v.mileage;
        return fuelNeeded * v.rate;
    }
    
    static double calculateMultiStopCost(Vehicle v, double totalDistance) {
        double fuelNeeded = totalDistance / v.mileage;
        return fuelNeeded * v.rate;
    }
    
    static void calculateBestMatches(Scanner sc) {
        if(routes.isEmpty() || vehicles.isEmpty()) {
            System.out.println("❌ Need at least one route and one vehicle!");
            return;
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ROUTE PLANNING - BEST MATCHES");
        System.out.println("=".repeat(80));
        
        for (Route r : routes) {
            PriorityQueue<CostPair> bestMatches = new PriorityQueue<>();
            
            for (Vehicle v : vehicles) {
                if (v.capacity >= r.cargoAmount) {
                    double cost = calculateCost(r, v);
                    bestMatches.add(new CostPair(cost, r, v));
                }
            }
            
            System.out.println("\n" + "-".repeat(80));
            System.out.printf("Route %s: %s → %s | Distance: %.1f km | Cargo: %.1f kg%n", 
                r.id, r.source, r.destination, r.distance, r.cargoAmount);
            System.out.println("-".repeat(80));
            
            if (bestMatches.isEmpty()) {
                System.out.println("❌ ERROR: No vehicle has sufficient capacity!");
            } else {
                CostPair best = bestMatches.poll();
                System.out.printf("✅ BEST MATCH: %s (%s)%n", best.vehicle.id, best.vehicle.getType());
                System.out.printf("   Cost: ₹%.2f | Capacity: %.1f kg%n", best.cost, best.vehicle.capacity);
                
                System.out.print("   Confirm allocation? (y/n): ");
                String confirm = sc.nextLine().trim().toLowerCase();
                
                if(confirm.equals("y")) {
                    allocations.add(new Allocation(best.route, best.vehicle, best.cost));
                    System.out.println("   ✅ Allocation saved!");
                }
            }
        }
    }
    
    static void viewAllocations() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ALLOCATION HISTORY");
        System.out.println("=".repeat(80));
        
        if(allocations.isEmpty()) {
            System.out.println("No allocations yet.");
        } else {
            System.out.printf("%-6s | %-8s | %-8s | %-12s | %-20s%n", 
                "ID", "Route", "Vehicle", "Cost (₹)", "Timestamp");
            System.out.println("-".repeat(80));
            
            for(Allocation a : allocations) {
                System.out.printf("%-6d | %-8s | %-8s | %-12.2f | %-20s%n",
                    a.allocationId, a.route.id, a.vehicle.id, a.cost, a.timestamp);
            }
            
            System.out.println("-".repeat(80));
            double totalRevenue = allocations.stream()
                    .mapToDouble(a -> a.cost)
                    .sum();
            System.out.printf("Total Revenue: ₹%.2f | Allocations: %d | Avg: ₹%.2f%n", 
                totalRevenue, allocations.size(), totalRevenue / allocations.size());
        }
    }
    
    static void viewMultiStopDeliveries() {
        System.out.println("\n" + "=".repeat(90));
        System.out.println("MULTI-STOP DELIVERY HISTORY");
        System.out.println("=".repeat(90));
        
        if(multiStopDeliveries.isEmpty()) {
            System.out.println("No multi-stop deliveries yet.");
        } else {
            for(MultiStopDelivery d : multiStopDeliveries) {
                System.out.printf("\n📦 Delivery ID: %s | Vehicle: %s | Stops: %d%n", 
                    d.deliveryId, d.vehicle.id, d.stops.size());
                System.out.println("-".repeat(90));
                
                for(int i = 0; i < d.stops.size(); i++) {
                    Stop stop = d.stops.get(i);
                    System.out.printf("   Stop %d: %s | Distance: %.1f km | Cargo: %.0f kg%n", 
                        i+1, stop.city, stop.distance, stop.cargo);
                }
                
                System.out.printf("\n   Total Distance: %.1f km | Total Cargo: %.0f kg | Cost: ₹%.2f%n", 
                    d.totalDistance, d.totalCargo, d.cost);
                System.out.printf("   Timestamp: %s%n", d.timestamp);
            }
        }
        System.out.println("\n" + "=".repeat(90));
    }
    
    // ==================== CSV FILE OPERATIONS ====================
    
    static void loadDataFromCSV() {
        loadVehiclesFromCSV();
        loadRoutesFromCSV();
        loadAllocationsFromCSV();
        loadMultiStopDeliveriesFromCSV();
        if(vehicles.size() > 0 || routes.size() > 0) {
            System.out.println("✅ Data loaded from CSV files\n");
        }
    }
    
    static void loadVehiclesFromCSV() {
        try {
            if (!Files.exists(Paths.get(VEHICLES_CSV))) return;
            
            List<String> lines = Files.readAllLines(Paths.get(VEHICLES_CSV));
            for(int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if(parts.length >= 5) {
                    try {
                        String id = parts[0].trim();
                        String type = parts[1].trim();
                        double cap = Double.parseDouble(parts[2].trim());
                        double mile = Double.parseDouble(parts[3].trim());
                        double rate = Double.parseDouble(parts[4].trim());
                        
                        if(type.equalsIgnoreCase("Truck"))
                            vehicles.add(new Truck(id, cap, mile, rate));
                        else
                            vehicles.add(new Van(id, cap, mile, rate));
                    } catch(NumberFormatException e) {
                        System.out.println("⚠ Skipping invalid vehicle record: " + lines.get(i));
                    }
                }
            }
        } catch(IOException e) {
            // File doesn't exist yet, which is fine
        }
    }
    
    static void loadRoutesFromCSV() {
        try {
            if (!Files.exists(Paths.get(ROUTES_CSV))) return;
            
            List<String> lines = Files.readAllLines(Paths.get(ROUTES_CSV));
            for(int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if(parts.length >= 5) {
                    try {
                        String id = parts[0].trim();
                        double dist = Double.parseDouble(parts[1].trim());
                        double cargo = Double.parseDouble(parts[2].trim());
                        String src = parts[3].trim();
                        String dest = parts[4].trim();
                        routes.add(new Route(id, dist, cargo, src, dest));
                    } catch(NumberFormatException e) {
                        System.out.println("⚠ Skipping invalid route record: " + lines.get(i));
                    }
                }
            }
        } catch(IOException e) {
            // File doesn't exist yet, which is fine
        }
    }
    
    static void loadAllocationsFromCSV() {
        try {
            if (!Files.exists(Paths.get(ALLOCATIONS_CSV))) return;
            
            List<String> lines = Files.readAllLines(Paths.get(ALLOCATIONS_CSV));
            for(int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if(parts.length >= 4) {
                    try {
                        String routeId = parts[1].trim();
                        String vehicleId = parts[2].trim();
                        double cost = Double.parseDouble(parts[3].trim());
                        
                        Route route = routes.stream().filter(r -> r.id.equals(routeId)).findFirst().orElse(null);
                        Vehicle vehicle = vehicles.stream().filter(v -> v.id.equals(vehicleId)).findFirst().orElse(null);
                        
                        if(route != null && vehicle != null) {
                            allocations.add(new Allocation(route, vehicle, cost));
                        }
                    } catch(NumberFormatException e) {
                        System.out.println("⚠ Skipping invalid allocation record: " + lines.get(i));
                    }
                }
            }
        } catch(IOException e) {
            // File doesn't exist yet, which is fine
        }
    }
    
    static void loadMultiStopDeliveriesFromCSV() {
        try {
            if (!Files.exists(Paths.get(MULTI_DELIVERY_CSV))) return;
            // Simplified loading - full implementation in production
        } catch(Exception e) {
            // File doesn't exist yet, which is fine
        }
    }
    
    static void saveAllDataToCSV() {
        saveVehiclesToCSV();
        saveRoutesToCSV();
        saveAllocationsToCSV();
        saveMultiStopDeliveriesToCSV();
        System.out.println("✅ All data saved to CSV files");
    }
    
    static void saveVehiclesToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(VEHICLES_CSV))) {
            writer.println("ID,Type,Capacity,Mileage,Rate");
            for(Vehicle v : vehicles) {
                writer.printf("%s,%s,%.1f,%.2f,%.2f%n", 
                    v.id, v.getType(), v.capacity, v.mileage, v.rate);
            }
            if(vehicles.size() > 0) System.out.println("📄 vehicles.csv saved");
        } catch(IOException e) {
            System.out.println("❌ Error saving vehicles.csv: " + e.getMessage());
        }
    }
    
    static void saveRoutesToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ROUTES_CSV))) {
            writer.println("ID,Distance,Cargo,Source,Destination");
            for(Route r : routes) {
                writer.printf("%s,%.1f,%.1f,%s,%s%n", 
                    r.id, r.distance, r.cargoAmount, r.source, r.destination);
            }
            if(routes.size() > 0) System.out.println("📄 routes.csv saved");
        } catch(IOException e) {
            System.out.println("❌ Error saving routes.csv: " + e.getMessage());
        }
    }
    
    static void saveAllocationsToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ALLOCATIONS_CSV))) {
            writer.println("ID,RouteID,VehicleID,Cost,Timestamp");
            for(Allocation a : allocations) {
                writer.printf("%d,%s,%s,%.2f,%s%n", 
                    a.allocationId, a.route.id, a.vehicle.id, a.cost, a.timestamp);
            }
            if(allocations.size() > 0) System.out.println("📄 allocations.csv saved");
        } catch(IOException e) {
            System.out.println("❌ Error saving allocations.csv: " + e.getMessage());
        }
    }
    
    static void saveMultiStopDeliveriesToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(MULTI_DELIVERY_CSV))) {
            writer.println("DeliveryID,VehicleID,Stops,TotalDistance,TotalCargo,Cost,Timestamp");
            for(MultiStopDelivery d : multiStopDeliveries) {
                String stops = String.join("|", 
                    d.stops.stream().map(s -> s.city).toArray(String[]::new));
                writer.printf("%s,%s,%s,%.1f,%.0f,%.2f,%s%n", 
                    d.deliveryId, d.vehicle.id, stops, d.totalDistance, d.totalCargo, d.cost, d.timestamp);
            }
            if(multiStopDeliveries.size() > 0) System.out.println("📄 multi_stop_deliveries.csv saved");
        } catch(IOException e) {
            System.out.println("❌ Error saving multi_stop_deliveries.csv: " + e.getMessage());
        }
    }
}

// ==================== ROUTE CLASS ====================

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

class Truck extends Vehicle {
    Truck(String id, double c, double m, double r) { 
        super(id, c, m, r); 
    }
    
    @Override
    String getType() { 
        return "Truck"; 
    }
}

class Van extends Vehicle {
    Van(String id, double c, double m, double r) { 
        super(id, c, m, r); 
    }
    
    @Override
    String getType() { 
        return "Van"; 
    }
}

// ==================== MULTI-STOP DELIVERY ====================

class MultiStopDelivery {
    String deliveryId;
    Vehicle vehicle;
    List<Stop> stops;
    double totalCargo, totalDistance, cost;
    String timestamp;
    
    MultiStopDelivery(String id, Vehicle v, List<Stop> s, double cargo, double dist, double c) {
        this.deliveryId = id;
        this.vehicle = v;
        this.stops = s;
        this.totalCargo = cargo;
        this.totalDistance = dist;
        this.cost = c;
        this.timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

class Stop {
    String city;
    double distance;
    double cargo;
    
    Stop(String c, double d, double cargo) {
        this.city = c;
        this.distance = d;
        this.cargo = cargo;
    }
}

// ==================== COST ALLOCATION ====================

class CostPair implements Comparable<CostPair> {
    double cost;
    Route route;
    Vehicle vehicle;
    
    CostPair(double c, Route r, Vehicle v) { 
        this.cost = c;
        this.route = r;
        this.vehicle = v;
    }
    
    @Override
    public int compareTo(CostPair other) {
        return Double.compare(this.cost, other.cost);
    }
}

// ==================== ALLOCATION RECORD ====================

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
