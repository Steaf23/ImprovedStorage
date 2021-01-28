package steef23.improvedstorage.common.tileentity;

import java.util.ArrayList;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import steef23.improvedstorage.common.block.BluestoneSide;
import steef23.improvedstorage.common.block.BluestoneWireBlock;
import steef23.improvedstorage.core.init.IMPSTileEntities;

public class BluestoneWireTileEntity extends TileEntity implements ITickableTileEntity
{
	// amount of ticks every item spends in a wire
	private static final int SPEED = 20;
	private int cooldown = 0;
	//FIFO Linked List
	public ArrayList<WireItem> items = new ArrayList<WireItem>();
	public ArrayList<Direction> inventoryPositions = new ArrayList<Direction>();
	
	protected BluestoneWireTileEntity(TileEntityType<?> typeIn) 
	{
		super(typeIn);
//		this.items.add(new WireItem(new ItemStack(Items.DIAMOND, 1)));
	}
	
	public BluestoneWireTileEntity()
	{
		this(IMPSTileEntities.BLUESTONE_WIRE.get());
	}

	public boolean receiveFromWire(WireItem wireItem)
	{
		return this.receiveItem(wireItem.item, wireItem.getTarget());
	}
	
	/* To be called right after the item entered the wire
	 * 
	 * */
	public boolean receiveItem(ItemStack item, Direction source)
	{
		// convert itemStack to wireItem
		WireItem wireItem = new WireItem(item, this, source);
		// add item to the Linked list
		this.items.add(wireItem);
		return true;
	}
	
	@Override
	public void tick() 
	{
		BlockState state = this.world.getBlockState(getPos());
		if (!this.world.isRemote && state.getBlock() instanceof BluestoneWireBlock)
		{
			//sendItem
			for (WireItem wireItem : this.items)
			{
				wireItem.tick();
				if (wireItem.ticksInWire >= 20)
				{
					TileEntity te = this.getWorld().getTileEntity(this.getPos().offset(wireItem.target));
					if (te instanceof BluestoneWireTileEntity)
					{
						((BluestoneWireTileEntity) te).receiveFromWire(wireItem);
					}
				}
			}
			this.items.removeIf((wireItem) -> (wireItem.isDead()));
			
			//ReceiveFromInventory
			if (cooldown >= SPEED)
			{
				// get all the positions of connected inventories
				for (Direction direction : inventoryPositions)
				{
					BlockPos pos = this.getPos().offset(direction);
					TileEntity te = this.getWorld().getTileEntity(pos);
					if (!(te instanceof BluestoneWireTileEntity))
					{
						IItemHandlerModifiable itemHandler = getItemHandlerFromTileEntity(te);
						if (itemHandler != null)
						{
							//if item is succesfully extracted
							if (IntStream.range(0, itemHandler.getSlots()).anyMatch((slot) -> {
								return !itemHandler.getStackInSlot(slot).isEmpty() ? 
										this.receiveItem(itemHandler.extractItem(slot, 1, false), direction) : false;
							}))
							{
								System.out.println("ITEM EXTRACTED");
								break;
							}
						}
					}
				}
				cooldown = 0;
			}
			else
			{
				cooldown++;
			}
			
			this.markDirty();
		}
	}
	
	private IItemHandlerModifiable getItemHandlerFromTileEntity(TileEntity te)
	{
		return te instanceof IItemHandlerModifiable ? (IItemHandlerModifiable)te : te instanceof IInventory ? new InvWrapper((IInventory)te) : null; 
	}
	
	//this method updates the connected positions
	public void updateConnectedPositions(BlockState state)
	{
		ArrayList<Direction> new_positions= new ArrayList<>();
		
		if (state.get(BluestoneWireBlock.NORTH) != BluestoneSide.NONE)
		{
			new_positions.add(Direction.NORTH);
		}
		if (state.get(BluestoneWireBlock.EAST) != BluestoneSide.NONE)
		{
			new_positions.add(Direction.EAST);
		}
		if (state.get(BluestoneWireBlock.SOUTH) != BluestoneSide.NONE)
		{
			new_positions.add(Direction.SOUTH);
		}
		if (state.get(BluestoneWireBlock.WEST) != BluestoneSide.NONE)
		{
			new_positions.add(Direction.WEST);
		}
		
		this.inventoryPositions = new_positions;
	}
	
	@Override
	public void read(BlockState blockState, CompoundNBT compound) 
	{
		super.read(blockState, compound);
		ListNBT listNBT = compound.getList("Items", 10);
		
		ArrayList<WireItem> itemList = new ArrayList<>();
		
		for (int i = 0; i < listNBT.size(); ++i)
		{
			CompoundNBT wireItemNBT = listNBT.getCompound(i);
			itemList.add(WireItem.read(wireItemNBT));
		}
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) 
	{
		super.write(compound);
		ListNBT listnbt = new ListNBT();
		
		for (int i = 0; i < this.items.size(); i++)
		{
			WireItem wireItem = this.items.get(i);
			CompoundNBT wireItemNBT = new CompoundNBT();
			wireItemNBT = wireItem.write(wireItemNBT);
			listnbt.add(wireItemNBT);
		}
		
		compound.put("Items", listnbt);
		
		return compound;
	}
	
	@Override
	public void markDirty() 
	{
		super.markDirty();
		this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
	}

	@Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() 
	{
        return new SUpdateTileEntityPacket(this.getPos(), -1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) 
    {
        handleUpdateTag(this.world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
    }

    @Override
    @Nonnull
    public CompoundNBT getUpdateTag() 
    {
        return this.serializeNBT();
    }

    @Override
    public void handleUpdateTag(BlockState blockState, CompoundNBT tag) 
    {
        deserializeNBT(tag);
    }
    
    public static class WireItem
    {
    	private final ItemStack item;
    	private int ticksInWire;
    	private Direction source;
    	private Direction target;
    	private boolean dead = false;
    	
    	protected WireItem(ItemStack item, BluestoneWireTileEntity wire, Direction source)
    	{
    		this.item = item;
    		this.source = source;
    		this.target = this.setTarget(wire);
    	}
    	
    	protected void tick()
    	{
    		this.ticksInWire++;
    		if (this.ticksInWire >= 20)
    		{
    			this.dead = true;
    		}
    	}
    	
    	public boolean isDead()
    	{
    		return this.dead;
    	}
    	
    	public CompoundNBT write(CompoundNBT compound)
    	{
    		this.getItemStack().write(compound);
    		compound.putByte("Ticks", (byte)this.getTicksInWire());
    		compound.putByte("Source", (byte)this.getSource().getIndex());
    		compound.putByte("Target", (byte)this.getTarget().getIndex());
    		return compound;
    	}
    	
    	private WireItem(CompoundNBT compound)
    	{
    		this.item = ItemStack.read(compound);
    		this.ticksInWire = compound.getByte("Ticks");
    		this.source = Direction.byIndex(compound.getByte("Source"));
    		this.target = Direction.byIndex(compound.getByte("Target"));
    	}
    	
    	public static WireItem read(CompoundNBT compound)
    	{
    		return new WireItem(compound);
    	}
    	
    	public ItemStack getItemStack()
    	{
    		return this.item;
    	}
    	
    	public int getTicksInWire()
    	{
    		return this.ticksInWire;
    	}
    	
    	public Direction getSource()
    	{
    		return this.source;
    	}
    	
    	public Direction getTarget()
    	{
    		return this.target;
    	}
    	
    	public Direction setTarget(BluestoneWireTileEntity wire)
    	{
    		for (Direction dir : wire.inventoryPositions)
    		{
    			if (dir != null && dir != this.source)
    			{
    				return dir;
    			}
    		}
    		return this.source;
    	}
    }
}

