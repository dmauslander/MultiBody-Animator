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

public class Cylinder extends AnimationObject
{
    float length, diam;
    
    public Cylinder(String name, idx3d_Scene scene, float[] position0, float[] defaultOrientation,
            float[] initOrientation, float rotation, int[] color, float[] dd, int objSpecData)
    {
        super(name, scene, position0, defaultOrientation, rotation, color);
        this.length = dd[objSpecData];
        this.diam = dd[objSpecData + 1];
        scene.addObject(name, idx3d_ObjectFactory.CYLINDER(length, diam / 2.0f, 16));  // last arg is number of segments
        scene.object(name).setMaterial(scene.material(name));
        scene.object(name).setPos(position);
        hasOrientation = true;
        hasRotation = false;
        // Set up initial orientation
        UpdateCylinder(scene, position0, initOrientation, rotation, color, dd, objSpecData);        
    }
    
    public void UpdateCylinder(idx3d_Scene scene, float[] newPosition, float[] newOrientation, float newRotation, int[] newColor, 
            float[] dd, int objSpecData)
    {
        UpdateObject(scene, newPosition, newOrientation, newRotation, newColor); // Call parent update function
    }
}
