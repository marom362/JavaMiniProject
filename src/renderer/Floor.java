package renderer;

import elements.Material;
import geometries.Geometries;
import geometries.Polygon;
import primitives.Color;
import primitives.Point3D;
import primitives.Vector;

public class Floor
{
    Geometries floorParts=new Geometries();


    public Geometries getFloorParts() {
        return floorParts;
    }

    public Floor(Point3D point, Vector v1, Vector v2, double size, double spaceSize, Color color2, Color color1, Material material) {
        Vector v3=v1.crossProduct(v2);
        //floorParts.add(new Plane(point.subtract(v3.scale(0.1)),v3));
        Point3D point1;
        for (int i = 1; i <20 ; i++) {
            point1=point.add(v1.scale(i*(size+spaceSize)));
            for (int j = 1; j <20 ; j++) {
                Point3D point2=point1.add(v1.scale(size));
                Point3D point3=point2.add(v2.scale(size));
                Point3D point4=point3.subtract(v1.scale(size));

                if((i+j)%2==0) {
                    //System.out.println(" 1 " +point1 +"2 "+ point2+"3 "+point3+"4"+point4);
                    floorParts.add(new Polygon(color1, material, point1, point2, point3, point4));
                }
                else
                    floorParts.add(new Polygon(color2,material,point1,point2,point3,point4));

                point1=point4;



            }

        }

    }
}
