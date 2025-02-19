import com.engeto.plants.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Načti seznam květin ze souboru
        List<Plant> plantsList = Settings.loadPlants();
        PlantManager manager = new PlantManager();

        // 2. Vypiš na obrazovku informace o zálivce pro všechny květiny ze seznamu.
        Settings.printPlantWateringInfo(plantsList);

        // 3. Přidej novou květinu do seznamu
        Settings.addNewSamplePlant(plantsList);

        // 4. Přidej 10 rostlin (tulipány)
        Settings.add10Tulips(plantsList);

        //5. Květinu na třetí pozici odeber ze seznamu (prodali jsme ji).
        Settings.removePlantAtPosition(manager, plantsList, 2);

        // 6.
        Settings.savePlantsToNewFile(manager, plantsList);
        Settings.printPlantWateringInfo(plantsList);
    }
}
