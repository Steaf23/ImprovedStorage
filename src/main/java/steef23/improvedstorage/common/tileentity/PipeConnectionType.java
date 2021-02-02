package steef23.improvedstorage.common.tileentity;

import net.minecraft.util.IStringSerializable;

public enum PipeConnectionType implements IStringSerializable
{
	INVENTORY("inventory"),	//face is connected to inventory
	PIPE("pipe"), 			//face is connected to wire 
	END("end"), 			//face is an endpoint
	NONE("none");			//face is not connected

	private final String name;
	
	
	PipeConnectionType(String name)
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
	
	public boolean isValid()
	{
		return this != NONE;
	}
}
