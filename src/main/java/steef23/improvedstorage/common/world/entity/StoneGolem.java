package steef23.improvedstorage.common.world.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import steef23.improvedstorage.common.world.inventory.StoneGolemMenu;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class StoneGolem extends AbstractGolem implements MenuProvider
{
	private final int inventorySize = 36;
	private final IItemHandlerModifiable inventory = getOrCreateHandler();
	private final LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> inventory);
	
	private static final double defaultMoveSpeed = 0.2D;
	private boolean isInteracting = false;

	private final ChestLidController lidController = new ChestLidController();
	
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
		this.goalSelector.addGoal(11, new StoneGolem.LookForItemsGoal());
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
		   		// should be fixed, but has the nice side effect of freezing all golems around the player
				// when they are interfacing with any stonegolem
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
	public void tick()
	{
		lidController.shouldBeOpen(this.isInteracting);
		lidController.tickLid();
//		eatItem(new ItemStack(Items.BEDROCK));
		super.tick();
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
		ItemStack itemStack = itemEntity.getItem();

		ItemStack remainder = insertItemIntoGolem(itemStack);

		if (remainder.isEmpty())
		{
			this.onItemPickup(itemEntity);
			take(itemEntity, itemStack.getCount());
			itemEntity.discard();
		}
		else
		{
			itemEntity.getItem().setCount(remainder.getCount());
		}
	}

	public ItemStack insertItemIntoGolem(ItemStack itemStack)
	{
		if (level.isClientSide)
			eatItem(itemStack);
		return ItemHandlerHelper.insertItem(this.inventory, itemStack, false);
	}

	//TODO: Fix this
	public void eatItem(ItemStack itemStack)
	{
		if (itemStack.isEmpty())
			return;

		this.playSound(SoundEvents.GRINDSTONE_USE, 1.0F, 1.0F);

		ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, itemStack);
		float angle = level.random.nextFloat() * 360;
		Vec3 origin = position().add(new Vec3(0.0, -0.5, 0.0));
		Vec3 target = new Vec3(level.random.nextFloat() * 2, level.random.nextFloat() * 2, level.random.nextFloat() * 2).add(origin);
		level.addParticle(data, origin.x, origin.y, origin.z, target.x, target.y, target.z);
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
		if (this.isAlive() && !player.isCrouching() && !this.level.isClientSide && hand == InteractionHand.MAIN_HAND)
		{
			if (!player.getItemInHand(hand).isEmpty())
			{
				int count = player.getItemInHand(hand).getCount();
				ItemStack remainder = insertItemIntoGolem(player.getItemInHand(hand));
				player.setItemInHand(hand, remainder);

				if (count == remainder.getCount())
				{
					NetworkHooks.openGui((ServerPlayer) player, this);
				}
			}
			else
			{
				NetworkHooks.openGui((ServerPlayer) player, this);
			}
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

	public float GetOpenNess(float partialTicks)
	{
		return lidController.getOpenness(partialTicks);
	}

	class LookForItemsGoal extends Goal
	{

		public LookForItemsGoal()
		{
			this.setFlags(EnumSet.of(Flag.MOVE));
		}

		@Override
		public boolean canUse()
		{
			List<ItemEntity> list = StoneGolem.this.level.getEntitiesOfClass(ItemEntity.class, StoneGolem.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));
			return list.size() > 0;
		}

		@Override
		public void tick()
		{
			List<ItemEntity> list = StoneGolem.this.level.getEntitiesOfClass(ItemEntity.class, StoneGolem.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));

			//TODO: Check if Inventory is full
			if (!list.isEmpty()) {
				StoneGolem.this.getNavigation().moveTo(list.get(0), 1.2D);
			}
		}

		@Override
		public void start()
		{
			List<ItemEntity> list = StoneGolem.this.level.getEntitiesOfClass(ItemEntity.class, StoneGolem.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));


			//TODO: Check if Inventory is full
			if (!list.isEmpty()) {
				StoneGolem.this.getNavigation().moveTo(list.get(0), 1.2D);
			}
		}
	}

}
