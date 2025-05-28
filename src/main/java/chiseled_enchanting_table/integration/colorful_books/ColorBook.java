package chiseled_enchanting_table.integration.colorful_books;

import java.util.Collection;
import java.util.Random;
import java.util.stream.IntStream;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;

public class ColorBook {

    private static java.lang.reflect.Method enchantmentDyesMethod_cache = null;
    private static Object ColorfulBooksConfig_cache = null;

    public static void enchantedWithConfig(ItemStack itemStack) {
        if (!FabricLoader.getInstance().isModLoaded("colorful_books")) return;
        try {
            // Optional<List<DyeItem>> enchantmentDyes
            // com.boyonk.colorfulbooks.ColorfulBooks
            // com.boyonk.colorfulbooks.ColorfulBooksConfig
            //  public Optional<List<DyeItem>> enchantmentDyes(RegistryKey<Enchantment> enchantment) 
            if (enchantmentDyesMethod_cache == null) {
                Class<?> ColorfulBooks = Class.forName("com.boyonk.colorfulbooks.ColorfulBooks");
                java.lang.reflect.Method configMethod = ColorfulBooks.getMethod("config");
                ColorfulBooksConfig_cache = configMethod.invoke(null);
                // Get the enchantmentDyes method via reflection
                enchantmentDyesMethod_cache = ColorfulBooksConfig_cache.getClass().getMethod("enchantmentDyes", net.minecraft.registry.RegistryKey.class);
            } 

            // Create a lambda that calls enchantmentDyes reflectively
            var enchantmentDyesFunc = (java.util.function.Function<Object, java.util.Optional<java.util.List<DyeItem>>>) (enchantKey) -> {
                try {
                    @SuppressWarnings("unchecked")
                    java.util.Optional<java.util.List<DyeItem>> result =
                        (java.util.Optional<java.util.List<DyeItem>>) enchantmentDyesMethod_cache.invoke(ColorfulBooksConfig_cache, enchantKey);
                    return result;
                } catch (Exception e) {
                    return java.util.Optional.empty();
                }
            };
            ItemEnchantmentsComponent component = itemStack.get(DataComponentTypes.STORED_ENCHANTMENTS);
            var newBook = DyedColorComponent.setColor(itemStack, component.getEnchantmentEntries().stream().flatMap((entry) -> {
                var enchantKey = entry.getKey().getKey();           
                return enchantKey.flatMap(enchantmentDyesFunc).stream().flatMap((dyes) -> {
                return IntStream.range(0, entry.getIntValue()).mapToObj((i) -> {
                    return dyes;
                }).flatMap(Collection::stream);
                });
            }).toList());
            var componentBuilder = ComponentChanges.builder();
            componentBuilder.add(DataComponentTypes.DYED_COLOR, newBook.get(DataComponentTypes.DYED_COLOR));
            itemStack.applyChanges(componentBuilder.build());

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void randomly(ItemStack itemStack, Random randomGenerator) {
        if (!FabricLoader.getInstance().isModLoaded("colorful_books")) return;

        final float MIN_SATURATION = 0.0f;
        final float MAX_SATURATION = 0.6f;
        final float MIN_BRIGHTNESS = 0.2f;
        final float MAX_BRIGHTNESS = 0.8f;

        var hue = randomGenerator.nextFloat();
        var saturation = MIN_SATURATION + randomGenerator.nextFloat() * (MAX_SATURATION - MIN_SATURATION); // 0 to 0.5 (0% to 50%)
        var lightness  = MIN_BRIGHTNESS + randomGenerator.nextFloat() * (MAX_BRIGHTNESS - MIN_BRIGHTNESS);

        // Convert HSL to RGB
        var q = lightness < 0.5f ? lightness * (1 + saturation) : (lightness + saturation - lightness * saturation);
        var p = 2 * lightness - q;
        var r = hueToRgb(p, q, hue + 1f / 3f);
        var g = hueToRgb(p, q, hue);
        var b = hueToRgb(p, q, hue - 1f / 3f);

        var color = ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);


        var componentBuilder = ComponentChanges.builder();
		componentBuilder.add(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color,  true));
        itemStack.applyChanges(componentBuilder.build());
    }

    final static float hueToRgb(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1f / 6f) return p + (q - p) * 6f * t;
        if (t < 1f / 2f) return q;
        if (t < 2f / 3f) return p + (q - p) * (2f / 3f - t) * 6f;
        return p;
    }

}
