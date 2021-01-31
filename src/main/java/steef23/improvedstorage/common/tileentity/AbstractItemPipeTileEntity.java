package steef23.improvedstorage.common.tileentity;

import java.util.Iterator;
import java.util.LinkedList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class AbstractItemPipeTileEntity extends TileEntity implements ITickableTileEntity
{
	public final int speed;
	public int cooldownTimer;
	public LinkedList<PipeItem> items;
	private boolean[] availableFaces;
	
	public AbstractItemPipeTileEntity(TileEntityType<?> tileEntityTypeIn, int speed)
	{
		super(tileEntityTypeIn);
		this.speed = speed;
		this.items = new LinkedList<PipeItem>();
		this.availableFaces = new boolean[6];
	}
	
	private void updateConnectedFaces()
	{
		for (int index = 0; index < this.availableFaces.length; index++)
		{
			Direction dir = Direction.byIndex(index);
			this.availableFaces[index] = this.shouldFaceConnect(dir);
		}
	}
	
	// should be implemented to define when the face should be connected
	protected abstract boolean shouldFaceConnect(Direction face);
	
	@Override
	public void tick()
	{
		this.updateConnectedFaces();
		Iterator<PipeItem> itr = this.items.iterator();
		while (itr.hasNext())
		{
			PipeItem item = itr.next();
			if (item.getTicksInPipe() > 20)
			{
				if (this.sendItem(item, item.getTarget()))
				{
					itr.remove();
				}
			}	
		}
	}
	
	/* To be called right after the item entered the wire
	 * 
	 * */
	public boolean receiveItem(PipeItem item, Direction source)
	{
		// convert itemStack to wireItem
		this.items.addLast(item);
		return true;
	}
	
	public boolean sendItem(PipeItem item, Direction outgoingFace)
	{
		if (this.isfaceConnected(outgoingFace))
		{
			TileEntity te = this.getWorld().getTileEntity(pos);
			if (!(te instanceof AbstractItemPipeTileEntity))
			{
				IItemHandlerModifiable itemHandler = getItemHandlerFromTileEntity(te);
				if (itemHandler != null)
				{
					ItemStack stack = insertIntoHandler(itemHandler, item.getItemStack(), outgoingFace.getOpposite());
					if (stack.isEmpty())
					{
						te.markDirty();
						return true;
					}
					
					item.stack = stack;
					item.source = outgoingFace;
					
//					//if item is succesfully extracted
//					if (IntStream.range(0, itemHandler.getSlots()).anyMatch((slot) -> {
//						return !itemHandler.getStackInSlot(slot).isEmpty() ? 
//								this.receiveItem(itemHandler.extractItem(slot, 1, false), direction) : false;
//					}))
//					{
//						System.out.println("ITEM EXTRACTED");
//						break;
//					}
				}
			}
			else if (te instanceof AbstractItemPipeTileEntity)
			{
				((AbstractItemPipeTileEntity)te).receiveItem(item, outgoingFace);
			}
		}
		return false;
	}
	
	private IItemHandlerModifiable getItemHandlerFromTileEntity(TileEntity te)
	{
		return te instanceof IItemHandlerModifiable ? (IItemHandlerModifiable)te : te instanceof IInventory ? new InvWrapper((IInventory)te) : null; 
	}
	
	public boolean isfaceConnected(Direction face)
	{
		if(face != null)
		{
			return this.availableFaces[face.getIndex()];
		}
		return false;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt)
	{
		super.write(nbt);
		
		ListNBT listnbt = new ListNBT();
		this.items.forEach((item) -> listnbt.add(item.write(new CompoundNBT())));
		
		nbt.put("Items", listnbt);
		
		return nbt;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt)
	{
		super.read(state, nbt);
		
		ListNBT listNBT = nbt.getList("Items", 10);
		listNBT.forEach((item)-> this.items.add(PipeItem.read((CompoundNBT)item)));
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
    
	/* ---------------------------------------------
	 * INVENTORY INTERFACING
	 * ---------------------------------------------
	 */
	
	//attempts to insert item into target inventory, returns remainder
	//conatis direction for sided inventories
	private static ItemStack insertIntoHandler(IItemHandlerModifiable targetHandler, ItemStack stack, Direction direction)
	{
		if (targetHandler instanceof ISidedInventory && direction != null) 
		{
			//for every slot for the face try to insert stack
			ISidedInventory iSidedInventory = (ISidedInventory)targetHandler;
			for (int index = 0; index < iSidedInventory.getSlotsForFace(direction).length && !stack.isEmpty(); index++)
			{
				stack = insertStack(targetHandler, stack, index, direction);
			}
		} 
		else 
		{
			for(int index = 0; index < targetHandler.getSlots() && !stack.isEmpty(); index++) 
			{
				stack = insertStack(targetHandler, stack, index, direction);
			}
		}
		return stack;
	}
	
	private static ItemStack insertStack(IItemHandlerModifiable targetHandler, ItemStack stack, int index, Direction direction)
	{
		ItemStack itemstack = targetHandler.getStackInSlot(index);
		if (canInsertItemInSlot(targetHandler, stack, index, direction)) 
		{
			boolean success = false;
			if (itemstack.isEmpty()) 
			{
				targetHandler.setStackInSlot(index, stack);
            	stack = ItemStack.EMPTY;
            	success = true;
			}
			else if (canCombine(itemstack, stack)) 
			{
				int leftoverAmount = stack.getMaxStackSize() - itemstack.getCount();
				int amount = Math.min(stack.getCount(), leftoverAmount);
				stack.shrink(amount);
				itemstack.grow(amount);
				success = amount > 0;
			}
			
			if (success) 
			{
				if (targetHandler instanceof TileEntity) 
				{
					((TileEntity)targetHandler).markDirty();
				}
			}
		}
		return stack;
	}

	private static boolean canInsertItemInSlot(IItemHandlerModifiable targetHandler, ItemStack stack, int index, @Nullable Direction side) 
	{
		if (!targetHandler.isItemValid(index, stack)) 
		{
			return false;
		} else 
		{
			return !(targetHandler instanceof ISidedInventory) || ((ISidedInventory)targetHandler).canInsertItem(index, stack, side);
		}
	}
	
	private static boolean canCombine(ItemStack stack1, ItemStack stack2) 
	{
		if (stack1.getItem() != stack2.getItem()) 
		{
			return false;
		} 
		else if (stack1.getDamage() != stack2.getDamage()) 
		{
			return false;
		} 
		else if (stack1.getCount() > stack1.getMaxStackSize()) 
		{
			return false;
		} 
		else 
		{
			return ItemStack.areItemStackTagsEqual(stack1, stack2);
		}
	}
	
	/* ---------------------------------------------
	 * PIPEITEM CLASS
	 * ---------------------------------------------
	 */
	
	public static class PipeItem
	{
		private ItemStack stack;
    	private int ticksInPipe;
    	private Direction source;
    	private Direction target;
    	private boolean dead = false;
    	
    	protected PipeItem(ItemStack stack, Direction source)
    	{
    		this.ticksInPipe = 0;
    		this.stack = stack;
    		this.source = source;
    	}
    	
    	protected void tick()
    	{
    		this.ticksInPipe++;
    		if (this.ticksInPipe >= 20)
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
    		compound.putByte("Ticks", (byte)this.getTicksInPipe());
    		compound.putByte("Source", (byte)this.getSource().getIndex());
    		compound.putByte("Target", (byte)this.getTarget().getIndex());
    		return compound;
    	}
    	
    	public static PipeItem read(CompoundNBT compound)
    	{
    		ItemStack item = ItemStack.read(compound);
    		Direction source = Direction.byIndex(compound.getByte("Source"));
    		Direction target = Direction.byIndex(compound.getByte("Target"));
    		int ticksInPipe = compound.getByte("Ticks");
    		
    		PipeItem pipeItem = new PipeItem(item, source);
    		pipeItem.ticksInPipe = ticksInPipe;
    		pipeItem.target = target;
    		return pipeItem;
    	}
    	
    	public ItemStack getItemStack()
    	{
    		return this.stack;
    	}
    	
    	public int getTicksInPipe()
    	{
    		return this.ticksInPipe;
    	}
    	
    	public Direction getSource()
    	{
    		return this.source;
    	}
    	
    	public Direction getTarget()
    	{
    		return this.target;
    	}
    	
    	public PipeItem getWithTarget(Direction target)
    	{
    		this.target = target;
    		return this;
    	}
    	
    	public PipeItem getWithTicks(int ticks)
    	{
    		this.ticksInPipe = ticks;
    		return this;
    	}
	}
}
