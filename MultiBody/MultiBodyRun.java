/** Multi-Body 3D Animation Based on the idx3d software base
 *  copyright (c) 2016 D. M. Auslander, University of California, Berkeley
 *  All rights reserved
 *  contact dma@me.berkeley.edu if you'd like to use this software.
 */

package MultiBody;

import idx3d.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import idx3d_axes.*;

// Data file format:
// time link0X link0Y link0Z link1X link1Y link1Z ...

public class MultiBodyRun extends Panel implements Runnable
{
    private Thread idx_Thread;
    idx3d_Scene scene;
    boolean initialized=false;
    boolean antialias=false;

    int oldx=0;
    int oldy=0;
    boolean autorotation= false;  // Value set below -- this is initial value
        //normal- true; display initial only - false
    boolean firstPass = true;

    // Pendulum geometry
    //idx3d_Vector alignLink = new idx3d_Vector(); // Vector to align to
    //idx3d_Vector dirLink = new idx3d_Vector(); // Current direction of link
    
    // File information
    FileReader inputRdr = null;
    BufferedReader bufRdr;  //For input file
    //int nItems = 3;  // Maximum number of items per line in data file
    int nObjs = 0;  // Number of objects
    int nLinks = 1;  // Only for debugging -- remove later
    int nRows = 0; // Number of rows of data in the data base
    float [] data;
    ArrayList<float []> allData = new ArrayList<>();
    // Data parsed from input lines
    float[] position = new float[3]; 
    float[] orientation = new float[3];
    int[] color = new int[3]; 
    float rotation = 0.0f;
    int objType = 0;
    float tt = 0.0f;  // Time from data file line
    int objSpecData = 0;  // Beginning of object specific data
    
    ArrayList<AnimationObject> animObjs = new ArrayList<>();
    
    public MultiBodyRun()
    {
        System.out.println("<MultiBodyRun>");
    }

    public float AngleR(float angleDeg)
    {
        return angleDeg * idx3d_Math.pi / 180.0f;
    }

    // Returns the number of items on the line (0 ==> end-of-file)
    public static int ReadFloatData(BufferedReader myBufRdr, float [] values)
    {
        int n = 0; // Number of items found
        String thisLine = null; 
        try
        {
            boolean isComment = true;
            while(isComment)
            {
                thisLine = myBufRdr.readLine();                
                if(thisLine == null)return -1;  // end of file
                if(thisLine.startsWith("#"))isComment = true;  // Comment line
                else isComment = false;
            }
            String s = thisLine.substring(0, Math.min(thisLine.length(), 3));
            if(s.equalsIgnoreCase("end"))return 0;  // End of this time frame
            StringTokenizer st = new StringTokenizer(thisLine); 
            n = 0;
            while(st.hasMoreTokens())
                {
                String str = st.nextToken();
                //values[n] = Float.valueOf(str).floatValue();
                values[n] = Float.parseFloat(str);
                n++;
                }         
        }
        catch(IOException e)
            {
            System.out.println("<ReadFloatData> IO Error " + e);
            System.exit(1);
            } 
        return n;
    }
    
    // Initialize the system
    float time = 0.0f;
    
    public void init() 
    {            
        int idl = 0;  // index for data in line array
        setNormalCursor();
        // Open the data file
        try
        {
                            // Open the file that is the first
                            // command line parameter
            inputRdr = new FileReader("data.txt");
                            // Convert our input stream to a
                            // DataInputStream
            bufRdr = new BufferedReader(inputRdr); 
            
            // Read all of the data into an array list
            float [] dd = null;
            float [] dataLine = null;
            float [] firstDataLine = null;
            //boolean InitialConfigOnly = false;
            int maxItems = 100;  // Maximum number of items on a line
            dd = new float[maxItems];
            int nFloats = ReadFloatData(bufRdr, dd);
            if(nFloats == 1)
            {
                nObjs = (int)dd[0];
            }
            else
            {
                System.out.printf("Expected just nObjs on first line\n");
                System.exit(1);
            }
            // Now cycle through the data sets -- 
            // each set starts with a line containg the time followed
            // by nLinks+1 lines, each containing an <x.y.z> data set 
            boolean newTime = true;
            
            while(true)
            {
                idl = 0;  // counter for data
                nFloats = ReadFloatData(bufRdr, dd);
                if(nFloats == -1)break;  // end-of-file
                if(nFloats == 0)
                {
                    newTime = true;
                    continue;
                }  // End of this time
                
                if(newTime)
                {
                    newTime = false;
                    if(nFloats == 1)
                    {
                        time = dd[0]; // time for this sample 
                    }
                    else
                    {
                        System.out.printf("Expected just 'time' on this line\n");
                        System.exit(1);
                    }
                }
                for(int i = 0; i < nObjs; i++)
                {
                    // read all of the positions
                    nFloats = ReadFloatData(bufRdr, dd);
                    dataLine = new float[nFloats + 1];
                    dataLine[0] = time;
                    for(int j = 0; j < nFloats; j++)dataLine[j + 1] = dd[j];
                    allData.add(dataLine);  // Add a line of data to the data base
                }
            }
            bufRdr.close();
            inputRdr.close();
            nRows = allData.size();
            System.out.printf("nRows = %d, nObjs = %d\n", nRows, nObjs);
            
            Boolean printData = false;
            if(printData)
            {
                for(int i = 0; i < nRows; i++)
                {
                    data = allData.get(i);
                    for(int j = 0; j < data.length; j++)
                        System.out.printf("%g ", data[j]);
                    System.out.printf("\n");
                }
                System.out.printf("\n ");
            }
        }
        catch(FileNotFoundException fe)
        {
            System.out.println("FileNotFoundException : " + fe);
            System.exit(1);
        }
        catch (IOException e)
        {
            System.err.println("File input error" + e);
            System.exit(1);
        }

        //System.exit(1);
        // BUILD SCENE

        //scene=new idx3d_Scene(this.size().width,this.size().height);
        scene=new idx3d_Scene(this.getWidth(),this.getHeight());
        //scene.rotate(0.0f, idx3d_Math.deg2rad(180.0f), 0.0f);

        // Color code is 0xRRGGBB
        scene.setBackgroundColor(0xeeeeee);

        scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(0.2f,0.2f,1f),0xFFFFFF,320,80));			
        scene.addLight("Light2",new idx3d_Light(new idx3d_Vector(-1f,-1f,1f),0xFFCC99,100,40));


        // Create objects
        for(int i = 0; i < nObjs; i++)
        {
            float [] dd = allData.get(i);  // Get initial data
            ParseData(dd);
            String name;
            switch(objType)
            {
                case 1: // Sphere
                    name = "Sphere" + Integer.toString(i);
                    Sphere sph = new Sphere(name, scene, position, orientation, 
                            rotation, color, dd, objSpecData);
                    animObjs.add(sph);  // Add to object list;
                    break;
                    
                case 2: // Cylinder
                    name = "Cylinder" + Integer.toString(i);
                    float[] defaultCylOrient = {0.0f, 1.0f, 0.0f}; // Orientation
                        // as originally created
                    Cylinder cyl = new Cylinder(name, scene, position, defaultCylOrient,orientation, 
                            rotation, color, dd, objSpecData);
                    animObjs.add(cyl);  // Add to object list;
                    break;
                    
                case 3: // Box
                    name = "Box" + Integer.toString(i);
                    float[] defaultBoxOrient = {0.0f, 0.0f, 1.0f}; // Orientation
                        // as originally created byy idx3d
                    float defaultRotation = 0.0f;  // Rotation as created by idx3d
                    Box box = new Box(name, scene, position, defaultBoxOrient,orientation, 
                            defaultRotation, rotation, color, dd, objSpecData);
                    animObjs.add(box);  // Add to object list;
                    break;
                    
                default:
                    System.out.printf("<init>Unknown object type\n");
                    System.exit(1);
            }
        }
        float scale = 1.5f;
        // Variables for axes, scene, camera
        float axisLength = 0.0f, axisDiam = 0.0f, originDiam = 0.0f;
        float xOrigin = 0.0f, yOrigin = 0.0f, zOrigin = 0.0f;
        float axisRotX = 0.0f, axisRotY = 0.0f, axisRotZ = 0.0f, axisRotAngle = 0.0f;
        float sceneRotX = 0.0f, sceneRotY = 0.0f, sceneRotZ = 0.0f;
        float scenePosX = 0.0f, scenePosY = 0.0f, scenePosZ = 0.0f;
        float cameraPosX = 0.0f, cameraPosY = 0.0f, cameraPosZ = 0.0f;
        float cameraLookAtX = 0.0f, cameraLookAtY = 0.0f, cameraLookAtZ = 0.0f;
        
        // Read scene and camera data from a file
        try
        {
                            // Open the file that is the first
                            // command line parameter
            inputRdr = new FileReader("SceneCamera.txt");
                            // Convert our input stream to a
                            // DataInputStream
            bufRdr = new BufferedReader(inputRdr); 
            
            // Read all of the data into an array list
            float [] dd = null;
            float [] dataLine = null;
            int maxItems = 10;  // Maximum number of items on a line
            dd = new float[maxItems];
            int nFloats = ReadFloatData(bufRdr, dd);
            axisLength = dd[0]; axisDiam = dd[1]; originDiam = dd[2];
            nFloats = ReadFloatData(bufRdr, dd);
            xOrigin = dd[0]; yOrigin = dd[1]; zOrigin = dd[2];
            axisRotX = dd[3]; axisRotY = dd[4]; axisRotZ = dd[5]; axisRotAngle = dd[6]; 
            nFloats = ReadFloatData(bufRdr, dd);
            sceneRotX = dd[0]; sceneRotY = dd[1]; sceneRotZ = dd[2]; 
            nFloats = ReadFloatData(bufRdr, dd);
            scenePosX = dd[0]; scenePosY = dd[1]; scenePosZ = dd[2]; 
            nFloats = ReadFloatData(bufRdr, dd);
            cameraPosX = dd[0]; cameraPosY = dd[1]; cameraPosZ = dd[2];
            nFloats = ReadFloatData(bufRdr, dd);
            cameraLookAtX = dd[0]; cameraLookAtY = dd[1]; cameraLookAtZ = dd[2];
            
            bufRdr.close();
            inputRdr.close();
        }
        catch(FileNotFoundException fe)
        {
            System.out.println("FileNotFoundException : " + fe);
            System.exit(1);
        }
        catch (IOException e)
        {
            System.err.println("File input error" + e);
            System.exit(1);
        }
        
        // Axes
        XYZAxes xyz = new XYZAxes(scene, axisLength, axisDiam, originDiam, "origin",
           "xAxis", "yAxis", "zAxis");
        xyz.positionAxes(xOrigin, yOrigin, zOrigin, new idx3d_Vector(axisRotX, axisRotY, axisRotZ), axisRotAngle * 3.14f / 180.0f);
        // Camera position
        scene.rotate(sceneRotX * 3.14f / 180f, sceneRotY * 3.14f / 180f, sceneRotZ * 3.14f / 180f);
        scene.setPos(scenePosX, scenePosY, scenePosZ);
        scene.defaultCamera.setPos(cameraPosX, cameraPosY, cameraPosZ); 
        scene.defaultCamera.lookAt(cameraLookAtX, cameraLookAtY, cameraLookAtZ); 

        idx_Thread = new Thread(this);
        idx_Thread.start();

        initialized=true;
        autorotation = true;  // normal - true; initial position only - false
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
                        idx3d_Sleep(50);                        
		}
	}

        int iPrev = 0, iCur = 0;  // Data rows to operate on
        
	public synchronized void update(Graphics g)
	{
            float [] dPrev, dCur;
            
            if (!initialized) return;
            if (autorotation)
            {             
                if(iCur == nObjs)
                {
                    //System.out.printf("%d %g %g %g\n", iCur, position[0], position[1], position[2]);
                    idx3d_Sleep(2000); // Hold initial position
                }
                 
                if(iCur >= nRows)
                {
                    // Start again
                    idx3d_Sleep(2000); // Hold final position
	            iPrev = nRows - 1;  // The first time is a special case
                    iCur = 0;
                }
                
                // Update objects
                for(int i = 0; i < nObjs; i++)
                {
                    dPrev = allData.get(iPrev);
                    dCur = allData.get(iCur);
                    ParseData(dCur);
                    //if(iCur == (nObjs - 1))
                    //   System.out.printf("%d %d %g %g %g\n", iCur, objType, position[0], position[1], position[2]);
                    switch(objType)
                    {
                        case 1: // Sphere
                            ((Sphere)animObjs.get(i)).UpdateSphere(scene, position, orientation, rotation, color, 
                                dCur, objSpecData);
                            break;

                        case 2: // Cylinder
                            ((Cylinder)animObjs.get(i)).UpdateCylinder(scene, position, 
                                orientation, rotation, color, 
                                dCur, objSpecData);
                            break;

                        case 3: // Cylinder
                            ((Box)animObjs.get(i)).UpdateBox(scene, position, 
                                orientation, rotation, color, 
                                dCur, objSpecData);
                            break;

                        default:
                            System.out.printf("<update>Unknown object type\n");
                            System.exit(1);
                    }
                    iCur++;
                    iPrev = iCur - 1;
                }
            }
            if (g != null)
            {
                scene.render();
                g.drawImage(scene.getImage(),0,0,null); //this);
            }
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

        public void ParseData(float[] dd)
        {
            // Parse a data line
            tt = dd[0];
            objType = (int)dd[1];
            rotation = dd[8];
            objSpecData = 12;  // Where the object specific data begins
            for(int i = 0; i < 3; i++)
            {
                position[i] = dd[i + 2];
                orientation[i] = dd[i + 5];
                color[i] = (int)dd[i + 9];
            }
        }
        
	public boolean keyDown(Event evt,int key)
	{
		if (key==32) { System.out.println(scene.getFPS()+""); return true; }
		if (key==Event.PGUP) {scene.defaultCamera.shift(0f,0f,0.02f); return true; }
		if (key==Event.PGDN) {scene.defaultCamera.shift(0f,0f,-0.02f); return true; }
		if (key==Event.UP) {scene.defaultCamera.shift(0f,0.02f,0f); return true; }
		if (key==Event.DOWN) {scene.defaultCamera.shift(0f,-0.02f,0f); return true; }
		if (key==Event.LEFT) {scene.defaultCamera.shift(-0.02f,0f,0f); return true; }
		if (key==Event.RIGHT) {scene.defaultCamera.shift(0.02f,0f,0f); return true; }
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
        
        public void idx3d_Sleep(int sleepTime)
        {
                    try
			{
				idx_Thread.sleep(sleepTime);
			}
		    catch (InterruptedException e)
			{
				System.out.println("idx://interrupted");
			}            
        }
}
