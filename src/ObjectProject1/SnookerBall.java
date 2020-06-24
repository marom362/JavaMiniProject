package ObjectProject1;

import elements.Material;
import geometries.Sphere;
import primitives.Color;
import primitives.Point3D;
import primitives.Vector;

/**
 * A class that creates a snooker ball and extends Sphere
 */
public class SnookerBall extends Sphere {
    final static double RADIUS=5;
    final static double COSLITTLECIRCLE=0.939;
    final static double COSBIGCIRCLE=0.642;
    //2 vectors that determine the location of the white circles
    private Vector vecLittleCircle;
    private Vector vecBigCircle;
    //A Boolean variable that determines if there is one or two white circles
    private boolean oneColor;

    /**
     * constructor
     * @param emissionLight
     * @param radius
     * @param center
     * @param vecLittleCircle
     * @param vecBigCircle
     */
    public SnookerBall(Color emissionLight,  double radius, Point3D center, Vector vecLittleCircle, Vector vecBigCircle) {
        this(emissionLight, radius, center, vecLittleCircle);
        this.vecBigCircle = vecBigCircle.normalize();
        this.oneColor=false;
    }

    /**
     * constructor
     * @param emissionLight
     * @param radius
     * @param center
     * @param vecLittleCircle
     */
    public SnookerBall(Color emissionLight, double radius, Point3D center, Vector vecLittleCircle) {
        super(emissionLight, radius, center);
        this._material=new Material(0.8,0.2,200,0,0.2);
        this.vecLittleCircle = vecLittleCircle.normalize();
        this.oneColor=true;
    }

    /**
     *
     * @param gp
     * @return get Emission Light
     */
    @Override
    public Color getEmissionLight(GeoPoint gp) {

        if(this.oneColor==false)
        {
            if (gp.getPoint().subtract(this.getCenter()).normalize().dotProduct(vecBigCircle)>=this.COSBIGCIRCLE ||
                    gp.getPoint().subtract(this.getCenter()).normalize().dotProduct(vecBigCircle.scale(-1))>=this.COSBIGCIRCLE)
                return (new Color(225,223,170));
        }
        if(gp.getPoint().subtract(this.getCenter()).normalize().dotProduct(vecLittleCircle)>=this.COSLITTLECIRCLE||
                gp.getPoint().subtract(this.getCenter()).normalize().dotProduct(vecLittleCircle.scale(-1))>=this.COSLITTLECIRCLE)
            return (new Color(225,223,170));
        return super.getEmissionLight();
    }
}
