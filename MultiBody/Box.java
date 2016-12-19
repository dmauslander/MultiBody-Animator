/** Multi-Body 3D Animation Based on the idx3d software base
 *  copyright (c) 2016 D. M. Auslander, University of California, Berkeley
 *  All rights reserved
 *  contact dma@me.berkeley.edu if you'd like to use this software.
 */

package MultiBody;

import idx3d.*;

/**
 *
 * @author x
 */

public class Box extends AnimationObject
{
    // 'length' is parallel to z-axis of box, width is paallel to x-axis and height
    //  is parallel to y-axis
    // The 'length' direction is considered the 'axis' of the box
    float length, width, height;
    
    public Box(String name, idx3d_Scene scene, float[] position0, float[] defaultOrientation,
            float[] initOrientation, float defaultRotation, float initRotation, int[] color, 
            float[] dd, int objSpecData)
    {
        super(name, scene, position0, defaultOrientation, defaultRotation, color);
        this.length = dd[objSpecData];
        this.width = dd[objSpecData + 1];
        this.height = dd[objSpecData + 2];
        scene.addObject(name, idx3d_ObjectFactory.BOX(width, height, length));
        scene.object(name).setMaterial(scene.material(name));
        scene.object(name).setPos(position);
        hasOrientation = true;
        hasRotation = true;
        // Set up initial orientation
        UpdateBox(scene, position0, initOrientation, initRotation, color, dd, objSpecData);        
    }
    
    public void UpdateBox(idx3d_Scene scene, float[] newPosition, float[] newOrientation, float newRotation, int[] newColor, 
            float[] dd, int objSpecData)
    {
        UpdateObject(scene, newPosition, newOrientation, newRotation, newColor); // Call parent update function
    }
}
