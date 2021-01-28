package steef23.improvedstorage.common.container;

import java.util.function.Predicate;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import steef23.improvedstorage.common.entity.StoneGolemEntity;
import steef23.improvedstorage.core.init.IMPSContainers;

public class StoneGolemContainer extends Container
{
	private static final float interactDistance = 5.0f;
	private StoneGolemEntity serverGolem;
	private final IItemHandlerModifiable golemInventory;
	private final IItemHandlerModifiable playerInventory;
	
	//client constructor
	public StoneGolemContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data)
	{
		this(windowId, playerInventory, new ItemStackHandler(36), player -> true, null);
	}
	
	//server constructor
	public StoneGolemContainer(int windowId, PlayerInventory playerInventory, StoneGolemEntity entity) 
	{
		this(windowId, playerInventory, entity.getOrCreateHandler(), player -> entity.getDistance(player) < interactDistance, entity);
	}
	
	public StoneGolemContainer(int windowId, PlayerInventory playerInventory, IItemHandlerModifiable entityInventory, Predicate<PlayerEntity> distanceCheck, StoneGolemEntity entity)
	{
		super(IMPSContainers.GOLEM_CONTAINER.get(), windowId);
		//if the client constructor was called
		if (entity != null)
		{
			this.serverGolem = entity;
		}
		this.golemInventory = entityInventory;
		this.playerInventory = new InvWrapper(playerInventory);
		
		int startX = 8;
		int startY = 18;
		int startPlayerInvY = startY + 84;
		this.addSlotBox(this.golemInventory, startX, startY, 4, 9, 0);
		this.addPlayerInventory(startX, startPlayerInvY);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemStack1 = slot.getStack();
			itemStack = itemStack1.copy();
			if (index < 36) 
			{
				if(!this.mergeItemStack(itemStack1, 36, this.inventorySlots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.mergeItemStack(itemStack1, 0, 36, false))
			{
				return ItemStack.EMPTY;
			}
			
			if (itemStack1.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}
		}
		return itemStack;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) 
	{
		return this.serverGolem != null && this.serverGolem.isAlive() && this.serverGolem.getDistance(playerIn) < interactDistance;
	}
	
	private void addSlotBar(IItemHandlerModifiable itemHandler, int startX, int startY, int size, int startIndex)
	{
		int slotOffset = 18;
		for (int i = 0; i < size; i++)
		{
			this.addSlot(new SlotItemHandler(itemHandler, startIndex + i, startX + (slotOffset * i), startY));
		}
	}
	
	private void addSlotBox(IItemHandlerModifiable itemHandler, int startX, int startY, int height, int width, int startIndex)
	{
		int slotOffset = 18;
		for (int i = 0; i < height; i++)
		{
			this.addSlotBar(itemHandler, startX, startY + (i * slotOffset), width, startIndex + (i * width));
		}
	}
	
	private void addPlayerInventory(int startX, int startY)
	{
		addSlotBox(this.playerInventory, startX, startY, 3, 9, 9);
		int startYHotbar = startY + 58;
		addSlotBar(this.playerInventory, startX, startYHotbar, 9, 0);
	}
}
