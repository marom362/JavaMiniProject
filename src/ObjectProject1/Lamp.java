package ObjectProject1;

import elements.Material;
import elements.PointLight;
import geometries.Geometries;
import geometries.Polygon;
import geometries.Sphere;
import primitives.Color;
import primitives.Point3D;
import primitives.Vector;

public class Lamp
{
    private Geometries lampParts=new Geometries();
    PointLight lightLamp;

    public Lamp(Color color, Color lampShadeColor, double radius,double radius1 ,PointLight lightLamp,Vector v1, Vector v2) {
        this.lightLamp=lightLamp;
        Material materialLamp=new Material(0.2,0.2,100,0.8,0);
        Material lampShadeMaterial=new Material(0.2,0.2,200,0,0);
        Point3D center=lightLamp.get_position();
        Sphere s=new Sphere(color,materialLamp,radius,center);
        Vector v3=v1.crossProduct(v2);
        Point3D p1=center.add(v1.scale(2*radius));
        Point3D p2=center.add(v2.scale(2*radius));
        Point3D p3=center.add(v3.scale(2*radius));
        Point3D p4=center.subtract(v2.scale(2*radius));
        Point3D p5=center.subtract(v3.scale(2*radius));
        Sphere s1=new Sphere(new Color(140,140,140),lampShadeMaterial,radius1,p1);

        Point3D pCord1=p1.add(v2.scale(0.5));
        Point3D pCord2=p1.subtract(v2.scale(0.5));
        Point3D pCord3=p1.add(v3.scale(0.5));
        Point3D pCord4=p1.subtract(v3.scale(0.5));



        //Polygon


        lampParts.add(s,s1, new Polygon(lampShadeColor,lampShadeMaterial,p1,p2,p3),
                new Polygon(lampShadeColor,lampShadeMaterial,p1,p3,p4),
                new Polygon(lampShadeColor,lampShadeMaterial,p1,p2,p5),
                new Polygon(lampShadeColor,lampShadeMaterial,p1,p4,p5),
                new Polygon(new Color(140,140,140),lampShadeMaterial,pCord1,pCord2,pCord2.add(v1.scale(100)),pCord1.add(v1.scale(100))),
                new Polygon(new Color(140,140,140),lampShadeMaterial,pCord3,pCord4,pCord4.add(v1.scale(100)),pCord3.add(v1.scale(100))));




    }

    public Geometries getLampParts() {
        return lampParts;
    }

    public PointLight getLightLamp() {
        return lightLamp;
    }
}
