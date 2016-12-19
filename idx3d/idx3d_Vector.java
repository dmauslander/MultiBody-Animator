// | -----------------------------------------------------------------
// | idx3d III is (c)1999/2000 by Peter Walser
// | -----------------------------------------------------------------
// | idx3d is a 3d engine written in 100% pure Java (1.1 compatible)
// | and provides a fast and flexible API for software 3d rendering
// | on the Java platform.
// |
// | Feel free to use the idx3d API / classes / source code for
// | non-commercial purposes (of course on your own risk).
// | If you intend to use idx3d for commercial purposes, please
// | contact me with an e-mail [proxima@active.ch].
// |
// | Thanx & greetinx go to:
// | * Wilfred L. Guerin, 	for testing, bug report, and tons 
// |			of brilliant suggestions
// | * Sandy McArthur,	for reverse loops
// | * Dr. Douglas Lyons,	for mentioning idx3d1 in his book
// | * Hugo Elias,		for maintaining his great page
// | * the comp.graphics.algorithms people, 
// | 			for scientific concerns
// | * Tobias Hill,		for inspiration and awakening my
// |			interest in java gfx coding
// | * Kai Krause,		for inspiration and hope
// | * Incarom & Parisienne,	for keeping me awake during the 
// |			long coding nights
// | * Doris Langhard,	for being the sweetest girl on earth
// | * Etnica, Infinity Project, X-Dream and "Space Night"@BR3
// | 			for great sound while coding
// | and all coderz & scenerz out there (keep up the good work, ppl :)
// |
// | Peter Walser
// | proxima@active.ch
// | http://www2.active.ch/~proxima
// | "On the eigth day, God started debugging"
// | -----------------------------------------------------------------

package idx3d;

public class idx3d_Vector
// defines a 3d vector
{
	// F I E L D S
	
		public float x=0;      //Cartesian (default)
		public float y=0;      //Cartesian (default)
		public float z=0;      //Cartesian (default),Cylindric
		public float r=0;      //Cylindric
		public float theta=0;  //Cylindric


	// C O N S T R U C T O R S

		public idx3d_Vector ()
		{
		}

		public idx3d_Vector (float xpos, float ypos, float zpos)
		{
			x=xpos;
			y=ypos;
			z=zpos;
		}

	// P U B L I C   M E T H O D S

		public idx3d_Vector normalize()
		// Normalizes the vector
		{
			float dist=length();
			if (dist==0) return this;
			float invdist=1/dist;
			x*=invdist;
			y*=invdist;
			z*=invdist;
			return this;
		}
		
		public idx3d_Vector reverse()
		// Reverses the vector
		{	
			x=-x;
			y=-y;
			z=-z;
			return this;
		}
		
		public float length()
		// Lenght of this vector
		{	
			return (float)Math.sqrt(x*x+y*y+z*z);
		}

		public idx3d_Vector transform(idx3d_Matrix m)
		// Modifies the vector by matrix m 
		{
			float newx = x*m.m00 + y*m.m01 + z*m.m02+ m.m03;
			float newy = x*m.m10 + y*m.m11 + z*m.m12+ m.m13;
			float newz = x*m.m20 + y*m.m21 + z*m.m22+ m.m23;
			return new idx3d_Vector(newx,newy,newz);
		}

		public void buildCylindric()
		// Builds the cylindric coordinates out of the given cartesian coordinates
		{
			r=(float)Math.sqrt(x*x+y*y);
			theta=(float)Math.atan2(x,y);
		}

		public void buildCartesian()
		// Builds the cartesian coordinates out of the given cylindric coordinates
		{
			x=r*idx3d_Math.cos(theta);
			y=r*idx3d_Math.sin(theta);
		}

		public static idx3d_Vector getNormal(idx3d_Vector a, idx3d_Vector b)
		// returns the normal vector of the plane defined by the two vectors
		{
			return vectorProduct(a,b).normalize();
		}
		
		public static idx3d_Vector getNormal(idx3d_Vector a, idx3d_Vector b, idx3d_Vector c)
		// returns the normal vector of the plane defined by the two vectors
		{
			return vectorProduct(a,b,c).normalize();
		}
		
		public static idx3d_Vector vectorProduct(idx3d_Vector a, idx3d_Vector b)
		// returns a x b
		{
			return new idx3d_Vector(a.y*b.z-b.y*a.z,a.z*b.x-b.z*a.x,a.x*b.y-b.x*a.y);
		}
		
		public static idx3d_Vector vectorProduct(idx3d_Vector a, idx3d_Vector b, idx3d_Vector c)
		// returns (b-a) x (c-a)
		{
			return vectorProduct(sub(b,a),sub(c,a));
		}

		public static float angle(idx3d_Vector a, idx3d_Vector b)
		// returns the angle between 2 vectors
		{
			a.normalize();
			b.normalize();
                        float result = a.x*b.x+a.y*b.y+a.z*b.z;
                        // Protect against round-off producing results with mag > 1
                        if(result > 1.0f)result = 1.0f;
                        if(result < -1.0f)result = -1.0f;
			return (result);
		}
		
		public static idx3d_Vector add(idx3d_Vector a, idx3d_Vector b)
		// adds 2 vectors
		{
			return new idx3d_Vector(a.x+b.x,a.y+b.y,a.z+b.z);
		}
		
		public static idx3d_Vector sub(idx3d_Vector a, idx3d_Vector b)
		// substracts 2 vectors
		{
			return new idx3d_Vector(a.x-b.x,a.y-b.y,a.z-b.z);
		}
		
		public static idx3d_Vector scale(float f, idx3d_Vector a)
		// substracts 2 vectors
		{
			return new idx3d_Vector(f*a.x,f*a.y,f*a.z);
		}
		
		public static float len(idx3d_Vector a)
		// length of vector
		{
			return (float)Math.sqrt(a.x*a.x+a.y*a.y+a.z*a.z);
		}
		
		public static idx3d_Vector random(float fact)
		// returns a random vector
		{
			return new idx3d_Vector(fact*idx3d_Math.random(),fact*idx3d_Math.random(),fact*idx3d_Math.random());
		}

		public String toString()
		{
			return new String ("<vector x="+x+" y="+y+" z="+z+">\r\n");
		}

		public idx3d_Vector getClone()
		{
			return new idx3d_Vector(x,y,z);
		}

        // Methods added by DM Auslander

        // Compute a rotation matrix that when applied to u will align it with v
        // Use method from:
        // http://sci.tech-archive.net/Archive/sci.math/2007-10/msg03644.html
        public static idx3d_Matrix AlignToVector(idx3d_Vector u, idx3d_Vector v)
        {
                idx3d_Vector n = new idx3d_Vector();
                n  = idx3d_Vector.vectorProduct(u, v);
                n.normalize();
                double phi = Math.acos(idx3d_Vector.angle(u, v));
                float s = (float)Math.sin(phi);
                float d = (float)Math.cos(phi);
                float t = 1.0f - d;
                float a = n.x, b = n.y, c = n.z;
                // The rotation matrix, term by term
                idx3d_Matrix rot = new idx3d_Matrix();
                /*
                [ t*a^2+d, t*a*b-s*c, t*a*c + s*b ]
                [ t*a*b + s*c, t*b^2+d, t*b*c - a*s ]
                [ t*a*c - s*b, t*b*c+s*a, t*c^2 + d ]
                 */
                rot.m00 = t * a * a + d;
                rot.m01 = t * a * b - s * c;
                rot.m02 = t * a * c + s * b;
                rot.m10 = t * a * b + s * c;
                rot.m11 = t * b * b + d;
                rot.m12 = t * b * c - a * s;
                rot.m20 = t * a * c - s * b;
                rot.m21 = t * b * c + s * a;
                rot.m22 = t * c * c + d;
                return rot;
        }

        /** Compute the rotation matrix for rotation about the vector
         * vv by an angle 'angle'. The result is returned in matrix 'r'
         * which must be predefined.
         * @param vv Rotate about this vector
         * @param angle Rotate by this angle
         * @param r Result matrix (must be predefined)
         */
        public static void rotationAboutVector(idx3d_Vector vv, float angle,
                idx3d_Matrix r)
        {
            //idx3d_Vector v = new idx3d_Vector();  // Working vector
            //v.x = vv.x; v.y = vv.y; v.z = vv.z;  // Copy vector
            idx3d_Vector v = vv.getClone();
            v.normalize();  // Normalize v in place
            double c = Math.cos(angle);
            double s = Math.sin(angle);
            double t = 1.0 - c;

            /* Rotation matrix for this
             * from: http://www.cprogramming.com/tutorial/3d/rotation.html
             * (right-handed)
             * tX^2 + c   tXY + sZ   tXZ - sY   0
             * tXY-sZ   tY^2 + c   tYZ + sX   0
             * tXY + sY   tYZ - sX   tZ^2 + c   0
             *  0           0        0       1
             */
            r.m00 = (float)(t * v.x * v.x + c);
            r.m01 = (float)(t * v.x * v.y + s * v.z);
            r.m02 = (float)(t * v.x * v.z - s * v.y);
            r.m03 = 0.0f;
            r.m10 = (float)(t * v.x * v.y - s * v.z);
            r.m11 = (float)(t * v.y * v.y + c);
            r.m12 = (float)(t * v.y * v.z + s * v.x);
            r.m13 = 0.0f;
            r.m20 = (float)(t * v.x * v.z + s * v.y);
            r.m21 = (float)(t * v.y * v.z - s * v.x);
            r.m22 = (float)(t * v.z * v.z + c);
            r.m23 = 0.0f;
            r.m30 = 0.0f;
            r.m31 = 0.0f;
            r.m32 = 0.0f;
            r.m33 = 1.0f;
        }
    
}