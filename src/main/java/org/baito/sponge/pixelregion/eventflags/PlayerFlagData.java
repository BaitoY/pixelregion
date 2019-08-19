package org.baito.sponge.pixelregion.eventflags;

import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.entity.living.player.Player;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PlayerFlagData {
    public UUID uuid;
    public Map<String, Boolean> values = new HashMap<>();

    PlayerFlagData(JSONObject f) {
        uuid = UUID.fromString(f.getString("UUID"));
        if (f.has("values") && f.getJSONArray("values").length() > 0) {
            for (int i = 0; i < f.getJSONArray("values").length(); i++) {
                values.put(f.getJSONArray("values").getJSONObject(i).getString("flag"),
                        f.getJSONArray("values").getJSONObject(i).getBoolean("set"));
            }
        }
    }

    PlayerFlagData(Player p) {
        uuid = p.getUniqueId();
    }

    public JSONArray valuesToArray() {
        JSONArray arr = new JSONArray();
        int ind = 0;
        if (!values.isEmpty()) {
            Iterator<String> i = values.keySet().iterator();
            while (i.hasNext()) {
                arr.put(ind, new JSONObject());
                arr.getJSONObject(ind).put("flag", i.next());
                arr.getJSONObject(ind).put("set", values.get(i.next()));
                ind++;
            }
        }
        return arr;
    }

    public JSONObject toJSON() {
        JSONObject o = new JSONObject();
        o.put("UUID", uuid.toString());
        o.put("values", valuesToArray());
        return o;
    }
}
