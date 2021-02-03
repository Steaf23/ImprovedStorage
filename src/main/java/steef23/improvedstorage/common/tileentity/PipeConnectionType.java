package steef23.improvedstorage.common.tileentity;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;

public enum PipeConnectionType implements IStringSerializable
{
	INVENTORY(0, "inventory"),	//face is connected to inventory
	PIPE(1, "pipe"), 			//face is connected to pipe 
	END(2, "end"), 				//face is an endpoint
	NONE(3, "none");			//face is not connected

	private final String name;
	private final int index;
	
	private static final PipeConnectionType[] VALUES = values();
	
	private static final PipeConnectionType[] BY_INDEX = Arrays.stream(VALUES).sorted(Comparator.comparingInt((type) -> {
	      return type.index;
	   })).toArray((size) -> {
	      return new PipeConnectionType[size];
	   });
	
	PipeConnectionType(int index, String name)
	{
		this.name = name;
		this.index = index;
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
	
	public int getIndex()
	{
		return this.index;
	}
	
	public static PipeConnectionType byIndex(int index)
	{
		return BY_INDEX[MathHelper.abs(index % BY_INDEX.length)];
	}
}
