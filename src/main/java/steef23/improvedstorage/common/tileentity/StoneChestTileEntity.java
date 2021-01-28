package steef23.improvedstorage.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import steef23.improvedstorage.common.block.StoneChestBlock;
import steef23.improvedstorage.common.container.StoneChestContainer;
import steef23.improvedstorage.core.init.IMPSTileEntities;

public class StoneChestTileEntity extends LockableLootTileEntity implements IChestLid, ITickableTileEntity
{

	private NonNullList<ItemStack> chestContents = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);
	protected float lidAngle;
	protected float prevLidAngle;
	protected int numPlayersUsing;
	private IItemHandlerModifiable items = createHandler();
	private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);

	public StoneChestTileEntity(TileEntityType<?> typeIn) 
	{
		super(typeIn);
	}

	public StoneChestTileEntity() 
	{
		this(IMPSTileEntities.STONE_CHEST.get());
	}

	@Override
	public int getSizeInventory() 
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
	protected ITextComponent getDefaultName() 
	{
		return new TranslationTextComponent("container.improvedstorage.stone_chest");
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) 
	{
		return new StoneChestContainer(id, player, this);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) 
	{
		super.write(compound);
		if (!this.checkLootAndWrite(compound)) {
			ItemStackHelper.saveAllItems(compound, this.chestContents);
		}
		return compound;
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) 
	{
		super.read(state, compound);
		this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		if (!this.checkLootAndRead(compound)) 
		{
			ItemStackHelper.loadAllItems(compound, this.chestContents);
		}
	}
	
	@Override
	public void tick()
	{
	    this.prevLidAngle = this.lidAngle;
		if (this.numPlayersUsing > 0 && this.lidAngle == 0.0f) 
		{
			this.playSound(SoundEvents.BLOCK_CHEST_OPEN);
		}

		if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) 
		{
			float f1 = this.lidAngle;
			if (this.numPlayersUsing > 0) 
			{
				this.lidAngle += 0.1f;
			} else {
				this.lidAngle -= 0.1f;
			}

			if (this.lidAngle > 1.0F) 
			{
				this.lidAngle = 1.0F;
			}
         
			if (this.lidAngle < 0.5F && f1 >= 0.5F) 
			{
				this.playSound(SoundEvents.BLOCK_CHEST_CLOSE);
			}

			if (this.lidAngle < 0.0F) 
			{
				this.lidAngle = 0.0F;
			}
		}
	}
	
	private void playSound(SoundEvent sound) 
	{
		double dx = (double) this.pos.getX() + 0.5D;
		double dy = (double) this.pos.getY() + 0.5D;
		double dz = (double) this.pos.getZ() + 0.5D;
		this.world.playSound((PlayerEntity) null, dx, dy, dz, sound, SoundCategory.BLOCKS, 0.5f,
				this.world.rand.nextFloat() * 0.1f + 0.9f);
	}

	@Override
	public boolean receiveClientEvent(int id, int type) 
	{
		if (id == 1) 
		{
			this.numPlayersUsing = type;
			return true;
		} 
		else 
		{
			return super.receiveClientEvent(id, type);
		}
	}

	@Override
	public void openInventory(PlayerEntity player) 
	{
		if (!player.isSpectator()) 
		{
			if (this.numPlayersUsing < 0) 
			{
				this.numPlayersUsing = 0;
			}

			++this.numPlayersUsing;
			this.onOpenOrClose();
		}
	}

	@Override
	public void closeInventory(PlayerEntity player) 
	{
		if (!player.isSpectator()) 
		{
			--this.numPlayersUsing;
			this.onOpenOrClose();
		}
	}

	protected void onOpenOrClose() 
	{
		Block block = this.getBlockState().getBlock();
		if (block instanceof StoneChestBlock) 
		{
			this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
			this.world.notifyNeighborsOfStateChange(this.pos, block);
		}
	}

	public static int getPlayersUsing(IBlockReader reader, BlockPos pos) 
	{
		BlockState blockstate = reader.getBlockState(pos);
		if (blockstate.hasTileEntity()) {
			TileEntity te = reader.getTileEntity(pos);
			if (te instanceof StoneChestTileEntity) 
			{
				return ((StoneChestTileEntity) te).numPlayersUsing;
			}
		}
		return 0;
	}

	public static void swapContents(StoneChestTileEntity te, StoneChestTileEntity te2) 
	{
		NonNullList<ItemStack> list = te.getItems();
		te.setItems(te2.getItems());
		te2.setItems(list);
	}

	@Override
	public void updateContainingBlockInfo() 
	{
		super.updateContainingBlockInfo();
		if (this.itemHandler != null) {
			this.itemHandler.invalidate();
			this.itemHandler = null;
		}
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
	public void remove() 
	{
		super.remove();
		if(itemHandler != null)
		{
			itemHandler.invalidate();
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getLidAngle(float partialTicks) 
	{
		return MathHelper.lerp(partialTicks, this.prevLidAngle, this.lidAngle);
	}
}
