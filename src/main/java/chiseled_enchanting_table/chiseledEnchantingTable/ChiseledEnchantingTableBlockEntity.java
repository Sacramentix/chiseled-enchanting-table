package chiseled_enchanting_table.chiseledEnchantingTable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.ComponentMap.Builder;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;

import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import chiseled_enchanting_table.registry.EntityRegistry;

public class ChiseledEnchantingTableBlockEntity extends BlockEntity implements Nameable {
	public FloatingBook floatingBook;
	@Nullable
	private Text customName;

	public ChiseledEnchantingTableBlockEntity(BlockPos pos, BlockState state) {
		super(EntityRegistry.CHISELED_ENCHANTING_TABLE_ENTITY_TYPE, pos, state);
		this.floatingBook = new FloatingBook();
	}

	@Override
	public void writeData(WriteView view) {
		super.writeData(view);
		if (color != -1) {
			view.putInt("color", color);
		}
		if (this.hasCustomName()) {
			view.putString("CustomName", customName.getString());
		}

	}

	@Override
	public void readData(ReadView view) {
		super.readData(view);
		color = view.getInt("color", -1);
		this.customName = tryParseCustomName(view, "CustomName");
		if (world != null) {
			world.updateListeners(pos, getCachedState(), getCachedState(), 0);
		}
	}

	public static void tick(World world, BlockPos pos, BlockState state, ChiseledEnchantingTableBlockEntity blockEntity) {
		FloatingBook.tick(world, pos, state, blockEntity.floatingBook);
	}

	public Text getName() {
		return (Text) (this.customName != null ? this.customName : Text.translatable("container.enchant"));
	}

	public void setCustomName(@Nullable Text customName) {
		this.customName = customName;
	}

	@Nullable
	public Text getCustomName() {
		return this.customName;
	}

	protected void readComponents(ComponentsAccess components) {
		super.readComponents(components);
		var dyed_color = components.get(DataComponentTypes.DYED_COLOR);
		if (dyed_color != null) {
			this.color = dyed_color.rgb();
		}
		this.customName = (Text) components.get(DataComponentTypes.CUSTOM_NAME);
	}

	public void addComponents(Builder componentMapBuilder) {
		super.addComponents(componentMapBuilder);
		componentMapBuilder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
		componentMapBuilder.add(DataComponentTypes.DYED_COLOR, new DyedColorComponent(this.color));
	}

	public ComponentMap getAllComponents() {
		var componentMapBuilder = ComponentMap.builder();
		super.addComponents(componentMapBuilder);
		componentMapBuilder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
		if (this.color != -1) componentMapBuilder.add(DataComponentTypes.DYED_COLOR, new DyedColorComponent(this.color));
		return componentMapBuilder.build();
	}

	public void removeFromCopiedStackNbt(NbtCompound nbt) {
	}

	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return createNbt(registryLookup);
	}
	
	public int color = -1;

	public static int DEFAULT_COLOR = 0xFFA020F0;

	public int colorOrDefault() {
		return color == -1 ? DEFAULT_COLOR : color;
	}

	@Override
	public @Nullable Object getRenderData() {
	  // this is the method from `RenderDataBlockEntity` class.
	  return colorOrDefault();
	}

}
