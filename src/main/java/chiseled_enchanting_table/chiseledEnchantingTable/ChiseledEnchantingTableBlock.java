package chiseled_enchanting_table.chiseledEnchantingTable;

import com.mojang.serialization.MapCodec;

import chiseled_enchanting_table.chiseledEnchantingTable.ChiseledEnchantingTableScreenHandler.AvailableEnchantmentPayload;
import chiseled_enchanting_table.registry.EntityRegistry;
import chiseled_enchanting_table.utils.EnchantmentFinder;
import chiseled_enchanting_table.utils.BlockPosStream;
import chiseled_enchanting_table.utils.EnchantmentWithLevel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.Nameable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import net.minecraft.util.math.ColorHelper;

public class ChiseledEnchantingTableBlock extends BlockWithEntity {

	public static final MapCodec<ChiseledEnchantingTableBlock> CODEC = createCodec(ChiseledEnchantingTableBlock::new);

	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

	public static final List<BlockPos> POWER_PROVIDER_OFFSETS = BlockPos.stream(-2, 0, -2, 2, 1, 2).filter((pos) -> {
		return Math.abs(pos.getX()) == 2 || Math.abs(pos.getZ()) == 2;
	}).map(BlockPos::toImmutable).toList();

	public MapCodec<ChiseledEnchantingTableBlock> getCodec() {
		return CODEC;
	}

	public ChiseledEnchantingTableBlock(Settings settings) {
		super(settings);
	}

	public static boolean canAccessPowerProvider(World world, BlockPos tablePos, BlockPos providerOffset) {
		return false;
	}

	protected boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		// var nbt = itemStack.getComponentChanges().entrySet().;
		// var e = world.getBlockEntity(pos);
		// if (e instanceof ChiseledEnchantingTableBlockEntity cetbe) {
		// 	// cetbe.updateColorFromNbt(nbt);
		// 	e.markDirty();
		// }
		
	}
	@Override
	public void afterBreak(
		World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity,
		ItemStack tool
	) {
		player.incrementStat(Stats.MINED.getOrCreateStat(this));
		player.addExhaustion(0.005F);
		if (
			blockEntity != null &&
			world instanceof ServerWorld serverWorld &&
			blockEntity instanceof ChiseledEnchantingTableBlockEntity cetbe
		) {
			var components = cetbe.getAllComponents();
			var builder = ComponentChanges.builder();
			components.forEach(c->builder.add(c));
			var stack = new ItemStack(
				world.getRegistryManager().get(RegistryKeys.ITEM).getEntry(
					Identifier.of("chiseled_enchanting_table", "chiseled_enchanting_table")
				).get(), 1, builder.build()
			);
			dropStack(serverWorld, pos, stack);
			state.onStacksDropped((ServerWorld)world, pos, tool, true);		
		}
		// super.afterBreak(world, player, pos, state, blockEntity, tool);
	}



	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		super.randomDisplayTick(state, world, pos, random);
		var var5 = POWER_PROVIDER_OFFSETS.iterator();

		while (var5.hasNext()) {
			BlockPos blockPos = (BlockPos) var5.next();
			if (random.nextInt(16) == 0 && canAccessPowerProvider(world, pos, blockPos)) {
				world.addParticle(ParticleTypes.ENCHANT, (double) pos.getX() + 0.5D, (double) pos.getY() + 2.0D,
						(double) pos.getZ() + 0.5D, (double) ((float) blockPos.getX() + random.nextFloat()) - 0.5D,
						(double) ((float) blockPos.getY() - random.nextFloat() - 1.0F),
						(double) ((float) blockPos.getZ() + random.nextFloat()) - 0.5D);
			}
		}

	}

	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ChiseledEnchantingTableBlockEntity(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
			BlockEntityType<T> type) {
		return world.isClient ? validateTicker(type, EntityRegistry.CHISELED_ENCHANTING_TABLE_ENTITY_TYPE , ChiseledEnchantingTableBlockEntity::tick)
				: null;
	}

	@Override
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (stack.getItem() instanceof DyeItem dyeItem) {
			if (world.getBlockEntity(pos) instanceof ChiseledEnchantingTableBlockEntity colorBlockEntity) {
				var block = colorBlockEntity;
				final int newColor = dyeItem.getColor().getEntityColor();
				final int originalColor = colorBlockEntity.colorOrDefault() | 0xFF000000;
				block.color = ColorHelper.Argb.averageArgb(newColor, originalColor) & 0x00FFFFFF; 
				stack.decrementUnlessCreative(1, player);
				block.markDirty();
				world.updateListeners(pos, state, state, 0);
				return ItemActionResult.SUCCESS;
			}
		}
		
		return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
	}

	public static int fullAlpha(int argb) {

		return argb | 0xFF000000;
	}

	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			player.openHandledScreen(createChiseledEnchantingTableScreenHandlerFactory(state, world, pos));
			return ActionResult.CONSUME;
		}
	}

	@Nullable
	protected NamedScreenHandlerFactory createChiseledEnchantingTableScreenHandlerFactory(BlockState state, World world, BlockPos pos) {

		var blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof ChiseledEnchantingTableBlockEntity)) return null;
		Text text = ((Nameable) blockEntity).getDisplayName();
		var unlocked_enchantements = this.get_unlocked_enchantements(state, world, pos);
		var enchantments_unlocked_by_default = this.enchantments_unlocked_by_default();
		var available_enchantments = 
			Stream.concat(
				unlocked_enchantements.stream(),
				enchantments_unlocked_by_default.stream()
			).collect(Collectors.toSet());

		return new ExtendedScreenHandlerFactory<>() {
			@Override
			public AvailableEnchantmentPayload getScreenOpeningData(ServerPlayerEntity player) {
				return new AvailableEnchantmentPayload(available_enchantments);
			}


			@Override
			public Text getDisplayName() {
				return text;
			}

			@Nullable
			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
				return new ChiseledEnchantingTableScreenHandler(syncId, playerInventory, new AvailableEnchantmentPayload(available_enchantments));
			}
		};
	}

	public Set<EnchantmentWithLevel> enchantments_unlocked_by_default() {
		Set<EnchantmentWithLevel> defaultEnchantments = new HashSet<>();
		
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("protection"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("projectile_protection"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("fire_protection"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("blast_protection"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("efficiency"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("sharpness"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("smite"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("bane_of_arthropods"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("smite"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("power"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("density"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("breach"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("impaling"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("piercing"), 1));
		defaultEnchantments.add(new EnchantmentWithLevel(Identifier.ofVanilla("unbreaking"), 1));
		return defaultEnchantments;
	}

	public Set<EnchantmentWithLevel> get_unlocked_enchantements(BlockState state, World world, BlockPos pos) {
		return BlockPosStream.streamPosInRadius(pos,2)
			.flatMap((p)->{
				var be = world.getBlockEntity(p);
				if (!(be instanceof ChiseledBookshelfBlockEntity cbsbe)) return Stream.empty();
				return EnchantmentFinder.streamAllEnchantements(cbsbe, world);
			}).collect(Collectors.toSet());
	}

	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}

}
