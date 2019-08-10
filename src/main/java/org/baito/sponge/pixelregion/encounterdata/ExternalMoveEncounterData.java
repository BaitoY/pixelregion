package org.baito.sponge.pixelregion.encounterdata;

import org.json.JSONObject;

public class ExternalMoveEncounterData {
    public String name;
    public int chance;
    public EncounterData.Encounters encounterData;

    ExternalMoveEncounterData(JSONObject j) {
        try {
            if (!j.has("name")) {
                throw new NullPointerException("An external encounter data has no name! Skipping...");
            }
            name = j.getString("name");
            if (!j.has("chance")) {
                throw new NullPointerException("External encounter data \"" + name + "\" has no chance! Skipping...");
            }
            chance = j.getInt("chance");
            if (!j.has("encounters")) {
                throw new NullPointerException("Extenral encounter data \"" + name + "\" has no encounters! Skipping...");
            }
            encounterData = new EncounterData.Encounters(j.getJSONObject("encounters"), name);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
