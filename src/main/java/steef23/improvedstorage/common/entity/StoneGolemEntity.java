package steef23.improvedstorage.common.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import steef23.improvedstorage.common.container.StoneGolemContainer;

public class StoneGolemEntity extends GolemEntity implements INamedContainerProvider
{
	private final int inventorySize = 36;
	private IItemHandlerModifiable inventory = getOrCreateHandler();
	private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> inventory);
	
	private static final double defaultMoveSpeed = (double)0.2f;
	private boolean isInteracting = false;
	
	public StoneGolemEntity(EntityType<? extends GolemEntity> type, World worldIn) 
	{
		super(type, worldIn);
		this.setCanPickUpLoot(true);
	}

	protected int getInventorySize()
	{
		return this.inventorySize;
	}
	
	@Override
	protected void registerGoals() 
	{	
		super.registerGoals();
		this.goalSelector.addGoal(1, new LookAtGoal(this, PlayerEntity.class, 6.0f));
		this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
	}
	
	public static AttributeModifierMap.MutableAttribute registerAttributes() 
	{
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 16.0D)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, defaultMoveSpeed)
				.createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, defaultMoveSpeed);
	}

	@Override
	protected void updateAITasks() 
	{
		super.updateAITasks();
	}
	
	@Override
	public void livingTick() 
	{
		//is the golem interacting with a player?
		for (PlayerEntity player : this.world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(
				   	this.getPosX() - 5.0f, 
				   	this.getPosY() - 5.0f, 
				   	this.getPosZ() - 5.0f, 
				   	this.getPosX() + 5.0f, 
				   	this.getPosY() + 5.0f, 
				   	this.getPosZ() + 5.0f)
				)) 
		   	{
				if (player.openContainer instanceof StoneGolemContainer)
				{
					this.isInteracting = true;
				}
				else
				{
					this.isInteracting = false;
				}
		   	}
		if (this.isInteracting)
		{
			this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0D);
		}
		else
		{
			this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(defaultMoveSpeed);
		}
		
		super.livingTick();
	}
	
	@Override
    protected void dropInventory() 
	{
    	if (this.inventory != null)
    	{
    		for (int i = 0; i < this.inventory.getSlots(); i++)
    		{
    			ItemStack itemstack = this.inventory.getStackInSlot(i);
    			if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack))
    			{
    				this.entityDropItem(itemstack);
    			}
    		}
    	}
    	super.dropInventory();
    }
	
	@Override
	public boolean canPickUpItem(ItemStack itemstackIn) 
	{
		return true;
	}
	
	@Override
	protected void updateEquipmentIfNeeded(ItemEntity itemEntity) 
	{
		ItemStack itemstack = itemEntity.getItem();
		int count = itemstack.getCount();
		for (int i = 0; i < this.inventory.getSlots(); i++)
		{
			ItemStack itemstackInv = this.inventory.getStackInSlot(i);
			// does the slot contain the same item with space left? Merge.
			if (itemstackInv.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(itemstackInv, itemstack) && itemstackInv.getMaxStackSize() - itemstackInv.getCount() > 0)
			{
				itemstack = mergeItemStacksWithCarry(i, itemstackInv, itemstack);
			}
			// is the inv slot empty?
			else if (itemstackInv.isEmpty())
			{
				this.inventory.insertItem(i, itemstack, false);
				break;
			}
		}
		
		this.onItemPickup(itemEntity, count);
		itemEntity.remove();
	}
	
	private ItemStack mergeItemStacksWithCarry(int slotIndex, ItemStack itemstackInv, ItemStack itemstack2) 
	{
		int additionStackCount = itemstackInv.getCount() + itemstack2.getCount();
		 int resultStackSize = additionStackCount % itemstackInv.getMaxStackSize();
		 //does the stack actually need to be NOT merged?
		 if (additionStackCount < itemstackInv.getMaxStackSize())
		 {
			 //fill up the Inventory stack
			 this.inventory.insertItem(slotIndex, new ItemStack(itemstackInv.getItem(), itemstack2.getCount()), false);
			 return ItemStack.EMPTY;
		 }
		 else
		 {
			 this.inventory.insertItem(slotIndex, new ItemStack(itemstackInv.getItem(), itemstackInv.getMaxStackSize() - itemstackInv.getCount()), false);
			 return new ItemStack(itemstackInv.getItem(), resultStackSize);
		 }
	}

	public int getFirstEmptyStackOrMerge(IItemHandlerModifiable inventory, ItemStack itemstack)
	{
		for(int i = 0; i < this.inventory.getSlots(); ++i) 
		{
	         if (this.inventory.getStackInSlot(i).isEmpty()) 
	         {
	            return i;
	         }
	      }
	      return -1;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void writeAdditional(CompoundNBT compound) 
	{
		itemHandler.ifPresent((h) -> {
			CompoundNBT nbt = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
			compound.put("Items", nbt);
		});
		super.writeAdditional(compound);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readAdditional(CompoundNBT compound) 
	{
		CompoundNBT nbt = compound.getCompound("Items");
		itemHandler.ifPresent((h) -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(nbt));
		super.readAdditional(compound);
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
	protected ActionResultType func_230254_b_(PlayerEntity player, Hand hand) 
	{
		if (this.isAlive() && !player.isSneaking() && !this.world.isRemote)
		{
			NetworkHooks.openGui((ServerPlayerEntity) player, this);
			return ActionResultType.SUCCESS;
		}
		else
		{
			return super.func_230254_b_(player, hand);
		}
	}
	
	public IItemHandlerModifiable getOrCreateHandler()
	{
		return this.inventory != null ? this.inventory : new ItemStackHandler(this.inventorySize);
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap) 
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) 
		{
			return itemHandler.cast();
		}
		return super.getCapability(cap);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity entity) 
	{
		return new StoneGolemContainer(windowId, playerInventory, this);
	}
}
