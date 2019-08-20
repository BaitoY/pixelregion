package org.baito.sponge.pixelregion.eventflags;

import org.json.JSONObject;

public class EventFlag {
    public String name;

    EventFlag(JSONObject j) {
        try {
            if (!j.has("name")) {
                throw new NullPointerException("An Event has no \"name\"! Skipping...");
            }
            name = j.getString("name");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public class FlagCondition {
        public EventFlag[] enabledFlags;
        public EventFlag[] disabledFlag;
        public PartyCondition partyCondition;
        public WorldCondition worldCondition;

        FlagCondition(JSONObject j) {
            partyCondition = j.has("partyConditions") ? new PartyCondition(j.getJSONObject("partyConditions")) : null;
            worldCondition = j.has("worldConditions") ? new WorldCondition(j.getJSONObject("worldConditions")) : null;
        }

        private class PartyCondition {
            public boolean useEntireParty;
            public String SPECIES;
            public int AVERAGE;
            public String[] TYPES;
            public String ABILITIY;
            public String MOVE;
            public boolean SHINY;
            public String GENDER;
            public String HELDITEM;

            PartyCondition(JSONObject j) {
                try {
                    if (!j.has("useEntireParty")) {
                        throw new NullPointerException("An Event Flag's party condition " +
                                "has no \"useEntireParty\" boolean! Skipping...");
                    }
                    useEntireParty = j.getBoolean("useEntireParty");
                    SPECIES = j.has("species") ? j.getString("species") : null;
                    AVERAGE = j.has("average") ? Math.max(Math.min(j.getInt("average"), 100), 1) : null;
                    TYPES = j.has("types") ? j.getJSONArray("types").toList().toArray(new String[0]) : null;
                    ABILITIY = j.has("ability") ? j.getString("ability") : null;
                    MOVE = j.has("move") ? j.getString("move") : null;
                    SHINY = j.has("shiny") ? j.getBoolean("shiny") : null;
                    GENDER = j.has("gender") ? j.getString("gender") : null;
                    HELDITEM = j.has("heldItem") ? j.getString("heldItem") : null;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        private class WorldCondition {
            public int[] TIME;
            public String[] WEATHER;
            public String[] BLOCKS;
            public boolean onBlock;

            WorldCondition(JSONObject j) {

            }
        }

    }

}

