package idx3d_axes;

import java.awt.*;

/** Basic test of idx3d functionality -- this program makes an XYZ axis
 * and then moves it and the camera in various ways to learn some
 * of the basic geometry of how idx3d works.
 * @author DMAuslander, July 17, 2009
 */
public class Main extends Frame
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        new Main();
    }

	public Main()
	{
		this.setSize(700,700);
		this.setTitle("XYZ Axes");
		this.setLocation(200,200);
		this.add(new AxesComponent());
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
