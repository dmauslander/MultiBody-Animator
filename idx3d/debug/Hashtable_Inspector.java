package idx3d.debug;
import idx3d.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.applet.*;

public class Hashtable_Inspector extends InspectorFrame
{
	public Hashtable_Inspector(java.util.Hashtable hash, String id)
	{
		super(hash, id);
		addEntry(new InspectorFrameEntry(this,"int","size",hash.size()+""));
		
		java.util.Enumeration enumm=hash.keys();
		int index=0;
		Object key;
		while (enumm.hasMoreElements())
		{
			key=enumm.nextElement();
			addEntry(new InspectorFrameEntry(this,hash.get(key),key.toString()));
		}
	}
}