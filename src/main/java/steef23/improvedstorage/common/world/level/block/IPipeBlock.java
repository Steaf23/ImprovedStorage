package steef23.improvedstorage.common.world.level.block;

import net.minecraft.core.Direction;

public interface IPipeBlock
{
    abstract Direction getConnectedSide(Direction face);
}
