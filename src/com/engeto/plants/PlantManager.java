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
            logger.warning("Invalid position: " + position);
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

    private static void sortByName(List<Plant> plantsList) {
        plantsList.sort(Comparator.comparing(Plant::getName));
        logger.info("Sorted plants by name.");
    }

    private static void sortByLastWateringDate(List<Plant> plantsList) {
        plantsList.sort(Comparator.comparing(Plant::getWatering));
        logger.info("Sorted plants by last watering date.");
    }

    // endregion Sort methods ------------------------------------------

    // region Print methods ------------------------------------------
    public static void printSortByName(List<Plant> plantsList) {
        sortByName(plantsList);
        System.out.println("\nRostliny seřazené podle jména:");
        plantsList.forEach(plant -> System.out.println(plant.getName()));
    }

    public static void printSortByLastWateringDate(List<Plant> plantsList) {
        sortByLastWateringDate(plantsList);
        System.out.println("\nRostliny seřazené podle dne poslední zálivky:");
        plantsList.forEach(plant -> System.out.println(plant.getName()));
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
        List<Plant> loadedPlants = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            reader.lines().forEach(line -> processLine(line, loadedPlants));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading file: " + filename, e);
        }
        return loadedPlants;
    }

    private void processLine(String line, List<Plant> loadedPlants) {
        try {
            String[] parts = validateAndSplitLine(line);
            Plant plant = createPlantFromParts(parts);
            loadedPlants.add(plant);
        } catch (PlantException | IllegalArgumentException e) {
            logger.log(Level.WARNING, "Skipping invalid line: " + line, e);
        }
    }

    private String[] validateAndSplitLine(String line) {
        String[] parts = line.split("\t"); // Assume tab-separated values
        if (parts.length < 5) throw new IllegalArgumentException("Invalid format");
        return parts;
    }

    private Plant createPlantFromParts(String[] parts) throws PlantException {
        String name = parts[0];
        String notes = parts[1];
        int frequency = parseFrequency(parts[2]);
        LocalDate watering = parseDate(parts[3]);
        LocalDate planted = parseDate(parts[4]);
        return new Plant(name, notes, planted, watering, Period.ofDays(frequency));
    }

    private int parseFrequency(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid frequency: " + value);
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + value);
        }
    }

    public static List<Plant> loadPlants() {
        PlantManager manager = new PlantManager();
        System.out.println("Rostliny načteny ze souboru květin do seznamu.");
        return manager.loadPlantsFromFile(PlantManager.FILE_PATH);
    }

    public void savePlantsToFile(String filename, List<Plant> plants) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            plants.forEach(plant -> writePlantData(writer, plant));
            logger.info("Successfully saved plants to file: " + filename);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save plants to file: " + filename, e);
        }
    }

    private void writePlantData(BufferedWriter writer, Plant plant) {
        try {
            writer.write(formatPlantData(plant));
            writer.newLine();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error writing plant data: " + plant.getName(), e);
        }
    }

    public static void savePlantsToNewFile(PlantManager manager, List<Plant> plants) {
        try {
            manager.savePlantsToFile(PlantManager.NEW_FILE_PATH, plants);
            logger.info("Plant list saved to new file: " + PlantManager.NEW_FILE_PATH);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to save plant list to new file: " + PlantManager.NEW_FILE_PATH, e);
        }
    }

    private String formatPlantData(Plant plant) {
        return String.join("\t",
                plant.getName(),
                plant.getNotes(),
                String.valueOf(plant.getFrequency().getDays()),
                plant.getWatering().toString(),
                plant.getPlanted().toString()
        );
    }
    // endregion Save and Load methods ------------------------------------------
}

