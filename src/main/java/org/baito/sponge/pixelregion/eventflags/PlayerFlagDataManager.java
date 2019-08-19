package org.baito.sponge.pixelregion.eventflags;

import org.apache.commons.io.FilenameUtils;
import org.baito.sponge.pixelregion.Config;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PlayerFlagDataManager {
    public static List<PlayerFlagData> data = new ArrayList<>();

    public static PlayerFlagData getOrCreateData(Player p) {
        Path direc = Config.pxrDir.resolve("events").resolve("playerdata");
        for (int i = 0; i < direc.toFile().listFiles().length; i++) {
            if (FilenameUtils.removeExtension(direc.toFile().listFiles()[i].getName()).equals(p.getUniqueId().toString())) {
                return new PlayerFlagData(Config.readConfig(direc.toFile().listFiles()[i]));
            }
        }
        File file = new File(direc.resolve(p.getUniqueId() + ".json").toString());
        try {
            file.createNewFile();
            PlayerFlagData pfd = new PlayerFlagData(p);
            PrintWriter pw = new PrintWriter(file);
            pw.print(pfd.toJSON().toString(4));
            pw.close();
            data.add(pfd);
            return pfd;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getFile(Player p) {
        Path direc = Config.pxrDir.resolve("events").resolve("playerdata");
        for (int i = 0; i < direc.toFile().listFiles().length; i++) {
            if (FilenameUtils.removeExtension(direc.toFile().listFiles()[i].getName()).equals(p.getUniqueId().toString())) {
                return direc.toFile().listFiles()[i];
            }
        }
        return null;
    }

    public static void generateData(File[] f) {
        data = new ArrayList<PlayerFlagData>();
        for (int i = 0; i < f.length; i++) {
            data.add(new PlayerFlagData(Config.readConfig(f[i])));
        }
    }

    public static void save() {
        for (PlayerFlagData i : data) {
            Player p = Sponge.getServer().getPlayer(i.uuid).get();
            File f = getFile(p);
            try {
                PrintWriter pw = new PrintWriter(f);
                pw.print(i.toJSON().toString(4));
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
