package elements;

import primitives.Point3D;
import primitives.Ray;
import primitives.Vector;

import java.util.ArrayList;
import java.util.List;

import static primitives.Util.isZero;

/**
 * class camera
 *  _p0,_vTo, _vUp ,_vRight
 *  @author haleli&marom
 */
public class Camera {
    Point3D _p0;
    Vector _vTo;
    Vector _vUp;
    Vector _vRight;
   double _apertureSize;
   double _focalLength;
    /**
     *render with depth of filed when DepthOfFiled=true;
     */
    private boolean DepthOfFiled;
    /**
     * constructor
     * @param _p0
     * @param _vTo
     * @param _vUp
     */
    public Camera(Point3D _p0, Vector _vTo, Vector _vUp) {

        this(_p0,_vTo,_vUp,1,1,false);

    }

    public Camera(Point3D _p0, Vector _vTo, Vector _vUp,double _apertureSize,double _focalLength,boolean DepthOfFiled ) {
        //if the the vectors are not orthogonal, throw exception.
        if (_vUp.dotProduct(_vTo) != 0)
            throw new IllegalArgumentException("the vectors must be orthogonal");

        this._p0 = new Point3D(_p0);
        this._vTo = _vTo.normalized();
        this._vUp = _vUp.normalized();

        _vRight = this._vTo.crossProduct(this._vUp).normalize();
        this._apertureSize=_apertureSize;
        this._focalLength=_focalLength;
        this.DepthOfFiled=DepthOfFiled;
    }


//*********************get***********************
    public Point3D getP0() {
        return new Point3D(_p0);
    }

    public Vector getVTo() {
        return new Vector(_vTo);
    }

    public Vector getVUp() {
        return new Vector(_vUp);
    }

    public Vector getVRight() {
        return new Vector(_vRight);
    }

    public double getApertureSize() { return _apertureSize; }

    public double getFocalLength() {
        return _focalLength;
    }



    /**
     * Constructing a ray through a pixel
     * @param nX
     * @param nY
     * @param j
     * @param i
     * @param screenDistance
     * @param screenWidth
     * @param screenHeight
     * @return ray Through Pixel
     */

    public Ray constructRayThroughPixel(int nX, int nY,
                                        int j, int i, double screenDistance,
                                        double screenWidth, double screenHeight) {
        if (isZero(screenDistance)) {
            throw new IllegalArgumentException("distance cannot be 0");
        }

        Point3D Pc = _p0.add(_vTo.scale(screenDistance));

        double Ry = screenHeight / nY;
        double Rx = screenWidth / nX;

        double yi = ((i - nY / 2d) * Ry + Ry / 2d);
        double xj = ((j - nX / 2d) * Rx + Rx / 2d);

        Point3D Pij = Pc;

        if (!isZero(xj)) {
            Pij = Pij.add(_vRight.scale(xj));
        }
        if (!isZero(yi)) {
            Pij = Pij.subtract(_vUp.scale(yi)); // Pij.add(_vUp.scale(-yi))
        }

        Vector Vij = Pij.subtract(_p0);

        return new Ray(_p0, Vij);

    }

    /**
     * construct Rays Through Pixel
     * call constructRaysForDepthOfField
     * @param nX
     * @param nY
     * @param j
     * @param i
     * @param screenDistance
     * @param screenWidth
     * @param screenHeight
     * @return list of rays throw the focal point
     */
    public List<Ray> constructRaysThroughPixel(int nX, int nY,
                                              int j, int i, double screenDistance,
                                              double screenWidth, double screenHeight)
    {
        List<Ray> rays=new ArrayList<>();
        Ray ray=constructRayThroughPixel( nX,  nY, j,  i,  screenDistance,
                screenWidth,  screenHeight);

        if(this.DepthOfFiled==false)
            return List.of( ray);

        return constructRaysForDepthOfField(ray,screenDistance);

    }

    /**
     * find Focal Point by ray
     * @param ray
     * @return focal point
     */
    public Point3D findFocalPoint( Ray ray)
    {

        double cosine=_vTo.dotProduct(ray.getDirection());
        double distance=this.getFocalLength()/cosine;
        Point3D focalPoint=ray.getTargetPoint(distance);
        return focalPoint;
    }

    /**
     * construct Rays For Depth Of Field
     * @param ray
     * @param screenDistance
     * @return list of ray
     */
    public List<Ray> constructRaysForDepthOfField(Ray ray,double screenDistance)
    {
        //Ray ray=constructRayThroughPixel( nX, nY, j, i, screenDistance, screenWidth, screenHeight);
        Point3D focalPoint=this.findFocalPoint(ray);
        //width and height are the number of pixels in the rows
        //and columns of the view plane
        double width = this._apertureSize;
        double height = this._apertureSize;

        //Nx and Ny are the width and height of the image.
        int Nx = 9; //columns
        int Ny = 9; //rows
        double cosine=_vTo.dotProduct(ray.getDirection());
        double distance=screenDistance/cosine;
        Point3D p=ray.getTargetPoint(distance);
        Point3D p1=p.subtract(_vRight.scale((double)Nx/2d)).subtract(_vUp.scale((double)Ny/2d));

        //pixels grid
        List<Ray> rays=new ArrayList<>();
        for (int row = 0; row < Ny; ++row) {
            for (int column = 0; column < Nx; ++column) {
                Point3D point= _p0.add(_vRight.scale((0.5+row)*(height/Ny))).add(_vUp.scale((0.5+column)*(width/Nx)));//
                Vector vector=focalPoint.subtract(point);
                Ray ray1= new Ray(point, vector);
                rays.add(ray1);
            }
        }
        return rays;

    }

}