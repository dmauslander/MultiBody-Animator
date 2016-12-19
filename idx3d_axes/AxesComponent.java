package idx3d_axes;

import idx3d.*;
import java.awt.*;
import java.io.*;
import java.util.*;



/**
 *
 * @author DMA
 */
public class AxesComponent extends Panel implements Runnable
{
	private Thread idx_Thread;
	idx3d_Scene scene;
	boolean initialized=false;
	boolean antialias=false;

	int oldx=0;
	int oldy=0;
	boolean autorotation=true;
    XYZAxes xyz = null;
    float axisLength = 0.75f;

	public AxesComponent()
	{
            System.out.println("<AxesComponent>");
	}

        // Initialize the system
    public void init()
    {
		setNormalCursor();

             // BUILD SCENE

            scene=new idx3d_Scene(this.size().width,this.size().height);

            scene.setBackgroundColor(0x666666);

            scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(0.2f,0.2f,1f),0xFFFFFF,320,80));
            scene.addLight("Light2",new idx3d_Light(new idx3d_Vector(-1f,-1f,1f),0xFFCC99,100,40));

            // Camera position
            idx3d_Vector cameraPos = new idx3d_Vector(0.0f, 2.0f, 0.5f);
            scene.defaultCamera.setPos(cameraPos);

            float axisDiam = 0.02f;
            float originDiam = 0.05f;

            xyz = new XYZAxes(scene, axisLength, axisDiam, originDiam,
                    "origin0", "xAxis0", "yAxis0", "zAxis0");
            xyz.positionAxes();  // Put the axes into their initial position
            
            XYZAxes xyz2 = new XYZAxes(scene, axisLength / 3.0f,
                    axisDiam / 2.0f, originDiam / 2.0f,
                    "origin1", "xAxis1", "yAxis1", "zAxis1");
            xyz2.positionAxes(0.2f, 0.2f, 0.2f,
                    new idx3d_Vector(0.0f, 0.0f, 1.0f),
                    idx3d_Math.deg2rad(45.0f));  // Put the axes into their initial position

            idx_Thread = new Thread(this);
            idx_Thread.start();

            initialized=true;
    }

	public synchronized void paint(Graphics g)
	{
		repaint();
	}

	public void run()
	{
		while(true)
		{
			repaint();
			try
			{
				idx_Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				System.out.println("idx://interrupted");
			}
		}
	}

	public synchronized void update(Graphics g)
	{
            if (!initialized) return;

            // NOTE: axes cannot be animated because they use "absolute"
            // construction.
            scene.render();
            g.drawImage(scene.getImage(),0,0,this);
	}

	public boolean imageUpdate(Image image, int a, int b, int c, int d, int e)
   	{
   	     return true;
   	}

	public boolean mouseDown(Event evt,int x,int y)
	{
		oldx=x;
		oldy=y;
		setMovingCursor();
		return true;
	}

	public boolean keyDown(Event evt,int key)
	{
        // Keyboard events are not being captured -- needs to be checked ***
        System.out.printf("key %d\n", key);
		if (key==32) { System.out.println(scene.getFPS()+""); return true; }
		if (key==Event.PGUP) {scene.defaultCamera.shift(0f,0f,0.2f); return true; }
		if (key==Event.PGDN) {scene.defaultCamera.shift(0f,0f,-0.2f); return true; }
		if (key==Event.UP) {scene.defaultCamera.shift(0f,0.2f,0f); return true; }
		if (key==Event.DOWN) {scene.defaultCamera.shift(0f,-0.2f,0f); return true; }
		if (key==Event.LEFT) {scene.defaultCamera.shift(-0.2f,0f,0f); return true; }
		if (key==Event.RIGHT) {scene.defaultCamera.shift(0.2f,0f,0f); return true; }
		if ((char)key=='+') {scene.scale(1.2f); return true; }
		if ((char)key=='-') {scene.scale(0.8f); return true; }
		if ((char)key=='.') {scene.defaultCamera.roll(0.2f); return true; }
		if ((char)key==',') {scene.defaultCamera.roll(-0.2f); return true; }
		if ((char)key=='a') {antialias=!antialias; scene.setAntialias(antialias); return true; }

		if ((char)key=='e') {export(); return true; }
		if ((char)key=='i') {idx3d.debug.Inspector.inspect(scene); return true; }

		return true;
	}

	private void export()
	{
		try{
			idx3d_3ds_Exporter.exportToStream(new java.io.FileOutputStream(new java.io.File("export.3ds")),scene);
		}
		catch(Exception ignored){}
	}

	public boolean mouseDrag(Event evt,int x,int y)
	{
		autorotation=false;
		float dx=(float)(y-oldy)/50;
		float dy=(float)(oldx-x)/50;
		scene.rotate(dx,dy,0);
		oldx=x;
		oldy=y;
		return true;
	}

	public boolean mouseUp(Event evt,int x,int y)
	{
		autorotation=true;
		setNormalCursor();
		return true;
	}

	private void setMovingCursor()
	{
		if (getFrame()==null) return;
		getFrame().setCursor(Frame.MOVE_CURSOR);
	}

	private void setNormalCursor()
	{
		if (getFrame()==null) return;
		getFrame().setCursor(Frame.HAND_CURSOR);
	}

	private Frame getFrame()
	{
		Component comp=this;
		while ((comp=comp.getParent())!=null) if(comp instanceof Frame) return (Frame)comp;
		return null;
	}

	public void reshape(int x, int y, int w, int h)
	{
		super.reshape(x,y,w,h);
		if (!initialized) init();
		scene.resize(w,h);
	}


	public synchronized void repaint()
	{
		if (getGraphics() != null) update(getGraphics());
	}

}
