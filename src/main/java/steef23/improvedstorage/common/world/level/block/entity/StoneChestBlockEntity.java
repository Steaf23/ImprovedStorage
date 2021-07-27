package steef23.improvedstorage.common.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import steef23.improvedstorage.common.world.inventory.StoneChestMenu;
import steef23.improvedstorage.core.init.IMPSBlockEntities;
import steef23.improvedstorage.core.init.IMPSBlocks;
import steef23.improvedstorage.core.init.IMPSMenus;

public class StoneChestBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity
{

	private NonNullList<ItemStack> chestContents = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);
	protected float lidAngle;
	protected float prevLidAngle;
	protected final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state)
		{
			StoneChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_OPEN);
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state)
		{
			StoneChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_CLOSE);
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int p_155466_, int id)
		{
			level.blockEvent(pos, IMPSBlocks.STONE_CHEST.get(), 1, id);
		}

		@Override
		protected boolean isOwnContainer(Player p_155451_)
		{
			return false;
		}
	};
	private IItemHandlerModifiable items = createHandler();
	private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);

	public StoneChestBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state)
	{
		super(typeIn, pos, state);
	}

	public StoneChestBlockEntity(BlockPos pos, BlockState state)
	{
		this(IMPSBlockEntities.STONE_CHEST.get(), pos, state);
	}

	@Override
	public int getContainerSize()
	{
		return this.chestContents.size();
	}

	@Override
	public NonNullList<ItemStack> getItems() 
	{
		return this.chestContents;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> itemsIn) 
	{
		this.chestContents = itemsIn;
	}

	@Override
	protected Component getDefaultName()
	{
		return new TranslatableComponent("container.improvedstorage.stone_chest");
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory player)
	{
		return new StoneChestMenu(IMPSMenus.STONE_CHEST.get(), id, player, this);
	}

	@Override
	public CompoundTag save(CompoundTag compound)
	{
		super.save(compound);
		if (!this.trySaveLootTable(compound)) {
			ContainerHelper.saveAllItems(compound, this.chestContents);
		}
		return compound;
	}

	@Override
	public void load(CompoundTag compound)
	{
		super.load(compound);
		this.chestContents = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(compound))
		{
			ContainerHelper.loadAllItems(compound, this.chestContents);
		}
	}
	
	static void playSound(Level level, BlockPos pos, BlockState state, SoundEvent sound)
	{

		double dx = (double) pos.getX() + 0.5D;
		double dy = (double) pos.getY() + 0.5D;
		double dz = (double) pos.getZ() + 0.5D;

		level.playSound((Player) null, dx, dy, dz, sound, SoundSource.BLOCKS, 0.5f,
				level.random.nextFloat() * 0.1f + 0.9f);
	}

	@Override
	public void startOpen(Player player)
	{
		if (!this.remove && !player.isSpectator())
		{
			assert this.level != null;
			this.openersCounter.incrementOpeners(player, this.level, this.getBlockPos(), this.getBlockState());
		}
	}

	@Override
	public void stopOpen(Player player)
	{
		if (!this.remove && !player.isSpectator())
		{
			assert this.level != null;
			this.openersCounter.decrementOpeners(player, this.level, this.getBlockPos(), this.getBlockState());
		}
	}

	public static int getOpenCount(BlockGetter reader, BlockPos pos)
	{
		BlockState blockstate = reader.getBlockState(pos);
		if (blockstate.hasBlockEntity()) {
			BlockEntity te = reader.getBlockEntity(pos);
			if (te instanceof StoneChestBlockEntity)
			{
				return ((StoneChestBlockEntity) te).openersCounter.getOpenerCount();
			}
		}
		return 0;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) 
		{
			return itemHandler.cast();
		}
		return super.getCapability(cap, side);
	}

	private IItemHandlerModifiable createHandler() 
	{
		return new InvWrapper(this);
	}

	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		if(itemHandler != null)
		{
			itemHandler.invalidate();
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getOpenNess(float partialTicks)
	{
		return Mth.lerp(partialTicks, this.prevLidAngle, this.lidAngle);
	}

	protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int p_155336_, int id) {
		Block block = state.getBlock();
		level.blockEvent(pos, block, 1, id);
	}
}
