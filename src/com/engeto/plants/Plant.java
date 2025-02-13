package com.engeto.plants;

import java.time.LocalDate;
import java.time.Period;
import java.util.logging.Logger;

public class Plant {
    private static final Logger logger = Logger.getLogger(Plant.class.getName());

    private String name;
    private String notes;
    private LocalDate planted;
    private LocalDate watering;
    private Period frequency;

    // region Constructors
    public Plant(String name, String notes, LocalDate planted, LocalDate watering, Period frequency) throws PlantException {
        this.name = name;
        this.notes = notes;
        this.planted = planted;
        this.watering = validateWateringDate(planted, watering);
        this.frequency = validateFrequency(frequency);
        logger.info("Plant created successfully: " + name);
    }

    public Plant(String name) throws PlantException {
        this(name, "", LocalDate.now(), LocalDate.now(), Period.ofDays(7));
    }

    public Plant(String name, Period frequency) throws PlantException {
        this(name, "", LocalDate.now(), LocalDate.now(), Period.ofDays(7));
    }
    // endregion Constructors

    // region Getters & setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getPlanted() {
        return planted;
    }

    public void setPlanted(LocalDate planted) {
        this.planted = planted;
    }

    public LocalDate getWatering() {
        return watering;
    }

    public void setWatering(LocalDate watering) throws PlantException {
        if (watering.isBefore(planted)) {
            logger.warning("Attempted to set invalid watering date.");
            throw new PlantException("Last watering date cannot be before the planting date.");
        }
        this.watering = watering;
        logger.info("Watering date updated for plant: " + name);
    }

    public Period getFrequency() {
        return frequency;
    }

    public void setFrequency(Period frequency) throws PlantException {
        if (frequency.isNegative() || frequency.isZero()) {
            logger.warning("Attempted to set invalid watering frequency.");
            throw new PlantException("Watering frequency must be a positive period.");
        }
        this.frequency = frequency;
        logger.info("Watering frequency updated for plant: " + name);
    }
    // endregion Getters & setters

    // region Methods
    private Period validateFrequency(Period frequency) throws PlantException {
        if (frequency.isNegative() || frequency.isZero()) {
            logger.severe("Invalid watering frequency: must be positive.");
            throw new PlantException("Watering frequency must be a positive period.");
        }
        logger.info("Valid watering frequency: " + frequency);
        return frequency;
    }

    private LocalDate validateWateringDate(LocalDate planted, LocalDate watering) throws PlantException {
        if (watering.isBefore(planted)) {
            logger.severe("Invalid watering date: cannot be before planting date.");
            throw new PlantException("Last watering date cannot be before the planting date.");
        }
        logger.info("Valid watering date: " + watering);
        return watering;
    }

    public String getWateringInfo() {
        LocalDate nextWatering = watering.plus(frequency);
        logger.info("Retrieved watering info for plant: " + name);
        return String.format("Plant: %s, Last watering: %s, Next watering: %s", name, watering, nextWatering);
    }

    public void doWateringNow() {
        this.watering = LocalDate.now();
        logger.info("Watering done for plant: " + name);
    }

    // endregion Methods
}

