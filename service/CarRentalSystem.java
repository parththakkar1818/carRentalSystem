package service;
import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


import model.*;

public class CarRentalSystem {
    private List<Car> cars;
    private List<Customer> customers;
    private List<Rental> rentals;

    public CarRentalSystem() {
        cars = new ArrayList<>();
        customers = new ArrayList<>();
        rentals = new ArrayList<>();
        loadCarsFromCsv(); // Load cars from CSV file
    }

    private void loadCarsFromCsv() {
        try (BufferedReader reader = new BufferedReader(new FileReader("cars.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Car car = Car.fromCsvString(line);
                cars.add(car);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCarsToCsv() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("cars.csv"))) {
            for (Car car : cars) {
                writer.write(car.toCsvString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addCar(Car car) {
        cars.add(car);
        saveCarsToCsv();
    }
    

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void rentCar(Car car, Customer customer, int days) {
        // System.out.println(car.isAvailable());
        if (car.isAvailable()) {
            car.rent();
            rentals.add(new Rental(car, customer, days));
            saveCarsToCsv(); // Update availability in CSV
    
        } else {
            System.out.println("Car is not available for rent.");
        }
    }

    public void returnCar(Car car) {
        car.returnCar();
        Rental rentalToRemove = null;
        for (Rental rental : rentals) {
            if (rental.getCar() == car) {
                rentalToRemove = rental;
                break;
            }
        }
        if (rentalToRemove != null) {
            rentals.remove(rentalToRemove);
            saveCarsToCsv(); // Update availability in CSV
    
        } else {
            System.out.println("Car was not rented.");
        }
    }
    

    public void menu() {
        Scanner scanner = new Scanner(System.in);
    
        while (true) {
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║           Car Rental System          ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║ 1. Show available Cars               ║");
            System.out.println("║ 2. Rent a Car                        ║");
            System.out.println("║ 3. Return a Car                      ║");
            System.out.println("║ 4. Add a Car                         ║");
            System.out.println("║ 5. Exit                              ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("Enter your choice: ");
    
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
    
            switch (choice) {
                case 1:
                    System.out.println("\n╔═══════════════════════════════════════════╗");
                    System.out.println("║               Available Cars              ║");
                    System.out.println("╠═══════════════════════════════════════════╣");
                    System.out.printf("%-8s %-15s %-15s %-15s%n", "S.No.", "Car ID", "Brand", "Model");
                    int cnt = 1;
                    for (Car car : cars) {
                        // System.out.println(car.isAvailable());
                        if (car.isAvailable()) {
                            System.out.printf("%-8d %-15s %-15s %-15s%n", cnt++, car.getCarId(), car.getBrand(), car.getModel());
                        }
                    }
                    System.out.println("╚════════════════════════════════════════════╝");
                    break;
    
                case 2:
                    System.out.println("\n╔══════════════════════════════════════╗");
                    System.out.println("║               Rent a Car             ║");
                    System.out.println("╠══════════════════════════════════════╣");
                    System.out.print("Enter your name: ");
                    String customerName = scanner.nextLine();
    
                    System.out.println("\nAvailable Cars:");
                    System.out.printf("%-15s %-15s %-15s%n", "Car ID", "Brand", "Model");
                    for (Car car : cars) {
                        if (car.isAvailable()) {
                            System.out.printf("%-15s %-15s %-15s%n", car.getCarId(), car.getBrand(), car.getModel());
                        }
                    }
    
                    System.out.print("\nEnter the car ID you want to rent: ");
                    String carId = scanner.nextLine();
    
                    System.out.print("Enter the number of days for rental: ");
                    int rentalDays = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
    
                    Customer newCustomer = new Customer("CUS" + (customers.size() + 1), customerName);
                    addCustomer(newCustomer);
    
                    Car selectedCar = null;
                    for (Car car : cars) {
                        if (car.getCarId().equals(carId) && car.isAvailable()) {
                            selectedCar = car;
                            break;
                        }
                    }
    
                    if (selectedCar != null) {
                        double totalPrice = selectedCar.calculatePrice(rentalDays);
                        System.out.println("\n╔══════════════════════════════════════╗");
                        System.out.println("║           Rental Information         ║");
                        System.out.println("╠══════════════════════════════════════╣");
                        System.out.printf("%-15s %-15s%n", "Customer ID:", newCustomer.getCustomerId());
                        System.out.printf("%-15s %-15s%n", "Customer Name:", newCustomer.getName());
                        System.out.printf("%-15s %-15s%n", "Car:", selectedCar.getBrand() + " " + selectedCar.getModel());
                        System.out.printf("%-15s %-15d%n", "Rental Days:", rentalDays);
                        System.out.printf("%-15s %-15.2f%n", "Total Price:", totalPrice);
    
                        System.out.print("\nConfirm rental (Y/N): ");
                        String confirm = scanner.nextLine();
    
                        if (confirm.equalsIgnoreCase("Y")) {
                            rentCar(selectedCar, newCustomer, rentalDays);
                            System.out.println("\nCar rented successfully.");
                        } else {
                            System.out.println("\nRental canceled.");
                        }
                    } else {
                        System.out.println("\nInvalid car selection or car not available for rent.");
                    }
                    break;
    
                case 3:
                    System.out.println("\n╔══════════════════════════════════════╗");
                    System.out.println("║               Return a Car           ║");
                    System.out.println("╠══════════════════════════════════════╣");
                    System.out.print("Enter the car ID you want to return: ");
                    String carIdReturn = scanner.nextLine();
    
                    Car carToReturn = null;
                    for (Car car : cars) {
                        if (car.getCarId().equals(carIdReturn) && !car.isAvailable()) {
                            carToReturn = car;
                            break;
                        }
                    }
    
                    if (carToReturn != null) {
                        Customer customer = null;
                        for (Rental rental : rentals) {
                            if (rental.getCar() == carToReturn) {
                                customer = rental.getCustomer();
                                break;
                            }
                        }
    
                        if (customer != null) {
                            returnCar(carToReturn);
                            System.out.println("Car returned successfully by " + customer.getName());
                        } else {
                            System.out.println("Car was not rented or rental information is missing.");
                        }
                    } else {
                        System.out.println("Invalid car ID or car is not rented.");
                    }
                    break;
    
                case 4:
                    System.out.println("\n╔════════════════════════════════════╗");
                    System.out.println("║               Add a Car            ║");
                    System.out.println("╠════════════════════════════════════╣");
                    Scanner sc = new Scanner(System.in);
                    System.out.print("Enter car ID in format of C0XX: "); 
                    String userCarId = sc.nextLine();
                    System.out.print("Enter brand: ");
                    String userBrand = sc.nextLine();
                    System.out.print("Enter Model: ");
                    String userModel = sc.nextLine();
                    System.out.print("Enter base price to charge per day: ");
                    double userBasePricePerDay = sc.nextDouble();
                    
                    // String randomCarId;
                    // boolean isUnique = false;
                    // while (!isUnique) {
                    //     randomCarId = "C" + String.format("%04d", (int) (Math.random() * 10000));
                    //     boolean idExists = cars.stream().anyMatch(c -> c.getCarId().equals(randomCarId));
                    //     if (!idExists) {
                    //         car.setCarId(randomCarId);
                    //         isUnique = true;
                    //     }
                    // }
    
                    Car userCar = new Car(userCarId, userBrand, userModel, userBasePricePerDay);
                    // cars.add(userCar);
                    // saveCarsToCsv();

                    addCar(userCar);
                    System.out.println("\nCar added successfully");
    
                    break;
    
                case 5:
                    System.out.println("Thank you for using the Car Rental System!");
                    return;
    
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
                    break;
            }
        }
    }
    
    
    
}

