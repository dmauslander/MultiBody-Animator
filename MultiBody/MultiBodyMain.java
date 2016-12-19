/** Multi-Body 3D Animation Based on the idx3d software base
 *  copyright (c) 2016 D. M. Auslander, University of California, Berkeley
 *  All rights reserved
 *  contact dma@me.berkeley.edu if you'd like to use this software.
 */

package MultiBody;

import java.awt.*;
//import idx3d.*;

public class MultiBodyMain extends Frame
{
	public static void main(String[] args)
	{
		MultiBodyMain mb =  new MultiBodyMain();
	}

	public MultiBodyMain()
	{
		this.setSize(1000,1000);
		this.setTitle("MultiBody");
		this.setLocation(800,20);
		this.add(new MultiBodyRun());
		this.setVisible(true);
		this.toFront();	
	}
	
	public boolean handleEvent(Event evt)
	{
		if (evt.id==Event.WINDOW_DESTROY)
		{
			System.exit(0);
		}
		//return super.processEvent(evt);
        return super.handleEvent(evt);
	}
}