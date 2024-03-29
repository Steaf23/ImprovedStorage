package steef23.improvedstorage.common.world.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import steef23.improvedstorage.common.world.inventory.StoneGolemMenu;

import javax.annotation.Nullable;
import java.util.Objects;

public class StoneGolem extends AbstractGolem implements MenuProvider
{
	private final int inventorySize = 36;
	private final IItemHandlerModifiable inventory = getOrCreateHandler();
	private final LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> inventory);
	
	private static final double defaultMoveSpeed = 0.2D;
	private boolean isInteracting = false;
	
	public StoneGolem(EntityType<? extends StoneGolem> type, Level worldIn)
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
		this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 6.0f));
		this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.6D));
	}

	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 16.0D)
				.add(Attributes.MOVEMENT_SPEED, defaultMoveSpeed)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
	}

	
	@Override
	public void aiStep()
	{
		super.aiStep();
		//is the golem interacting with a player?
		for (Player player : this.level.getEntitiesOfClass(Player.class, new AABB(
				   	this.getX() - 5.0f,
				   	this.getY() - 5.0f,
				   	this.getZ() - 5.0f,
				   	this.getX() + 5.0f,
				   	this.getY() + 5.0f,
				   	this.getZ() + 5.0f)
				)) 
		   	{
				this.isInteracting = player.containerMenu instanceof StoneGolemMenu;
		   	}
		if (this.isInteracting)
		{
			Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.0D);
		}
		else
		{
			Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(defaultMoveSpeed);
		}
	}
	
	@Override
    protected void dropEquipment()
	{
    	if (this.inventory != null)
		{
			for (int i = 0; i < this.inventory.getSlots(); i++)
			{
				ItemStack itemstack = this.inventory.getStackInSlot(i);
				if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack))
				{
					this.spawnAtLocation(itemstack);
				}
			}
		}
    }
	
	@Override
	protected void pickUpItem(ItemEntity itemEntity)
	{
		ItemStack itemstack = itemEntity.getItem();
		int count = itemstack.getCount();
		for (int i = 0; i < this.inventory.getSlots(); i++)
		{
			ItemStack itemstackInv = this.inventory.getStackInSlot(i);
			// does the slot contain the same item with space left? Merge.
			if (itemstackInv.sameItem(itemstack) && ItemStack.isSameItemSameTags(itemstackInv, itemstack) && itemstackInv.getMaxStackSize() - itemstackInv.getCount() > 0)
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
		
		this.onItemPickup(itemEntity);
		itemEntity.discard();
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
	public void addAdditionalSaveData(CompoundTag compound)
	{
		super.addAdditionalSaveData(compound);
		itemHandler.ifPresent((h) -> {
			CompoundTag nbt = ((INBTSerializable<CompoundTag>)h).serializeNBT();
			compound.put("Items", nbt);
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readAdditionalSaveData(CompoundTag compound)
	{
		super.readAdditionalSaveData(compound);
		CompoundTag nbt = compound.getCompound("Items");
		itemHandler.ifPresent((h) -> ((INBTSerializable<CompoundTag>)h).deserializeNBT(nbt));
    }

	@Override
	public void die(DamageSource source)
	{
		if(itemHandler != null)
		{
			itemHandler.invalidate();
		}
		super.die(source);
	}		

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		if (this.isAlive() && !player.isCrouching() && !this.level.isClientSide)
		{
			NetworkHooks.openGui((ServerPlayer) player, this);
			return InteractionResult.SUCCESS;
		}
		else
		{
			return super.mobInteract(player, hand);
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

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player)
	{
		return new StoneGolemMenu(windowId, playerInventory, this);
	}

}
