package nl.wesleydev.wesleydev.commands.immutables;

import com.google.common.collect.Lists;
import org.bukkit.Material;

import java.util.List;

public class BuyableMaterial {
    private final Material material;
    private final double price;

    public BuyableMaterial(Material material, double price) {
        this.material = material;
        this.price = price;
    }

    public Material getMaterial() {
        return material;
    }

    public double getPrice() {
        return price;
    }

    /**
     * @param buyableMaterials BuyableMaterial array to match the given material string against.
     * @param material String to match against a Material enum in the given BuyableMaterial array.
     * @return a BuyableMaterial from the given array,
     * when a given material string matches the Material enum from a BuyableMaterial in the array.
     */
    public static BuyableMaterial getFromArray(BuyableMaterial[] buyableMaterials, String material) {
        for (BuyableMaterial buyableMaterial : buyableMaterials) {
            if (buyableMaterial.material.name().replace("_", "").equalsIgnoreCase(material)) {
                return buyableMaterial;
            }
        }
        return null;
    }

    /**
     * @param buyableMaterials Array to convert to material enum string list.
     * @return A string list that is converted from the given BuyableMaterial array, based on the material enums.
     */
    public static List<String> getAsEnumStringList(BuyableMaterial[] buyableMaterials) {
        List<String> enums = Lists.newArrayList();
        for (BuyableMaterial buyableMaterial : buyableMaterials) {
            enums.add(buyableMaterial.material.name().replace("_", "").toLowerCase());
        }
        return enums;
    }
}
