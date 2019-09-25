package org.baito.sponge.pixelregion.regions;

import org.json.JSONException;
import org.json.JSONObject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class GlobalRegion extends Region {
    public Text displayName;
    public Text desc;
    public String[] encounterData;
    public String headbuttData;
    public String sweetScentData;
    public String forageData;
    public String[] eventFlags;

    public GlobalRegion(JSONObject j) {
        try {
            name = "_global";
            displayName = TextSerializers.FORMATTING_CODE.deserialize(j.getString("DISPLAY_NAME"));
            desc = TextSerializers.FORMATTING_CODE.deserialize(j.getString("DESCRIPTION"));
            encounterData = j.getJSONArray("ENCOUNTER_DATA").toList().toArray(new String[0]);
            sweetScentData = j.getString("SWEET_SCENT_DATA");
            headbuttData = j.getString("HEADBUTT_DATA");
            forageData = j.getString("FORAGE_DATA");
            eventFlags = j.getJSONArray("EVENTS").toList().toArray(new String[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
