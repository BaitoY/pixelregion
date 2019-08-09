package org.baito.sponge.pixelregion.regions;

import org.baito.sponge.pixelregion.encounterdata.EncounterData;
import org.baito.sponge.pixelregion.encounterdata.EncounterDataManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.awt.*;

public class Region {
    public String name;
    public Text displayName;
    public boolean notifyEnter;
    public boolean notifyExit;
    public int[][] points;
    public int[] yDim;
    public double weight;
    public Text desc;
    public Polygon polygon;
    public EncounterData[] encounterData;

    Region(JSONObject j) {
        try {
            if (!j.has("name") || !j.has("points")) {
                throw new NullPointerException("One region JSON has thrown an error! " +
                        "Ensure that all JSONs have atleast a name and array of points!");
            }
            name = j.getString("name");
            displayName = j.has("displayName") ?
                    TextSerializers.FORMATTING_CODE.deserialize(j.getString("displayName")) :
                    TextSerializers.FORMATTING_CODE.deserialize(j.getString("name"));
            if (j.has("notifs")) {
                notifyEnter = j.getJSONObject("notifs").has("entry") ? j.getJSONObject("notifs").getBoolean("entry") : true;
                notifyExit = j.getJSONObject("notifs").has("exit") ? j.getJSONObject("notifs").getBoolean("exit") : true;
            } else {
                notifyEnter = true;
                notifyExit = true;
            }
            this.points = createArray(j.getJSONArray("points"));
            weight = j.has("weight") ? (double) j.getNumber("weight") : 1.0;
            desc = j.has("desc") ? TextSerializers.FORMATTING_CODE.deserialize(j.getString("desc")): null ;
            if (j.has("yDim")) {
                yDim[0] = j.getJSONArray("yDim").getInt(0);
                yDim[0] = j.getJSONArray("yDim").getInt(1);
            }
            if (j.has("encounterData")) {
                encounterData = new EncounterData[j.getJSONArray("encounterData").length()];
                for (int i = 0; i < encounterData.length; i++) {
                    encounterData[i] = EncounterDataManager.getData(j.getJSONArray("encounterData").getString(i));
                }
            }
            polygon = new Polygon();
            for (int i = 0; i < points.length; i++) {
                polygon.addPoint(points[i][0], points[i][1]);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private int[][] createArray(JSONArray a) {
        int[][] newArray = new int[a.length()][2];
        for (int i = 0; i < a.length(); i++) {
            newArray[i][0] = a.getJSONArray(i).getInt(0);
            newArray[i][1] = a.getJSONArray(i).getInt(1);
        }
        return newArray;
    }
}