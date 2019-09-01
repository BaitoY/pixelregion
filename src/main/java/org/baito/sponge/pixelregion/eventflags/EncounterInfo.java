package org.baito.sponge.pixelregion.eventflags;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumBossMode;
import net.minecraft.entity.player.EntityPlayer;
import org.json.JSONObject;

public class EncounterInfo {
    public int lvlMin;
    public int lvlMax;
    public int shinyChance;
    public int bossChance;
    public String species;

    EncounterInfo(JSONObject j) {
        if (j.has("species")) {
            species = j.getString("species");
        } else {
            throw new NullPointerException("An Event's Battle effect has no species! Skipping...");
        }
        if (j.has("levelMin") && j.has("levelMax")) {
            lvlMin = j.getInt("levelMin");
            lvlMax = j.getInt("levelMax");
        } else {
            throw new NullPointerException("An Event's Battle effect has no level min or max! Skipping...");
        }
        if (j.has("shinyChance")) {
            shinyChance = j.getInt("shinyChance");
        } else {
            shinyChance = (int) PixelmonConfig.shinyRate;
        }
        if (j.has("bossChance")) {
            bossChance = j.getInt("bossChance");
        } else {
            bossChance = (int)PixelmonConfig.bossRate;
        }
    }

    EncounterInfo() {}

    EncounterInfo(String species, int lvlMin, int lvlMax) {
        this.species = species;
        this.lvlMin = lvlMin;
        this.lvlMax = lvlMax;
        shinyChance = (int) PixelmonConfig.shinyRate;
        bossChance = (int)PixelmonConfig.bossRate;
    }

    EncounterInfo(String species, int lvlMin, int lvlMax, int shinyChance) {
        this.species = species;
        this.lvlMin = lvlMin;
        this.lvlMax = lvlMax;
        this.shinyChance = shinyChance;
        bossChance = (int)PixelmonConfig.bossRate;
    }

    EncounterInfo(String species, int lvlMin, int lvlMax, int shinyChance, int bossChance) {
        this.species = species;
        this.lvlMin = lvlMin;
        this.lvlMax = lvlMax;
        this.shinyChance = shinyChance;
        this.bossChance = bossChance;
    }

    public Pokemon createPokemon() {
        StringBuilder sb = new StringBuilder();
        int lvl = (int)Math.floor(Math.random() * (lvlMax - lvlMin)) + lvlMin;
        sb.append(species + " lvl:" + lvl);
        if (Math.floor(Math.random() * shinyChance) == 0) {
            sb.append(" s");
        }
        return Pixelmon.pokemonFactory.create(PokemonSpec.from(sb.toString().split(" ")));
    }

    public EntityPixelmon spawn(EntityPlayer plr) {
        EntityPixelmon e = createPokemon().getOrSpawnPixelmon(plr.getEntityWorld(), plr.getPosition().getX(),
                plr.getPosition().getY(), plr.getPosition().getZ());
        if (Math.floor(Math.random() * bossChance) == 0) {
            e.setBoss(EnumBossMode.getRandomMode());
        }
        return e;
    }
}
