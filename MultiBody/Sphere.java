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
public class Sphere extends AnimationObject
{
    float diam;
    
    public Sphere(String name, idx3d_Scene scene, float[] position0, float[] orientation, float rotation, int[] color, float[] dd, int objSpecData)
    {
        super(name, scene, position0, orientation, rotation, color);
        this.diam = dd[objSpecData];
        scene.addObject(name, idx3d_ObjectFactory.SPHERE(diam / 2.0f, 16));  // 2nd arg is number of segments
        scene.object(name).setMaterial(scene.material(name));
        //scene.object(name).setPos(position);
        hasOrientation = false;
        hasRotation = false;
        UpdateSphere(scene, position0, orientation, rotation, color, dd, objSpecData);
    }
    
    public void UpdateSphere(idx3d_Scene scene, float[] newPosition, float[] newOrientation, 
            float newRotation, int[] newColor, float[] dd, int objSpecData)
    {
        UpdateObject(scene, newPosition, newOrientation, newRotation, newColor); // Call parent update function
    }
}
