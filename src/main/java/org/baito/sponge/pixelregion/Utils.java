package org.baito.sponge.pixelregion;

import net.minecraft.nbt.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class Utils {

    public static BlockState stringToBlock(String b) {
        Optional<BlockState> bt = Sponge.getRegistry().getType(BlockState.class, b);
        return bt.orElse(null);
    }

    public static boolean hasVariant(String b) {
        return b.contains("[");
    }

    public static String blockWithoutVariant(String b) {
        return b.substring(0, b.indexOf('[')-1);
    }

    public static String variant(String b) {
        return b.substring(b.indexOf('['), b.indexOf(']'));
    }

    public static boolean matches(BlockState a, BlockState[] b) {
        for (BlockState i : b) {
            if (i == null) continue;
            if (a == i) return true;
        }
        return false;
    }

    public static boolean matches(BlockType a, BlockState[] b) {
        for (BlockState i : b) {
            if (i == null) continue;
            if (a == i.getType()) return true;
        }
        return false;
    }

    public static boolean NBTMatch(NBTTagCompound origin, NBTTagCompound test) {
        Main main = (Main) Sponge.getPluginManager().getPlugin("pixelregion").get().getInstance().get();
        if (test.equals(origin)) {
            return true;
        }
        if (test.getKeySet().containsAll(origin.getKeySet())) {
            Set<String> set = origin.getKeySet();
            for (String e : set) {
                if (test.getTagId(e) != origin.getTagId(e)) {
                    return false;
                }
                if (!NBTMatch(origin.getTag(e), test.getTag(e))) {
                    return false;
                }
                /*
                if (origin.getTag(e) instanceof NBTTagCompound) {
                    if (!NBTMatch(origin.getCompoundTag(e), test.getCompoundTag(e))) return false;
                } else if (origin.getTag(e) instanceof NBTTagList) {
                    NBTTagList originList = ((NBTTagList)origin.getTag(e));
                    NBTTagList testList = ((NBTTagList)test.getTag(e));
                    for (int i = 0; i < originList.tagCount(); i++) {

                    }
                } else if (origin.getTag(e) instanceof NBTTagByteArray) {
                    if (!Arrays.equals(origin.getByteArray(e), test.getByteArray(e)))
                        return false;
                } else if (origin.getTag(e) instanceof NBTTagIntArray) {
                    if (!Arrays.equals(origin.getIntArray(e), test.getIntArray(e)))
                        return false;
                } else {
                    if (!origin.getTag(e).equals(test.getTag(e))) return false;
                }*/
            }
        } else {
            return false;
        }
        return true;
    }

    public static boolean NBTMatch(NBTBase origin, NBTBase test) {
        switch (origin.getId()) {
            case 1: // Byte
            case 2: // Short
            case 3: // Int
            case 4: // Long
            case 5: // Float
            case 6: // Double
            case 8: // String
                if (!origin.equals(test)) return false;
                break;
            case 7: // Byte Array
                if (!Arrays.equals(((NBTTagByteArray)test).getByteArray(), (((NBTTagByteArray)origin).getByteArray()))) return false;
                break;
            case 9: // List
                NBTTagList originList = ((NBTTagList)origin);
                NBTTagList testList = ((NBTTagList)test);
                for (int i = 0; i < originList.tagCount(); i++) {
                    if (!NBTMatch(originList.get(i), testList.get(i))) return false;
                }
                break;
            case 10: // Compound
                if (!NBTMatch((NBTTagCompound)origin, (NBTTagCompound)test)) return false;
                break;
            case 11: // Int Array
                if (!Arrays.equals(((NBTTagIntArray)test).getIntArray(), (((NBTTagIntArray)origin).getIntArray()))) return false;
                break;
        }
        return true;
    }

    public static boolean equals(String one, String two) {
        return one.toUpperCase().equals(two.toUpperCase());
    }
}
