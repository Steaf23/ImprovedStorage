package steef23.improvedstorage.common.world.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import steef23.improvedstorage.common.world.entity.StoneGolem;
import steef23.improvedstorage.core.init.IMPSMenus;

import java.util.function.Predicate;

public class StoneGolemMenu extends AbstractContainerMenu
{
	private static final float interactDistance = 5.0f;
	private StoneGolem serverGolem;
	private final IItemHandlerModifiable golemInventory;
	private final IItemHandlerModifiable playerInventory;
	
	//client constructor
	public StoneGolemMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data)
	{
		this(windowId, playerInventory, new ItemStackHandler(36), player -> true, null);
	}
	
	//server constructor
	public StoneGolemMenu(int windowId, Inventory playerInventory, StoneGolem entity)
	{
		this(windowId, playerInventory, entity.getOrCreateHandler(), player -> entity.distanceTo(player) < interactDistance, entity);
	}
	
	public StoneGolemMenu(int windowId, Inventory playerInventory, IItemHandlerModifiable entityInventory, Predicate<Player> distanceCheck, StoneGolem entity)
	{
		super(IMPSMenus.STONE_GOLEM.get(), windowId);
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
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasItem())
		{
			ItemStack itemStack1 = slot.getItem();
			itemStack = itemStack1.copy();
			if (index < 36)
			{
				if(!this.moveItemStackTo(itemStack1, 36, this.slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.moveItemStackTo(itemStack1, 0, 36, false))
			{
				return ItemStack.EMPTY;
			}

			if (itemStack1.isEmpty())
			{
				slot.set(ItemStack.EMPTY);
			}
			else
			{
				slot.setChanged();
			}
		}
		return itemStack;
	}

	@Override
	public boolean stillValid(Player playerIn)
	{
		return this.serverGolem != null && this.serverGolem.isAlive() && this.serverGolem.distanceTo(playerIn) < interactDistance;
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
