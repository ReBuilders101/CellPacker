package dev.lb.cellpacker.structure;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface ByteData {
	public byte[] getData();
	public default InputStream getDataAsStream(){
		return new ByteArrayInputStream(getData());
	}
	public default int getLength(){
		return getData().length;
	}
}
