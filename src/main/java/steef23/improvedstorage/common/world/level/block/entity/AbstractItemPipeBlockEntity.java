package steef23.improvedstorage.common.world.level.block.entity;

import com.mojang.math.Vector3d;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

public abstract class AbstractItemPipeBlockEntity extends BlockEntity implements BlockEntityTicker<AbstractItemPipeBlockEntity>
{
	public int cooldownTimer;
	public ArrayList<PipeItem> items;
	private boolean needsUpdate;

	public AbstractItemPipeBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState)
	{
		super(tileEntityTypeIn, blockPos, blockState);
		this.items = new ArrayList<>();
		this.needsUpdate = false;
	}

	public abstract boolean canBeBlocked();

	public abstract boolean isSideConnected(Direction direction);

	public abstract int getSpeed();

	@Override
	public void tick(Level level, BlockPos blockPos, BlockState blockState, AbstractItemPipeBlockEntity blockEntity)
	{
		assert level != null;
		if (!level.isClientSide)
		{
			//INSERT INTO OTHER INVENTORIES
			for (PipeItem item : this.items)
			{
				if (item.getTicksInPipe() >= this.getSpeed() && !item.isRemoved)
				{
					Direction target = item.getTarget();
					switch (this.sendItem(item, target))
					{
						case SUCCESS, PASS -> {
							item = item.remove();
							this.needsUpdate = true;
						}
						default -> resetTargets();
					}
				}
			}

			//EXTRACT FROM OTHER INVENTORIES
			this.cooldownTimer++;
			if (this.cooldownTimer >= this.getSpeed())
			{
				this.cooldownTimer = 0;
				for (Direction face : Direction.values())
				{
					if (this.isSideConnected(face))
					{
						//if items have been sent out OR came in
						this.needsUpdate |= this.pullFromInventory(face);
					}
				}
			}

			this.items.removeIf(item -> item.isRemoved);

			if (needsUpdate)
			{
				needsUpdate = false;
				this.setChanged();
			}
		}

		for (PipeItem item : this.items)
		{
			item.tick();
		}
	}

	public InteractionResult sendItem(PipeItem item, Direction outgoingFace)
	{
		assert this.level != null;
		BlockEntity te = this.level.getBlockEntity(this.getBlockPos().relative(outgoingFace));
		if (te != null)
		{
			if (!(te instanceof AbstractItemPipeBlockEntity))
			{
				Optional<Pair<IItemHandler, Object>> optional = VanillaInventoryCodeHooks.getItemHandler(this.level,
						te.getBlockPos().getX(),
						te.getBlockPos().getY(),
						te.getBlockPos().getZ(),
						outgoingFace.getOpposite());

				if (optional.isPresent())
				{
					IItemHandlerModifiable handler = (IItemHandlerModifiable)optional.get().getKey();

					if (!isInventoryFull(handler))
					{
						if (item.isValid())
						{
							ItemStack insertStack = item.getItemStack().copy();
							ItemStack remainder = insertIntoHandler(handler, insertStack, outgoingFace.getOpposite());
							if (remainder.isEmpty())
							{
								item.remove();
								return InteractionResult.SUCCESS;
							}
							item.stack = remainder;
							//set source to outgoing, meaning it will bounce back
							item.source = outgoingFace;
							item.ticksInPipe = 0;
						}
					}
					return InteractionResult.FAIL;
				}
				return InteractionResult.FAIL;
			}
			else
			{
				item.source = outgoingFace.getOpposite();
				item.ticksInPipe = 0;
				((AbstractItemPipeBlockEntity)te).receiveItem(item.getCopy());
				return InteractionResult.PASS;
			}
		}

		//spit out item if target is set to a "NONE" face and it isn't blocked
		BlockState state = this.level.getBlockState(this.getBlockPos().relative(outgoingFace));
		//if its not 
		if (!(state.isFaceSturdy(this.level, this.getBlockPos(), outgoingFace.getOpposite()) && canBeBlocked()))
		{
			this.dropItem(item, null);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	private boolean pullFromInventory(Direction incomingFace)
	{
		assert this.level != null;
		BlockEntity te = this.level.getBlockEntity(this.getBlockPos().relative(incomingFace));
		if (te != null && !(te instanceof AbstractItemPipeBlockEntity))
		{
			Optional<Pair<IItemHandler, Object>> optional = VanillaInventoryCodeHooks.getItemHandler(this.level,
					te.getBlockPos().getX(),
					te.getBlockPos().getY(),
					te.getBlockPos().getZ(),
					incomingFace.getOpposite());

			if (optional.isPresent())
			{
				IItemHandlerModifiable handler = (IItemHandlerModifiable)optional.get().getKey();

				for (int index = 0; index < handler.getSlots(); index++)
				{
					ItemStack extractItem = handler.extractItem(index, handler.getStackInSlot(index).getCount(), true);
					if (!extractItem.isEmpty())
					{
						extractItem = handler.extractItem(index, handler.getStackInSlot(index).getCount(), false);
						this.receiveItemStack(extractItem, incomingFace);
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}

	public void receiveItemStack(ItemStack stack, @Nullable Direction source)
	{
		this.receiveItem(new PipeItem(stack, source));
	}

	public void receiveItem(PipeItem item)
	{
		item.target = getTargetFace(item.source);
		if (item.target != null)
		{
			this.items.add(item);
		}
		this.setChanged();
	}

	/*
	 * Direction priority for target selection (highest to lowest): Clockwise from source in order D-(S-W-N-E)-U.
	 * Override in subclass to set target to be spat out if desired by setting target to a BluestoneSide.NONE face.
	 */
	public Direction getTargetFace(Direction source)
	{
		if (source != null)
		{
			if (this.isSideConnected(Direction.DOWN) && source != Direction.DOWN)
			{
				return Direction.DOWN;
			}

			Direction horizontalIndex = this.getTargetFaceHorizontal(source);

			if (horizontalIndex != null)
			{
				return horizontalIndex;
			}

			if (this.isSideConnected(Direction.UP) && source != Direction.UP)
			{
				return Direction.UP;
			}
		}

		return source;
	}

	public Direction getTargetFaceHorizontal(@Nonnull Direction source)
	{
		Direction dir = source;

		for (int i = 0; i < 4; i++)
		{
			dir = dir.getClockWise();
			if (this.isSideConnected(dir) && dir != source)
			{
				return dir;
			}
		}
		return null;
	}

	public void resetTargets()
	{
		for (PipeItem item : this.items)
		{
			item.target = this.getTargetFace(item.source);
		}
	}

	public void dropInventory()
	{
		for (PipeItem item : this.items)
		{
			this.dropItem(item, null);
		}
	}

	public void dropItem(PipeItem item, @Nullable Vector3d addPos)
	{
		if (item == null)
		{
			if (this.items.isEmpty()) return;
			item = this.items.get(this.items.size() - 1);
		}
		if (addPos == null)
		{
			addPos = new Vector3d(0.0 ,0.0, 0.0);
		}
		assert this.level != null;
		Containers.dropItemStack(this.level,
				this.getBlockPos().getX() + addPos.x,
				this.getBlockPos().getY() + addPos.y,
				this.getBlockPos().getZ() + addPos.z,
				item.getItemStack());
		item = item.remove();
		this.setChanged();
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		ArrayList<PipeItem> items = new ArrayList<>();
		ListTag listNBT = tag.getList("Items", 10);
		listNBT.forEach((item)-> items.add(PipeItem.load((CompoundTag)item)));
		this.items = items;
	}

	@Override
	public CompoundTag save(CompoundTag nbt)
	{
		super.save(nbt);
		ListTag items = new ListTag();
		this.items.forEach(item -> items.add(item.save(new CompoundTag())));
		nbt.put("Items", items);
		return nbt;
	}

	@Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
        return new ClientboundBlockEntityDataPacket(this.getBlockPos(), -1, this.getUpdateTag());
    }

    @Override
    @Nonnull
    public CompoundTag getUpdateTag()
    {
        return this.serializeNBT();
    }
    
	/* ---------------------------------------------
	 * INVENTORY INTERFACING
	 * ---------------------------------------------
	 */
	
	//attempts to insert item into target inventory, returns remainder
	//contains direction for sided inventories
	private static ItemStack insertIntoHandler(IItemHandlerModifiable targetHandler, ItemStack stack, Direction direction)
	{
		if (targetHandler instanceof SidedInvWrapper iSidedInventory && direction != null)
		{
			//for every slot for the face try to insert stack
			for (int index = 0; index < iSidedInventory.getSlots() && !stack.isEmpty(); index++)
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
//		if (canInsertItemInSlot(targetHandler, stack, index, direction))
//		{
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
			if (targetHandler instanceof BlockEntity)
			{
				((BlockEntity)targetHandler).setChanged();
			}
		}
//		}
		return stack;
	}

	private static boolean canInsertItemInSlot(IItemHandlerModifiable targetHandler, ItemStack stack, int index, @Nullable Direction side)
	{
		return targetHandler.isItemValid(index, stack);
	}
	
	private static boolean canCombine(ItemStack stack1, ItemStack stack2) 
	{
		if (stack1.getItem() != stack2.getItem()) 
		{
			return false;
		} 
		else if (stack1.getDamageValue() != stack2.getDamageValue())
		{
			return false;
		} 
		else if (stack1.getCount() > stack1.getMaxStackSize()) 
		{
			return false;
		} 
		else 
		{
			return ItemStack.isSameItemSameTags(stack1, stack2);
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
    	private boolean isRemoved;
    	
    	protected PipeItem(ItemStack stack, @Nullable Direction source)
    	{
    		this.ticksInPipe = 0;
    		this.stack = stack;
    		this.source = source == null ? Direction.SOUTH : source;
    		this.isRemoved = false;
    	}
    	
    	protected void tick()
    	{
    		ticksInPipe++;
    	}
    	
    	public boolean isValid()
    	{
    		return !getItemStack().isEmpty();
    	}
    	
    	public CompoundTag save(CompoundTag tag)
    	{
    		getItemStack().save(tag);
    		tag.putByte("Ticks", (byte)getTicksInPipe());
    		tag.putByte("Target", (byte)target.get3DDataValue());
    		tag.putByte("Source", (byte)source.get3DDataValue());
    		return tag;
    	}
    	
    	public static PipeItem load(CompoundTag tag)
    	{
    		ItemStack item = ItemStack.of(tag);
    		Direction target = Direction.from3DDataValue(tag.getByte("Target"));
    		Direction source = Direction.from3DDataValue(tag.getByte("Source"));
    		int ticksInPipe = tag.getByte("Ticks");
    		
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
    	
    	public boolean isRemoved()
		{
			return isRemoved;
		}
    	
    	public PipeItem remove()
		{
			this.isRemoved = true;
			return this;
		}
    	
    	public PipeItem getCopy()
    	{
    		return new PipeItem(this.stack, this.source).getWithTarget(this.target).getWithTicks(this.ticksInPipe);
    	}
	}
}
