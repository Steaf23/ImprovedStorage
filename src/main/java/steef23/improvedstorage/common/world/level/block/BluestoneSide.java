package steef23.improvedstorage.common.world.level.block;

import net.minecraft.util.StringRepresentable;

public enum BluestoneSide implements StringRepresentable
{
	UP("up"),
	SIDE("side"),
	NONE("none"),
	END("end");
	
	private final String name;
	
	BluestoneSide(String name)
	{
		this.name = name;
	}
	
	public String toString()
	{
		return this.getSerializedName();
	}

	@Override
	public String getSerializedName()
	{
		return this.name;
	}
	
	public boolean isConnected()
	{
		return this != NONE;
	}
	
	public boolean isEnd()
	{
		return this == END;
	}

}
