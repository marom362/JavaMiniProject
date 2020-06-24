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
    private int _threads = 1;
    private final int SPARE_THREADS = 4;
    private boolean _print = false;

    /**
     * Pixel is an internal helper class whose objects are associated with a Render object that
     * they are generated in scope of. It is used for multithreading in the Renderer and for follow up
     * its progress.<br/>
     * There is a main follow up object and several secondary objects - one in each thread.
     * @author Dan
     *
     */
    private class Pixel {
        private long _maxRows = 0;
        private long _maxCols = 0;
        private long _pixels = 0;
        public volatile int row = 0;
        public volatile int col = -1;
        private long _counter = 0;
        private int _percents = 0;
        private long _nextCounter = 0;

        /**
         * The constructor for initializing the main follow up Pixel object
         * @param maxRows the amount of pixel rows
         * @param maxCols the amount of pixel columns
         */
        public Pixel(int maxRows, int maxCols) {
            _maxRows = maxRows;
            _maxCols = maxCols;
            _pixels = maxRows * maxCols;
            _nextCounter = _pixels / 100;
            if (Render.this._print) System.out.printf("\r %02d%%", _percents);
        }

        /**
         *  Default constructor for secondary Pixel objects
         */
        public Pixel() {}

        /**
         * Internal function for thread-safe manipulating of main follow up Pixel object - this function is
         * critical section for all the threads, and main Pixel object data is the shared data of this critical
         * section.<br/>
         * The function provides next pixel number each call.
         * @param target target secondary Pixel object to copy the row/column of the next pixel
         * @return the progress percentage for follow up: if it is 0 - nothing to print, if it is -1 - the task is
         * finished, any other value - the progress percentage (only when it changes)
         */
        private synchronized int nextP(Pixel target) {
            ++col;
            ++_counter;
            if (col < _maxCols) {
                target.row = this.row;
                target.col = this.col;
                if (_counter == _nextCounter) {
                    ++_percents;
                    _nextCounter = _pixels * (_percents + 1) / 100;
                    return _percents;
                }
                return 0;
            }
            ++row;
            if (row < _maxRows) {
                col = 0;
                if (_counter == _nextCounter) {
                    ++_percents;
                    _nextCounter = _pixels * (_percents + 1) / 100;
                    return _percents;
                }
                return 0;
            }
            return -1;
        }

        /**
         * Public function for getting next pixel number into secondary Pixel object.
         * The function prints also progress percentage in the console window.
         * @param target target secondary Pixel object to copy the row/column of the next pixel
         * @return true if the work still in progress, -1 if it's done
         */
        public boolean nextPixel(Pixel target) {
            int percents = nextP(target);
            if (percents > 0)
                if (Render.this._print) System.out.printf("\r %02d%%", percents);
            if (percents >= 0)
                return true;
            if (Render.this._print) System.out.printf("\r %02d%%", 100);
            return false;
        }
    }
    public Render(Scene _scene) {
        this._scene = _scene;
    }

    /**
     * constructor
     * @param _imageWriter-the imageWriter of the Render
     * @param _scene-the scene of the Render
     */



    public Render(  ImageWriter _imageWriter,Scene _scene) {
        this._scene = _scene;
        this._imageWriter = _imageWriter;
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
        final Camera camera = _scene.getCamera();
        Intersectable geometries = _scene.getGeometries();
        final double distance = _scene.getDistance();

        //width and height are the number of pixels in the rows
        //and columns of the view plane
        final double width =  _imageWriter.getWidth();
        final double height =  _imageWriter.getHeight();

        //Nx and Ny are the width and height of the image.
        final int Nx = _imageWriter.getNx(); //columns
        final int Ny = _imageWriter.getNy(); //rows

        final Pixel thePixel = new Pixel(Ny, Nx);

        // Generate threads
        Thread[] threads = new Thread[_threads];
        for (int i = _threads - 1; i >= 0; --i) {
            threads[i] = new Thread(() -> {
                Pixel pixel = new Pixel();
                while (thePixel.nextPixel(pixel)) {
                    List<Ray> rays = camera.constructRaysThroughPixel(Nx, Ny, pixel.col, pixel.row, //
                            distance, width, height);
                    _imageWriter.writePixel(pixel.col, pixel.row, calcColor(rays).getColor());
                }
            });

        }
        // Start threads
        for (Thread thread : threads) thread.start();

        // Wait for all threads to finish
        for (Thread thread : threads) try { thread.join(); } catch (Exception e) {}
        if (_print) System.out.printf("\r100%%\n");
    }



    /**
     * Set multithreading <br>
     * - if the parameter is 0 - number of coress less 2 is taken
     *
     * @param threads number of threads
     * @return the Render object itself
     */
    public Render setMultithreading(int threads) {
        if (threads < 0)
            throw new IllegalArgumentException("Multithreading patameter must be 0 or higher");
        if (threads != 0)
            _threads = threads;
        else {
            int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
            if (cores <= 2)
                _threads = 1;
            else
                _threads = cores;
        }
        return this;
    }

    /**
     * Set debug printing on
     *
     * @return the Render object itself
     */
    public Render setDebugPrint() {
        _print = true;
        return this;
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


    private Color colorPoint(Point3D p)
    {
        Ray ray=new Ray(_scene.getCamera().getP0(), p.subtract(_scene.getCamera().getP0()));
        GeoPoint gp=findClosestIntersection(ray);
        Color color=calcColor(gp,ray);
        return color;
    }
    private Color calcColor(List<Ray> rays)
    {
        Color color;
        int size=rays.size();
        double sumRed=0;
        double sumGreen=0;
        double sumBlue=0;
        for (Ray ray: rays)
        {
            GeoPoint closestPoint= findClosestIntersection(ray);
            color=calcColor(closestPoint, ray);
            sumRed+=color.getColor().getRed();
            sumGreen+=color.getColor().getGreen();
            sumBlue+=color.getColor().getBlue();
        }
        return new Color(sumRed/size,sumGreen/size,sumBlue/size);

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
       if(closestPoint==null)
          return this._scene.getBackground();
        Color result = _scene.getAmbientLight().getIntensity();
        result =result.add(calcColor(gp, inRay, MAX_CALC_COLOR_LEVEL, 1d));
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

        Color result = gp.getGeometry().getEmissionLight(gp);
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