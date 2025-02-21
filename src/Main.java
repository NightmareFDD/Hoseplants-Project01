import com.engeto.plants.*;

import java.util.List;
import java.util.logging.Logger;

public class Main {
        public static void main(String[] args) {
        LoggerSetup.setupLogger();
        Logger logger = Logger.getLogger(Main.class.getName());
        logger.info("Main application started");

        PlantManager manager = new PlantManager();

        // 1. Načti seznam květin ze souboru
        List<Plant> plantsList = PlantManager.loadPlants();

        // 2. Vypiš na obrazovku informace o zálivce pro všechny květiny ze seznamu.
        printWateringInfo(plantsList);

        // (úkoly 3 až 5)
        addOrRemoveNewPlants(plantsList);

        //(úkol 6 a 7)
        saveAndReload(manager, plantsList);
        sortAndDisplay(plantsList);
    }

    private static void printWateringInfo(List<Plant> plants) {
        PlantManager.printPlantWateringInfo(plants);
    }

    private static void addOrRemoveNewPlants(List<Plant> plants) {
        // 3. Přidej novou květinu do seznamu
        PlantManager.addNewSamplePlant(plants);
        // 4. Přidej 10 rostlin (tulipány)
        PlantManager.add10Tulips(plants);
        //5. Květinu na třetí pozici odeber ze seznamu (prodali jsme ji).
        PlantManager.removePlant(plants, 2);
    }

    private static void saveAndReload(PlantManager manager, List<Plant> plants) {
        //6. Ulož seznam květin do nového souboru a ověř, že je jeho obsah odpovídá provedeným změnám.
        PlantManager.savePlantsToNewFile(manager, plants);

        //7. Vyzkoušej opětovné načtení vygenerovaného souboru.
        List<Plant> reloadedPlants = manager.loadPlantsFromFile(PlantManager.NEW_FILE_PATH);
        System.out.println("\nSeznam z nově vytvořeného souboru:");
        PlantManager.printPlantsInfo(reloadedPlants);
    }

    private static void sortAndDisplay(List<Plant> plants) {
        //8. Seřazení rostlin ve správci seznamu podle různých kritérií a výpis seřazeného seznamu.
        PlantManager.printSortedByName(plants);
        PlantManager.printSortedByLastWateringDate(plants);
    }
}

