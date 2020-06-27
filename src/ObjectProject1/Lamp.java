package ObjectProject1;

import elements.Material;
import elements.PointLight;
import geometries.Geometries;
import geometries.Sphere;
import primitives.Color;
import primitives.Point3D;
import primitives.Vector;

public class Lamp
{
    private Geometries lampParts=new Geometries();
    PointLight lightLamp;

    public Lamp(Color color, double radius,double radius1 ,PointLight lightLamp,Vector v) {
        this.lightLamp=lightLamp;
            Material materialLamp=new Material(0.2,0.2,100,0.8,0);
        Material materialLamp1=new Material(0.2,0.2,200,0,0);

        Sphere s=new Sphere(color,materialLamp,radius,lightLamp.get_position());
        Point3D center1=lightLamp.get_position().add(v.scale(radius));
        Sphere s1=new Sphere(new Color(140,140,140),materialLamp1,radius1,center1);
        //Polygon


        lampParts.add(s,s1);




    }

    public Geometries getLampParts() {
        return lampParts;
    }

    public PointLight getLightLamp() {
        return lightLamp;
    }
}
