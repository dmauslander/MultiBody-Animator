package idx3d_axes;

import idx3d.*;
import java.awt.*;

/** Create an XYZ axis set using RGB --> XYZ
 *
 * @author DMAuslander, July 19, 2009
 */
public class XYZAxes
{
    idx3d_Scene scene;
    float axisLength;
    idx3d_Matrix rotX = null;
    idx3d_Matrix rotY = null;
    idx3d_Matrix rotZ = null;
    idx3d_Vector positionX =  new idx3d_Vector(axisLength / 2.0f, 0.0f, 0.0f);
    idx3d_Vector positionY =  new idx3d_Vector(0.0f, axisLength / 2.0f, 0.0f);
    idx3d_Vector positionZ =  new idx3d_Vector(0.0f, 0.0f, axisLength / 2.0f );
    idx3d_Object xaxis = null, yaxis = null,  zaxis = null;
    String oName, xName, yName, zName;
    
    public XYZAxes(idx3d_Scene scene, float axisLength,
            float axisDiam, float originDiam,
            String oName, String xName, String yName, String zName)
    {
        this.scene = scene;
        this.axisLength = axisLength;
        System.out.println(oName + "oName\n");
        this.oName = oName; //new String(oName);
        this.xName = xName;
        this.yName = yName;
        this.zName = zName;
        idx3d_Material redStuff=new idx3d_Material();
        redStuff.setColor(0x00ff0000);
        scene.addMaterial("RedStuff",redStuff);

        idx3d_Material greenStuff=new idx3d_Material();
        greenStuff.setColor(0x0000ff00);
        scene.addMaterial("GreenStuff",greenStuff);

        idx3d_Material blueStuff=new idx3d_Material();
        blueStuff.setColor(0x00ff);
        scene.addMaterial("BlueStuff",blueStuff);

        idx3d_Material whiteStuff=new idx3d_Material();
        whiteStuff.setColor(0x00ffffff);
        scene.addMaterial("WhiteStuff",whiteStuff);
        // Create objects for the x, y, and z axes and the origin
        scene.addObject(oName, idx3d_ObjectFactory.SPHERE(originDiam, 16));
        scene.object(oName).setMaterial(scene.material("WhiteStuff"));

        // Axes RGB (red-green-blue) ==> XYZ
        xaxis = idx3d_ObjectFactory.CYLINDER( axisLength, axisDiam, 20);
        xaxis.setMaterial(scene.material("RedStuff"));
        scene.addObject(xName, xaxis);

        yaxis = idx3d_ObjectFactory.CYLINDER( axisLength, axisDiam, 20);
        yaxis.setMaterial(scene.material("GreenStuff"));
        scene.addObject(yName, yaxis);

        zaxis = idx3d_ObjectFactory.CYLINDER( axisLength, axisDiam, 20);
        zaxis.setMaterial(scene.material("BlueStuff"));
        scene.addObject(zName, zaxis);
    }

    idx3d_Vector vv = new idx3d_Vector(0.0f, 0.0f, 1.0f);

    public void positionAxes()
    {
        positionAxes(0.0f, 0.0f, 0.0f, vv, idx3d_Math.deg2rad(0.0f));
    }

    idx3d_Matrix rotV = new idx3d_Matrix(); // Working matrix

    public void positionAxes(float xOrigin, float yOrigin, float zOrigin,
            idx3d_Vector vr, float angleAboutVR)
    {
        idx3d_Vector pos = null;  // for rotated positions
        scene.object(oName).setPos(xOrigin, yOrigin, zOrigin);
           // Position of origin

        // X- axis
        // Step 1: put the cylinder for the axis in the correct default
        // orientation and position.
        // Rotation matrix to orient the X axis , etc.
        rotX = idx3d_Matrix.rotateMatrix(0.0f, 0.0f,
                idx3d_Math.deg2rad(90.0f));
        scene.object(xName).transform(rotX);

        // Step 2: Rotate about the specified vector
        idx3d_Vector.rotationAboutVector(vr, angleAboutVR, rotV);
        scene.object(xName).transform(rotV);
        positionX.x = axisLength / 2.0f;
        positionX.y = 0.0f;
        positionX.z = 0.0f;
        pos = positionX.transform(rotV);  // Rotate positioning vector
            // This thrashes some memory but we'll live with it for the moment

        // Step 3: Move to position corresponding to location of origin
        // The positionX (Y,Z) vector is to locate the center of the
        // cylinder representing the axis so it can be moved properly
        pos.x += xOrigin;
        pos.y += yOrigin;
        pos.z += zOrigin;
        scene.object(xName).setPos(pos);

        // Y-axis
        // Step 1: put the cylinder for the axis in the correct default
        // orientation and position.
        // Rotation matrix to orient the X axis , etc.
        // Not needed for Y-axis
        //rotY = idx3d_Matrix.rotateMatrix(0.0f, 0.0f, idx3d_Math.deg2rad(0.0f));
        //scene.object("yAxis").transform(rotY);

        // Step 2: Rotate about the specified vector
        //rotationAboutVector(vr, angleAboutVR, rotV);
        scene.object(yName).transform(rotV);  // rotV doesn't have
                // to be recomputed
        positionY.x = 0.0f;
        positionY.y = axisLength / 2.0f;
        positionY.z = 0.0f;
        pos = positionY.transform(rotV);  // Rotate positioning vector
            // This thrashes some memory but we'll live with it for the moment

        // Step 3: Move to position corresponding to location of origin
        // The positionX (Y,Z) vector is to locate the center of the
        // cylinder representing the axis so it can be moved properly
        pos.x += xOrigin;
        pos.y += yOrigin;
        pos.z += zOrigin;
        scene.object(yName).setPos(pos);

        // Z-axis
        // Step 1: put the cylinder for the axis in the correct default
        // orientation and position.
        // Rotation matrix to orient the X axis , etc.
        rotZ = idx3d_Matrix.rotateMatrix(idx3d_Math.deg2rad(90.0f), 0.0f, 0.0f);
        scene.object(zName).transform(rotZ);

        // Step 2: Rotate about the specified vector
        //rotationAboutVector(vr, angleAboutVR, rotV);
        scene.object(zName).transform(rotV);  // rotV doesn't have
                // to be recomputed
        positionZ.x = 0.0f;
        positionZ.y = 0.0f;
        positionZ.z = axisLength / 2.0f;
        pos = positionZ.transform(rotV);  // Rotate positioning vector
            // This thrashes some memory but we'll live with it for the moment

        // Step 3: Move to position corresponding to location of origin
        // The positionX (Y,Z) vector is to locate the center of the
        // cylinder representing the axis so it can be moved properly
        pos.x += xOrigin;
        pos.y += yOrigin;
        pos.z += zOrigin;
        scene.object(zName).setPos(pos);
    }
}
