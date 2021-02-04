package steef23.improvedstorage.common.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
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
import net.minecraftforge.items.VanillaInventoryCodeHooks;

public abstract class AbstractItemPipeTileEntity extends TileEntity implements ITickableTileEntity
{
	public final int speed;
	public int cooldownTimer;
	public ArrayList<PipeItem> items;
	private PipeConnectionType[] faceConnections = {PipeConnectionType.NONE,
													PipeConnectionType.NONE,
													PipeConnectionType.NONE,
													PipeConnectionType.NONE,
													PipeConnectionType.NONE,
													PipeConnectionType.NONE};
	private boolean needsUpdate;
	
	public AbstractItemPipeTileEntity(TileEntityType<?> tileEntityTypeIn, int speed)
	{
		super(tileEntityTypeIn);
		this.speed = speed;
		this.items = new ArrayList<>();
		this.needsUpdate = false;
	}
	
	private void updateFaceConnections()
	{
		for (int index = 0; index < this.faceConnections.length; index++)
		{
			Direction dir = Direction.byIndex(index);
			this.faceConnections[index] = this.setConnectionType(dir);
		}
		this.markDirty();
	}
	
	// should be implemented to define when the face should be connected
	protected abstract PipeConnectionType setConnectionType(Direction face);
	
	// should be implemented to define if the ends of a pipe should bounce back items or spit them out
	protected abstract boolean doEndsBounceBack();
	
	@Override
	public void tick()
	{
		if (!world.isRemote)
		{
			this.updateFaceConnections();
			
			//INSERT INTO OTHER INVENTORIES
			Iterator<PipeItem> itr = this.items.iterator();
			while (itr.hasNext())
			{
				PipeItem item = itr.next();
				if (item.getTicksInPipe() > this.speed)
				{
					Direction target = item.getTarget();
					switch (this.getConnectionType(target))
					{
						case PIPE:
						case INVENTORY:
							if (this.sendItem(item, target))
							{
								itr.remove();
								this.needsUpdate = true;
							}
						case END:
							// TODO spit out item
							break;
						case NONE:
							break;
						default:
							break;
					}
				}	
			}
			//EXTRACT FROM OTHER INVENTORIES
			this.cooldownTimer++;
			if (this.cooldownTimer >= this.speed)
			{
				this.cooldownTimer = 0;
				for (Direction face : Direction.values())
				{
					if (this.getConnectionType(face) == PipeConnectionType.INVENTORY)
					{
						//if items have been sent out OR came in
						this.needsUpdate |= this.pullFromInventory(face);
					}
				}
			}
			if (needsUpdate)
			{
				needsUpdate = false;
				this.markDirty();
			}
		}
	}
	
	public boolean sendItem(PipeItem item, Direction outgoingFace)
	{
		TileEntity te = this.getWorld().getTileEntity(this.pos.offset(outgoingFace));
		if (!(te instanceof AbstractItemPipeTileEntity))
		{
			return VanillaInventoryCodeHooks.getItemHandler(this.world, 
															te.getPos().getX(), 
															te.getPos().getY(), 
															te.getPos().getZ(), 
															outgoingFace.getOpposite())
					.map(destinationResult ->
					{
						IItemHandlerModifiable itemHandler = (IItemHandlerModifiable)destinationResult.getKey();
						if (isInventoryFull(itemHandler))
						{
							return false;
						}
						else
						{
							if (item.isValid())
							{
								ItemStack insertStack = item.getItemStack().copy();
	                               ItemStack remainder = insertIntoHandler(itemHandler, insertStack, outgoingFace.getOpposite());
	                            if (remainder.isEmpty())
	                            {
	                                return true;
	                            }
	                            item.stack = remainder;
	                            //set source to outgoing, meaning it will bounce back
	                            item.source = outgoingFace;
	                            item.ticksInPipe = 0;
							}
							return false;
						}
					})
					.orElse(false);
		}
		else if (te instanceof AbstractItemPipeTileEntity)
		{
			((AbstractItemPipeTileEntity)te).receiveItem(item);
			return true;
		}
		return false;
	}
	
	private boolean pullFromInventory(Direction incomingFace)
	{
		TileEntity te = this.getWorld().getTileEntity(this.pos.offset(incomingFace));
		if (te != null && !(te instanceof AbstractItemPipeTileEntity))
		{
			return VanillaInventoryCodeHooks.getItemHandler(this.world, 
															te.getPos().getX(), 
															te.getPos().getY(), 
															te.getPos().getZ(), 
															incomingFace.getOpposite())
					.map(itemHandlerResult ->
					{
						IItemHandlerModifiable handler = (IItemHandlerModifiable)itemHandlerResult.getKey();
						
						for (int index = 0; index < handler.getSlots(); index++)
						{
							ItemStack extractItem = handler.extractItem(index, handler.getStackInSlot(index).getCount(), true);
							if (!extractItem.isEmpty())
							{
								extractItem = handler.extractItem(index, handler.getStackInSlot(index).getCount(), false);
								PipeItem item = new PipeItem(extractItem, incomingFace);
								this.receiveItem(item);
								this.markDirty();
								return true;
							}
						}
						return false;
					})
					.orElse(false);
		}
		return false;
	}

	public void receiveItem(PipeItem item)
	{
		item.target = setTargetFace(item.source);
		if (item.target != null)
		{
			this.items.add(item);
		}
	}

	// Direction priority for target selection (highest to lowest): Clockwise from source in order D-(S-W-N-E)-U
	public Direction setTargetFace(Direction source)
	{
		if (this.getConnectionType(Direction.DOWN) != PipeConnectionType.NONE && source != Direction.DOWN)
		{
			return Direction.DOWN;
		}
		
		Direction horizontalIndex = this.setTargetFaceHorizontal(source);
		
		if (horizontalIndex != null)
		{
			return horizontalIndex;
		}
		
		if (this.getConnectionType(Direction.UP) != PipeConnectionType.NONE && source != Direction.UP)
		{
			return Direction.UP;
		}
		
		return source;
	}
	
	public Direction setTargetFaceHorizontal(Direction source)
	{
		//if source is null, start as SOUTH
		Direction dir = source == null ? Direction.SOUTH : source;
		
		for (int i = 0; i < 4; i++)
		{
			dir = Direction.byHorizontalIndex(dir.getHorizontalIndex() + 1);
			if (this.getConnectionType(dir) != PipeConnectionType.NONE && dir != source)
			{
				return dir;
			}
		}
		return null;
	}
	
	public PipeConnectionType getConnectionType(Direction face)
	{
		return face == null ? PipeConnectionType.NONE : this.faceConnections[face.getIndex()];
	}
	
	public void dropInventory()
	{
		for (PipeItem item : this.items)
		{
	    	InventoryHelper.spawnItemStack(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), item.getItemStack());
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt)
	{
		super.write(nbt);
		ListNBT items = new ListNBT();
		this.items.forEach(item -> items.add(item.write(new CompoundNBT())));
		
		ListNBT faceConnections = new ListNBT();
		if (this.faceConnections != null)
		{
			for (PipeConnectionType connection : this.faceConnections)
			{
				CompoundNBT typeNBT = new CompoundNBT();
				typeNBT.putByte("Type", (byte)connection.getIndex());
				faceConnections.add(typeNBT);
			}
		}
		
		
		nbt.put("Items", items);
		nbt.put("Connections", faceConnections);
		return nbt;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt)
	{
		super.read(state, nbt);
		ArrayList<PipeItem> items = new ArrayList<>();
		ListNBT listNBT = nbt.getList("Items", 10);
		listNBT.forEach((item)-> items.add(PipeItem.read((CompoundNBT)item)));
		ListNBT faceConnections = nbt.getList("Connections", 10);
		for (int i = 0; i < faceConnections.size(); i++)
		{
			this.faceConnections[i] = PipeConnectionType.byIndex(((CompoundNBT)faceConnections.get(i)).getByte("Type"));
		}
		this.items = items;
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
	//contains direction for sided inventories
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
	
    private static boolean isInventoryFull(IItemHandlerModifiable itemHandler)
    {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++)
        {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.isEmpty() || stackInSlot.getCount() < itemHandler.getSlotLimit(slot))
            {
                return false;
            }
        }
        return true;
    }
	
	/* ---------------------------------------------
	 * PIPEITEM CLASS
	 * ---------------------------------------------
	 */
	
	public static class PipeItem
	{
		private ItemStack stack;
    	private int ticksInPipe;
    	private Direction target;
    	private Direction source;
    	private boolean hasSource;
    	
    	protected PipeItem(ItemStack stack, @Nullable Direction source)
    	{
    		this.ticksInPipe = 0;
    		this.stack = stack;
    		this.source = source;
    		this.hasSource = source != null;
    	}
    	
    	protected void tick()
    	{
    		ticksInPipe++;
    	}
    	
    	public boolean isValid()
    	{
    		return getItemStack().isEmpty();
    	}
    	
    	public CompoundNBT write(CompoundNBT compound)
    	{
    		getItemStack().write(compound);
    		compound.putByte("Ticks", (byte)getTicksInPipe());
    		compound.putByte("Target", (byte)target.getIndex());
    		compound.putBoolean("hasSource", hasSource);
    		if (hasSource)
    		{
    			compound.putByte("Source", (byte)source.getIndex());
    		}
    		return compound;
    	}
    	
    	public static PipeItem read(CompoundNBT compound)
    	{
    		ItemStack item = ItemStack.read(compound);
    		Direction target = Direction.byIndex(compound.getByte("Target"));
    		Direction source = compound.getBoolean("hasSource") ? Direction.byIndex(compound.getByte("Source")) : null;
    		int ticksInPipe = compound.getByte("Ticks");
    		
    		PipeItem pipeItem = new PipeItem(item, source);
    		pipeItem.ticksInPipe = ticksInPipe;
    		pipeItem.target = target;
    		return pipeItem;
    	}
    	
    	public ItemStack getItemStack()
    	{
    		return stack;
    	}
    	
    	public int getTicksInPipe()
    	{
    		return ticksInPipe;
    	}
    	
    	public Direction getTarget()
    	{
    		return target;
    	}
    	
    	public Direction getSource()
		{
			return source;
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
