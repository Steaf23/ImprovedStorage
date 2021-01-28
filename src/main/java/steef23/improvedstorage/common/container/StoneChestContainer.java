package steef23.improvedstorage.common.container;

import java.util.Objects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import steef23.improvedstorage.common.tileentity.StoneChestTileEntity;
import steef23.improvedstorage.core.init.IMPSBlocks;
import steef23.improvedstorage.core.init.IMPSContainers;

public class StoneChestContainer extends Container
{
	public final StoneChestTileEntity tileEntity;
	private final IWorldPosCallable canInteractWithCallable;
	
	public StoneChestContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data)
	{
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}
	
	public StoneChestContainer(final int windowId, final PlayerInventory playerInventory, final StoneChestTileEntity tileEntity)
	{
		super(IMPSContainers.STONE_CHEST.get(), windowId);
		this.tileEntity = tileEntity;
		this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());
		this.tileEntity.openInventory(playerInventory.player);
		
		//Main Inventory
		int startX = 8;
		int startY = 18;
		int slotSizePlusOffset = 18;
		for (int row = 0; row < 4; row++)
		{
			for(int column = 0; column < 9; column++)
			{
				this.addSlot(new Slot(tileEntity, (row * 9) + column, startX + (column * slotSizePlusOffset), startY + (row * slotSizePlusOffset)));
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
	
	private static StoneChestTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data)
	{
		Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
		if(tileAtPos instanceof StoneChestTileEntity)
		{
			return (StoneChestTileEntity)tileAtPos;
		}
		throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) 
	{
		return isWithinUsableDistance(canInteractWithCallable, playerIn, IMPSBlocks.STONE_CHEST.get());
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
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		this.tileEntity.closeInventory(playerIn);
	}
}
