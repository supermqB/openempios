package org.openhealthtools.openpixpdq.common;

import java.io.IOException;

public interface IHL7Channel<M>
{
	M sendMessage(M message) throws IOException;
	
	M sendMessage(M message, boolean keepOpen) throws IOException;
	
	void close();
	
	boolean isOpen();
}
