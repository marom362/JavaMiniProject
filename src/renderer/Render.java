package renderer;

import elements.*;
import geometries.*;
import primitives.*;
import geometries.Intersectable.GeoPoint;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * class Render
 */
public class Render {

    /**
     *
     */
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    /**
     *
     */
    private static final double MIN_CALC_COLOR_K = 0.001;


    /**
     * _scene contain geometries camera and lights
     */
    private Scene _scene;
    /**
     *Image Writer
     */
    private ImageWriter _imageWriter;

    /**
     *constructor
     * @param _scene-the scene of the Render
     */
    public Render(Scene _scene) {
        this._scene = _scene;
    }

    /**
     * constructor
     * @param imageWriter-the imageWriter of the Render
     * @param scene-the scene of the Render
     */

    public Render(ImageWriter imageWriter, Scene scene) {
        this._imageWriter = imageWriter;
        this._scene = scene;
    }


    //***********get****************

    public Scene get_scene() {
        return _scene;
    }

    /**
     * Filling the buffer according to the geometries that are in the scene.
     * This function does not creating the picture file, but create the buffer pf pixels
     */
    public void renderImage() {
        java.awt.Color background = _scene.getBackground().getColor();
        Camera camera = _scene.getCamera();
        Intersectable geometries = _scene.getGeometries();
        double distance = _scene.getDistance();

        //width and height are the number of pixels in the rows
        //and columns of the view plane
        int width = (int) _imageWriter.getWidth();
        int height = (int) _imageWriter.getHeight();

        //Nx and Ny are the width and height of the image.
        int Nx = _imageWriter.getNx(); //columns
        int Ny = _imageWriter.getNy(); //rows
        //pixels grid
        for (int row = 0; row < Ny; ++row) {
            for (int column = 0; column < Nx; ++column) {
                Ray ray = camera.constructRayThroughPixel(Nx, Ny, column, row, distance, width, height);
                List<GeoPoint> intersectionPoints = geometries.findIntersections(ray);

                if (intersectionPoints == null) {
                    _imageWriter.writePixel(column, row, background);
                } else {
                    GeoPoint closestPoint = findClosestIntersection(ray);
                    if(closestPoint==null)
                        _imageWriter.writePixel(column, row, background);
                    java.awt.Color pixelColor = calcColor(closestPoint,ray).getColor();
                    _imageWriter.writePixel(column, row, pixelColor);
                }
            }
        }
    }

    /**
     *Calculating the intersections and also calculating the intersection near the beginning of the fund
     * @param ray
     * @return closest intersection
     */
    private GeoPoint findClosestIntersection(Ray ray) {
        GeoPoint result = null;
        double mindist = Double.MAX_VALUE;
        List<GeoPoint> intersectionPoints = _scene.getGeometries().findIntersections(ray);
        if (intersectionPoints == null)
            return null;
        for (GeoPoint geo : intersectionPoints) {
            Point3D pt = geo.getPoint();
            double distance = ray.getPoint().distance(pt);
            if (distance < mindist) {
                mindist = distance;
                result = geo;
            }
        }
        return result;
    }
    /**
     * Finding the closest point to the P0 of the camera.
     *
     * @param intersectionPoints list of points, the function should find from
     *                           this list the closet point to P0 of the camera in the scene.
     * @return the closest point to the camera
     */

    private GeoPoint getClosestPoint(List<GeoPoint> intersectionPoints) {
        GeoPoint result = null;
        double mindist = Double.MAX_VALUE;

        Point3D p0 = this._scene.getCamera().getP0();

        for (GeoPoint geo : intersectionPoints) {
            Point3D pt = geo.getPoint();
            double distance = p0.distance(pt);
            if (distance < mindist) {
                mindist = distance;
                result = geo;
            }
        }
        return result;
    }

    /**
     * Printing the grid with a fixed interval between lines
     *
     * @param interval The interval between the lines.
     */
    public void printGrid(int interval, java.awt.Color colorsep) {
        double rows = this._imageWriter.getNy();
        double collumns = _imageWriter.getNx();
        //Writing the lines.
        for (int row = 0; row < rows; ++row) {
            for (int collumn = 0; collumn < collumns; ++collumn) {
                if (collumn % interval == 0 || row % interval == 0) {
                    _imageWriter.writePixel(collumn, row, colorsep);
                }
            }
        }
    }
    public void writeToImage() {
        _imageWriter.writeToImage();
    }

    /**
     * construct refracted ray
     * @param point3D
     * @param ray
     * @return refracted ray
     */
    public Ray constructRefractedRay(Vector normal,Point3D point3D , Ray ray)
    {
        return new Ray( point3D, ray.getDirection(),normal);
    }

    /**
     * construct reflected ray
     * @param normal
     * @param point3D
     * @param ray
     * @return reflected ray
     */
    public Ray constructReflectedRay(Vector normal, Point3D point3D , Ray ray)
    {
        Vector vector=ray.getDirection();
        double vn=vector.dotProduct(normal);
        if(vn==0)
        {
            return null;
        }
        Vector r=vector.subtract(normal.scale(2*vn));
        Ray ReflectedRay = new Ray(point3D , r ,normal);


        return ReflectedRay;
    }

    /**
     * calculate the color at the point
     * call the Recursive function
     * @param gp point and geometry
     * @param inRay the ray to the point
     * @return color intensuty
     */
    private Color calcColor(GeoPoint gp,Ray inRay)
    {
        GeoPoint closestPoint= findClosestIntersection(inRay);
        Color result = _scene.getAmbientLight().getIntensity();
        result =result.add(calcColor(closestPoint, inRay, MAX_CALC_COLOR_LEVEL, 1d));
        return  result;
    }
    /**
     * Calculate the color intensity in a point-Recursive function
     * @param gp intersection the point for which the color is required
     * @param inRay the ray to the point
     * @param level for the stop condition of the recursive.
     * @param k
     * @return the color intensity
     */


    private Color calcColor(GeoPoint gp, Ray inRay, int level, double k) {
       if (level == 0 || k < MIN_CALC_COLOR_K)
           return Color.BLACK;

        Color result = gp.getGeometry().getEmissionLight();
        //result.add(gp.getGeometry().getEmissionLight());

        Vector v = gp.getPoint().subtract(_scene.getCamera().getP0()).normalize();
        Vector n = gp.getGeometry().getNormal(gp.getPoint());

        Material material = gp.getGeometry().getMaterial();
        double nShininess = material.getnShininess();
        double kd = material.getKd();
        double ks = material.getKs();
        List<LightSource> lights = _scene.getLightSources();
        if (_scene.getLightSources() != null) {
            for (LightSource lightSource : lights) {

                Vector l = lightSource.getL(gp.getPoint());
                double nl = alignZero(n.dotProduct(l));
                double nv = alignZero(n.dotProduct(v));

                if (nl*nv>0) {
                    double ktr=transparency(lightSource,l,n,gp);
                    if (ktr*k>MIN_CALC_COLOR_K)
                    {

                        Color ip = lightSource.getIntensity(gp.getPoint()).scale(ktr);
                        //if(ktr==0.75 )//&& gp.getGeometry().getEmissionLight()==new Color(java.awt.Color.BLUE))
                         //   System.out.println(gp.getGeometry().getEmissionLight().getColor().toString());//"spe"+calcSpecular(ks, l, n, nl, v, nShininess, ip).getColor().toString()+" "+"def"+calcDiffusive(kd, nl, ip).getColor().toString());
                        result = result.add(
                                calcDiffusive(kd, nl, ip),
                                calcSpecular(ks, l, n, nl, v, nShininess, ip)
                        );
                    }

                }
            }
            if (level == 1)
                return Color.BLACK;
            double kr = gp.getGeometry().getMaterial().getKR(), kkr = k * kr;
            if (kkr > MIN_CALC_COLOR_K)
            {
                Ray reflectedRay = constructReflectedRay(n, gp.getPoint(), inRay);
                GeoPoint reflectedPoint = findClosestIntersection(reflectedRay);

                if (reflectedPoint != null){
                    result = result.add(calcColor(reflectedPoint, reflectedRay,
                            level - 1, kkr).scale(kr));}

            }
            double kt = gp.getGeometry().getMaterial().getKT(), kkt = k * kt;
            if (kkt > MIN_CALC_COLOR_K) {
                Ray refractedRay = constructRefractedRay(n,gp.getPoint(), inRay) ;
                GeoPoint refractedPoint = findClosestIntersection(refractedRay);
                if (refractedPoint != null) {
                    result = result.add(calcColor(refractedPoint, refractedRay,
                            level - 1, kkt).scale(kt));
                }
            }

        }

        return result;
    }
    /**
     * A non-shading test between a point and a light source
     * @param l-  from light source to point (GP)
     * @param n -normal of geometry in point (gp)
     * @param gp- the point of the geometry
     * @param lightSource-the light source
     * @return
     */
    public boolean unshaded(LightSource lightSource,Vector l, Vector n, Intersectable.GeoPoint gp) {

        Vector lightDirection = l.scale(-1); // from point to light source

        Ray lightRay = new Ray(gp.getPoint(), lightDirection ,n);
        List<Intersectable.GeoPoint> intersections = _scene.getGeometries().findIntersections(lightRay);
        if (intersections==null)
            return true;
        double lightDistance = lightSource.getDistance(gp.getPoint());
        for (GeoPoint gpoint : intersections) {
            if (alignZero(gpoint.getPoint().distance(gp.getPoint())- lightDistance) <= 0 && gpoint.getGeometry().getMaterial().getKR()==0)
                return false;
        }
        return true;
    }
    private double transparency(LightSource lightSource, Vector l, Vector n, GeoPoint gp)
    {
        Vector lightDirection = l.scale(-1); // from point to light source

        Ray lightRay = new Ray(gp.getPoint(), lightDirection ,n);
        List<Intersectable.GeoPoint> intersections = _scene.getGeometries().findIntersections(lightRay);
        if (intersections==null)
            return 1d;
        double lightDistance = lightSource.getDistance(gp.getPoint());
        double ktr=1d;
        for (GeoPoint gpoint : intersections) {
            if (alignZero(gpoint.getPoint().distance(gp.getPoint())- lightDistance)<=0){
                ktr*=gpoint.getGeometry().getMaterial().getKT();
                if(ktr<MIN_CALC_COLOR_K)
                    return 0d;

            }

        }

        return ktr;
    }

    private boolean sign(double val) {
        return (val > 0d);
    }

    /**
     * Calculate Specular component of light reflection.
     *
     * @param ks         specular component coef
     * @param l          direction from light to point
     * @param n          normal to surface at the point
     * @param nl         dot-product n*l
     * @param v          direction from point of view to point
     * @param nShininess shininess level
     * @param ip         light intensity at the point
     * @return specular component light effect at the point
     * @author Dan Zilberstein
     * <p>
     * Finally, the Phong model has a provision for a highlight, or specular, component, which reflects light in a
     * shiny way. This is defined by [rs,gs,bs](-V.R)^p, where R is the mirror reflection direction vector we discussed
     * in class (and also used for ray tracing), and where p is a specular power. The higher the value of p, the shinier
     * the surface.
     */
    private Color calcSpecular(double ks, Vector l, Vector n, double nl, Vector v, double nShininess, Color ip) {
        double p = nShininess;

        Vector R = l.subtract(n.scale(2 * nl)); // nl must not be zero!

        double minusVR = -alignZero(R.dotProduct(v));
        if (minusVR <= 0) {
            return Color.BLACK; // view from direction opposite to r vector
        }
        return ip.scale(ks * Math.pow(minusVR, p));
    }

    /**
     * Calculate Diffusive component of light reflection.
     *
     * @param kd diffusive component coef
     * @param nl dot-product n*l
     * @param ip light intensity at the point
     * @return diffusive component of light reflection
     * @author Dan Zilberstein
     * <p>
     * The diffuse component is that dot product n•L that we discussed in class. It approximates light, originally
     * from light source L, reflecting from a surface which is diffuse, or non-glossy. One example of a non-glossy
     * surface is paper. In general, you'll also want this to have a non-gray color value, so this term would in general
     * be a color defined as: [rd,gd,bd](n•L)
     */
    private Color calcDiffusive(double kd, double nl, Color ip) {
        if (nl < 0) nl = -nl;
           return ip.scale(nl * kd);
    }

}