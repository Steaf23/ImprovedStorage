package steef23.improvedstorage.common.world.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import steef23.improvedstorage.common.world.level.block.entity.StoneChestBlockEntity;
import steef23.improvedstorage.core.init.IMPSMenus;

import java.util.Objects;

public class StoneChestMenu extends AbstractContainerMenu
{
	private final Container container;
//	private final IWorldPosCallable canInteractWithCallable;
	
	public StoneChestMenu(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data)
	{
		this(IMPSMenus.STONE_CHEST.get(), windowId, playerInventory, getContainer(playerInventory, data));
	}
	
	public StoneChestMenu(MenuType<?> type, final int windowId, final Inventory playerInventory, final Container container)
	{
		super(type, windowId);
		this.container = container;
//		this.canInteractWithCallable = IWorldPosCallable.of(container.getWorld(), tileEntity.getPos());
		this.container.startOpen(playerInventory.player);
		
		//Main Inventory
		int startX = 8;
		int startY = 18;
		int slotSizePlusOffset = 18;
		for (int row = 0; row < 4; row++)
		{
			for(int column = 0; column < 9; column++)
			{
				this.addSlot(new Slot(container, (row * 9) + column, startX + (column * slotSizePlusOffset), startY + (row * slotSizePlusOffset)));
			}
		}
		
		//Main Player Inventory
		int startPlayerInvY = startY + slotSizePlusOffset * 4 + 12; //== 102
		for (int row = 0; row < 3; row++)
		{
			for (int column = 0; column < 9; column++)
			{
				this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, startX + (column * slotSizePlusOffset), startPlayerInvY + (row *  slotSizePlusOffset)));
			}
		}
		
		//Hotbar
		int hotbarY = startPlayerInvY + slotSizePlusOffset * 3 + 4;
		for (int column = 0; column < 9; column++)
		{
			this.addSlot(new Slot(playerInventory, column,  startX + (column *  slotSizePlusOffset), hotbarY));
		}
	}

	private static Container getContainer(final Inventory playerInventory, final FriendlyByteBuf data)
	{
		Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
		if(tileAtPos instanceof StoneChestBlockEntity)
		{
			return (StoneChestBlockEntity) tileAtPos;
		}
		throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
	}

//	@Override
//	public boolean canInteractWith(Player playerIn)
//	{
//		return isWithinUsableDistance(canInteractWithCallable, playerIn, IMPSBlocks.STONE_CHEST.get());
//	}
	
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
	public boolean stillValid(Player player)
	{
		return this.container.stillValid(player);
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		this.container.stopOpen(playerIn);
	}
}
