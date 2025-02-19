package com.engeto.plants;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Settings {
    private static final Logger logger = Logger.getLogger(Settings.class.getName());

    public static List<Plant> loadPlants() {
        PlantManager manager = new PlantManager();
        return manager.loadPlantsFromFile(PlantManager.FILE_PATH);
    }

    public static void printPlantWateringInfo(List<Plant> plants) {
        plants.forEach(plant -> System.out.println(plant.getWateringInfo()));
    }

    public static void addNewSamplePlant(List<Plant> plants) {
        addPlant(plants, "Orchidej", "Bílá");
    }

    public static void add10Tulips(List<Plant> plants) {
        for (int i = 1; i <= 10; i++) {
            addPlant(plants, "Tulipán na prodej " + i, "Zasazen dnes");
        }
    }

    private static void addPlant(List<Plant> plants, String name, String description) {
        try {
            Plant plant = new Plant(name, description, LocalDate.now(), LocalDate.now(), Period.ofDays(14));
            plants.add(plant);
            logger.info("New plant added: " + plant.getWateringInfo());
        } catch (PlantException e) {
            logger.log(Level.SEVERE, "Error adding plant: " + name, e);
        }
    }

    public static void removePlantAtPosition(PlantManager manager, List<Plant> plants, int position) {
        try {
            manager.removePlant(position, plants);
            logger.info("Plant at position " + position + " removed.");
        } catch (PlantException e) {
            logger.log(Level.WARNING, "Failed to remove plant at position: " + position, e);
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



}
