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

    public void addPlant(Plant plant) {
        plantsList.add(plant);
        logger.info("Plant added: " + plant.getName());
    }

    public Plant getPlant(int index, List<Plant> plantsList) throws PlantException {
        if (!isValidIndex(index, plantsList)) {
            logInvalidIndex(index);
            throw new PlantException("Invalid index: " + index);
        }
        return plantsList.get(index);
    }

    public void removePlant(int index, List<Plant> plantsList) throws PlantException {
        if (!isValidIndex(index, plantsList)) {
            logInvalidIndex(index);
            throw new PlantException("Invalid index: " + index);
        }
        plantsList.remove(index);
        logger.info("Plant removed at index: " + index);
    }

    private boolean isValidIndex(int index, List<Plant> plantsList) {
        return index >= 0 && index < plantsList.size();
    }

    private void logInvalidIndex(int index) {
        logger.severe("Invalid index access in getPlant: " + index);
    }

    public List<Plant> getCopyPlantsList() {
        return new ArrayList<>(plantsList);
    }

    public List<Plant> getPlantsNeedingWatering(List<Plant> plants) {
        List<Plant> needsWatering = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Plant plant : plants) {
            if (needsWatering(plant, today)) {
                needsWatering.add(plant);
            }
        }
        logger.info("Retrieved plants needing watering: " + needsWatering.size());
        return needsWatering;
    }

    private boolean needsWatering(Plant plant, LocalDate today) {
        return plant.getWatering().plus(plant.getFrequency()).isBefore(today);
    }

    public void sortByName() {
        plantsList.sort(Comparator.comparing(Plant::getName));
        logger.info("Sorted plants by name.");
    }

    public void sortByLastWateringDate() {
        plantsList.sort(Comparator.comparing(Plant::getWatering));
        logger.info("Sorted plants by last watering date.");
    }

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

    private String formatPlantData(Plant plant) {
        return String.join("\t",
                plant.getName(),
                plant.getNotes(),
                String.valueOf(plant.getFrequency().getDays()),
                plant.getPlanted().toString(),
                plant.getWatering().toString()
        );
    }
}

