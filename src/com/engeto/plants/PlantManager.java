package com.engeto.plants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class PlantManager {
    private static final Logger logger = Logger.getLogger(PlantManager.class.getName());
    List<Plant> plantsList;

    public PlantManager() {
        this.plantsList = new ArrayList<>();
    }

    public void addPlant(Plant plant){
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

    public void sortByName(){
        plantsList.sort(Comparator.comparing(Plant::getName));
        logger.info("Sorted plants by name.");
    }

    public void sortByLastWateringDate(){
        plantsList.sort(Comparator.comparing(Plant::getWatering));
        logger.info("Sorted plants by last watering date.");
    }
}
