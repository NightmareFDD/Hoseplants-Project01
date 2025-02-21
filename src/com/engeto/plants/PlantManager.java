package com.engeto.plants;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlantManager {
    private static final Logger logger = Logger.getLogger(PlantManager.class.getName());
    public static final String FILE_PATH = "Resources/kvetiny.txt";
    public static final String NEW_FILE_PATH = "Resources/kvetiny_novy.txt";
    List<Plant> plantsList;

    public PlantManager() {
        this.plantsList = new ArrayList<>();
    }

    // region Add methods ------------------------------------------
    public static void addPlantToList(List<Plant> plants, String name, String description, int frequencyDays) {
        try {
            Plant plant = new Plant(name, description, LocalDate.now(), LocalDate.now(), Period.ofDays(frequencyDays));
            plants.add(plant);
            logger.info("New plant added: " + plant.getWateringInfo());
        } catch (PlantException e) {
            logger.log(Level.SEVERE, "Error adding plant: " + name, e);
        }
    }

    public static void addNewSamplePlant(List<Plant> plants) {
        System.out.println("\nPřidána nová rostlina do seznamu.");
        addPlantToList(plants, "Orchidej", "Bílá", 7);
    }

    public static void add10Tulips(List<Plant> plants) {
        System.out.println("Přidáno 10 tulipánů do seznamu.");
        for (int i = 1; i <= 10; i++) {
            addPlantToList(plants, "Tulipán na prodej " + i, "Zasazen dnes", 7);
        }
    }
    // endregion AddMethods -------------------------------------------

    // region Get methods ------------------------------------------
    public Plant getPlant(int index, List<Plant> plantsList) throws PlantException {
        if (!isValidIndex(index, plantsList)) {
            throw new PlantException("Invalid index: " + index);
        }
        return plantsList.get(index);
    }

    public List<Plant> getCopyPlantsList() {
        return new ArrayList<>(plantsList);
    }

    public List<Plant> getPlantsNeedingWatering(List<Plant> plants) {
        LocalDate today = LocalDate.now();
        List<Plant> needsWatering = plants.stream()
                .filter(plant -> plant.getWatering().plus(plant.getFrequency()).isBefore(today))
                .toList();

        logger.info("Retrieved " + needsWatering.size() + " plants needing watering.");
        return needsWatering;
    }
    // endregion Get methods ------------------------------------------

    // region Remove methods ------------------------------------------
    public static void removePlant(List<Plant> plants, int position) {
        if (!isValidIndex(position, plants)) {
            logger.warning("Removing plant at valid position: " + position);
            System.out.println("Nelze odstranit rostlinu: Chybně zadaná pozice.");
            return;
        }
        plants.remove(position);
        System.out.println("\nRostina s pořadovým číslem " + (position + 1) + ". odstraněna ze seznamu.");
        logger.info("Plant removed at position: " + position);
    }

    private static boolean isValidIndex(int index, List<Plant> plantsList) {
        return index >= 0 && index < plantsList.size();
    }
    // endregion Remove methods ------------------------------------------

    // region Sort methods ------------------------------------------

    private static void printSorted(List<Plant> plantsList, Comparator<Plant> comparator, String message) {
        List<Plant> sortedList = new ArrayList<>(plantsList);
        sortedList.sort(comparator);
        System.out.println("\n" + message);
        sortedList.forEach(plant -> System.out.println(plant.getName()));
    }

    // endregion Sort methods ------------------------------------------

    // region Print methods ------------------------------------------
    public static void printSortedByName(List<Plant> plantsList) {
        printSorted(plantsList, Comparator.comparing(Plant::getName), "\nRostliny seřazené podle jména:");
    }

    public static void printSortedByLastWateringDate(List<Plant> plantsList) {
        printSorted(plantsList, Comparator.comparing(Plant::getWatering), "\nRostliny seřazené podle dne poslední zálivky:");
    }

    public static void printPlantWateringInfo(List<Plant> plants) {
        System.out.println("\nInformace o zálivce pro všechny květiny ze seznamu:");
        plants.forEach(plant -> System.out.println(plant.getWateringInfo()));
    }

    public static void printPlantsInfo(List<Plant> plants) {
        plants.forEach(plant -> System.out.printf(
                "\nJméno: [%s], Poznámky: [%s], Zasazeno: [%s], Poslední zálivka: [%s], Frekvence zálivky: [%d] dnů",
                plant.getName(),
                plant.getNotes(),
                plant.getPlanted(),
                plant.getWatering(),
                plant.getFrequency().getDays()
        ));
    }

    // endregion Print methods ------------------------------------------

    // region Save and Load methods ------------------------------------------
    public List<Plant> loadPlantsFromFile(String filename) {
        return readFile(filename);
    }

    private List<Plant> readFile(String filename) {
        List<Plant> loadedPlants = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line, loadedPlants);
            }
        } catch (IOException e) {
            logError("Error reading file: " + filename, e);
        }
        return loadedPlants;
    }

    private void processLine(String line, List<Plant> loadedPlants) {
        try {
            loadedPlants.add(parsePlant(line));
        } catch (IllegalArgumentException | PlantException e) {
            logWarning("Skipping invalid line: " + line, e);
        }
    }

    private Plant parsePlant(String line) throws PlantException {
        String[] parts = validateLine(line);
        if (parts[0].isEmpty()) {
            throw new IllegalArgumentException("Plant name cannot be empty!");
        }
        return new Plant(parts[0], parts[1], parseDate(parts[4]), parseDate(parts[3]), Period.ofDays(parseFrequency(parts[2])));
    }

    private String[] validateLine(String line) {
        String[] parts = line.split("\t", -1);
        if (parts.length < 5)
            throw new IllegalArgumentException("Invalid format: Expected 5 fields, got " + parts.length);
        return parts;
    }

    private int parseFrequency(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid frequency: '" + value + "' → Defaulting to 7 days.");
            return 7;
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            logger.warning("Invalid date format: '" + value + "' → Defaulting to today's date.");
            return LocalDate.now();
        }
    }

    public static List<Plant> loadPlants() {
        PlantManager manager = new PlantManager();
        System.out.println("Rostliny načteny ze souboru květin do seznamu.");
        return manager.loadPlantsFromFile(PlantManager.FILE_PATH);
    }

    public void savePlantsToFile(String filename, List<Plant> plants) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Plant plant : plants) {
                writer.write(formatPlant(plant));
                writer.newLine();
            }
            logger.info("Successfully saved plants to file: " + filename);
        } catch (IOException e) {
            logError("Failed to save plants to file: " + filename, e);
        }
    }

    private String formatPlant(Plant plant) {
        return String.join("\t", plant.getName(), plant.getNotes(),
                String.valueOf(plant.getFrequency().getDays()),
                plant.getWatering().toString(), plant.getPlanted().toString());
    }

    public static void savePlantsToNewFile(PlantManager manager, List<Plant> plants) {
        try {
            manager.savePlantsToFile(PlantManager.NEW_FILE_PATH, plants);
            logger.info("Plant list saved to new file: " + PlantManager.NEW_FILE_PATH);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to save plant list to new file: " + PlantManager.NEW_FILE_PATH, e);
        }
    }

    private void logError(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }

    private void logWarning(String message, Exception e) {
        logger.log(Level.WARNING, message, e);
    }
    // endregion Save and Load methods ------------------------------------------
}

