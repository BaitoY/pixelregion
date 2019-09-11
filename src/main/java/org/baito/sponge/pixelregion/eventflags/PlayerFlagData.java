package org.baito.sponge.pixelregion.eventflags;

import org.baito.sponge.pixelregion.Config;
import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
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
        if (!values.isEmpty()) {
            String[] keys = values.keySet().toArray(new String[0]);
            for (int i = 0; i < keys.length; i++) {
                arr.put(i, new JSONObject());
                arr.getJSONObject(i).put("flag", keys[i]);
                arr.getJSONObject(i).put("set", values.get(keys[i]));
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

    public boolean flagState(EventFlag f) {
        if (values.containsKey(f.name)) {
            return values.get(f.name);
        }
        return false;
    }

    public boolean flagState(String f) {
        if (values.containsKey(f)) {
            return values.get(f);
        }
        return false;
    }

    public void setFlag(EventFlag f, boolean value) {
        values.put(f.name, value);
        save();
    }

    public void setFlag(String f, boolean value) {
        values.put(f, value);
        save();
    }

    public void save() {
        Player p = Sponge.getServer().getPlayer(uuid).get();
        File f = PlayerFlagDataManager.getFile(p);
        if (Config.readConfig(f).toString().equals(toJSON().toString())) {
            return;
        }
        try {
            PrintWriter pw = new PrintWriter(f);
            pw.print(toJSON().toString(4));
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
