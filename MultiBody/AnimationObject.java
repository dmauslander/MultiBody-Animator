/** Multi-Body 3D Animation Based on the idx3d software base
 *  copyright (c) 2016 D. M. Auslander, University of California, Berkeley
 *  All rights reserved
 *  contact dma@me.berkeley.edu if you'd like to use this software.
 */

package MultiBody;

import idx3d.*;

public class AnimationObject 
{
    String name;
    idx3d_Vector position = new idx3d_Vector();
    idx3d_Vector orientation = new idx3d_Vector();
    idx3d_Vector zero = new idx3d_Vector(0.0f, 0.0f, 0.0f);
    
    float rotation = 0.0f;
    int[] color = new int[3];
    idx3d_Material material = new idx3d_Material();
    boolean hasOrientation = false, hasRotation = false;
    
    public AnimationObject(String name, idx3d_Scene scene, float[] position0, float[] orientation0, 
            float rotation, int[] color)
    {
        this.name = name;
        ArrayToVector(position0, position);
        ArrayToVector(orientation0, orientation);
        this.rotation = rotation;
        for(int i = 0; i < 3; i++)this.color[i] = color[i];
        material.setColor(MakeColorWord(color));
        material.setReflectivity(255);
        scene.addMaterial(name, material);
    }
    
    public void ArrayToVector(float[] array, idx3d_Vector vec)
    {
        vec.x = array[0];
        vec.y = array[1];
        vec.z = array[2];
    }
    
    public int MakeColorWord(int[] color)
    {
        int colorWord = (color[0] << 16) + (color[1] << 8) + color[2];
        return colorWord;
    }
    
    idx3d_Matrix rot2 = null;
    
    public void UpdateObject(idx3d_Scene scene, float[] newPosition, float[] newOrientation, 
            float newRotation, int[] newColor)
    {
        // ***Mystery! Doing position first produces a transient error!!
        
        if(hasRotation)
        {
            // Orient object's axis to the x-axis, then rotate about that axis,
            // then orient it back
            scene.object(name).setPos(zero);  // Translate to origin 
            idx3d_Vector newOrientV  = new idx3d_Vector(0.0f, 0.0f, 1.0f);
            idx3d_Matrix rot = idx3d_Vector.AlignToVector(orientation, newOrientV);
            //System.out.println(rot);
            scene.object(name).transform(rot); // Object is now oriented in base axis direction
            float rotateBy = -(newRotation - rotation);
            if(rot2 == null)
            {
                rot2 = idx3d_Matrix.rotateMatrix(0.0f, 0.0f, rotation);
            }
            else
            {
                TransposeInPlace(rot2);
            }
            // This scheme for rotation seems awkward but is necessary because
            // the repeated incremental rotation using 'float' accumulates too 
            // much error. This method uses the transpose to undo the previous
            // rotation -- the transpose does not introduce any error as it just
            // moves elements around (the inverse of a rotation matrix is equal
            // to its transpose).
            // This can be revisited after idx3d is converted from float to double.
            scene.object(name).transform(rot2);
            rot2 = idx3d_Matrix.rotateMatrix(0.0f, 0.0f, -newRotation);
            scene.object(name).transform(rot2);
            // The previous method -- errors accumulate too quickly because
            // of the 'float' computations
            //scene.object(name).rotate(0.0f, 0.0f, rotateBy);
            rotation = newRotation;
            // Now orient back to original orientation
            TransposeInPlace(rot);  // For inverse transformation
            scene.object(name).transform(rot); // Object is now oriented in its 
                // original orientation 
            scene.object(name).setPos(position); // Translate back to original position
        }
        
        // Orientation
        if(hasOrientation)
        {
            idx3d_Vector newOrientV  = new idx3d_Vector();
            ArrayToVector(newOrientation, newOrientV);
            idx3d_Matrix rot = idx3d_Vector.AlignToVector(orientation, newOrientV);
            scene.object(name).transform(rot);
            //orientation = newOrientV;
            orientation.x = newOrientation[0];
            orientation.y = newOrientation[1];
            orientation.z = newOrientation[2];
            //System.out.println("or " + name + " " + position + orientation);
        }
        
        //System.out.println("po0 " + name + " " + position + orientation);
        position.x = newPosition[0];
        position.y = newPosition[1];
        position.z = newPosition[2];
        scene.object(name).setPos(position);  // Set new position
        //System.out.println("po1 " + name + " " + position + orientation);
    }
    
    public void TransposeInPlace(idx3d_Matrix m)
    {
        float b;
        b = m.m10;
        m.m10 = m.m01;
        m.m01 = b;
        
        b = m.m20;
        m.m20 = m.m02;
        m.m02 = b;
        
        b = m.m30;
        m.m30 = m.m03;
        m.m03 = b;
        
        b = m.m21;
        m.m21 = m.m12;
        m.m12 = b;
        
        b = m.m31;
        m.m31 = m.m13;
        m.m13 = b;
        
        b = m.m32;
        m.m32 = m.m23;
        m.m23 = b;
        
    }
}
