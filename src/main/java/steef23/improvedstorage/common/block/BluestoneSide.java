package steef23.improvedstorage.common.block;

import net.minecraft.util.IStringSerializable;

public enum BluestoneSide implements IStringSerializable 
{
	UP("up"),
	SIDE("side"),
	NONE("none");
	
	private final String name;
	
	private BluestoneSide(String name)
	{
		this.name = name;
	}
	
	public String toString()
	{
		return this.getString();
	}

	@Override
	public String getString()
	{
		return this.name;
	}

}
