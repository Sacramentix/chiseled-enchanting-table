package chiseled_enchanting_table.utils;

import org.jetbrains.annotations.Nullable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record EnchantmentWithLevel(Identifier enchantment_id, int enchantment_level) {
    /**
     * Convert an Enchantement Identifier into Enchantment
     * This method need a world to get access to the registry of enchantment
     * Since enchantment are data driven and can be defined by datapack
     * @param enchantment_id
     * @param world
     * @return Enchantment
     */
    @Nullable
    public static Enchantment IdentifierToEnchantment(Identifier enchantment_id, World world) {
        return world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).get(enchantment_id);
    }

    public boolean is(String id) {
        return this.enchantment_id().equals(Identifier.ofVanilla(id));
    }

    /**
     * Convert an Enchantement Enchantment into Identifier
     * This method need a world to get access to the registry of enchantment
     * Since enchantment are data driven and can be defined by datapack
     * @param enchantment_id
     * @param world
     * @return Enchantment
     */
    @Nullable
    public static Identifier EnchantmentToIdentifier(Enchantment enchantment, World world) {
        return world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getId(enchantment);
    }

    /**
     * Convert an Enchantement Identifier into Enchantment
     * This method need a world to get access to the registry of enchantment
     * Since enchantment are data driven and can be defined by datapack
     * @param enchantment_id
     * @param world
     * @return Enchantment
     */
    @Nullable
    public static RegistryEntry<Enchantment> IdentifierToRegistryEntryEnchantment(Identifier enchantment_id, World world) {
        return world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(enchantment_id).get();
    }

    public static final PacketCodec<PacketByteBuf, EnchantmentWithLevel> ENCHANTMENT_WITH_LEVEL_CODEC = 
        PacketCodec.of(
            (payload, buf) -> {
                buf.writeInt(payload.enchantment_level);
                buf.writeIdentifier(payload.enchantment_id);
            },
            buf -> {
                var enchantment_level    = buf.readInt();
                var enchantment_id       = buf.readIdentifier();
                return new EnchantmentWithLevel(enchantment_id, enchantment_level);
            }
        );
}