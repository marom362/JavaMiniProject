package ObjectProject1;

import elements.Material;
import geometries.Geometries;
import geometries.Polygon;
import primitives.Color;
import primitives.Point3D;
import primitives.Vector;

/**
 * A class that makes up a snooker table with rectangles
 */
public class Table
{
    double tableWidth;
    double tableLength;
    double holeSize;
    double edgeWidth;
    double edgeHeight;
    double woodWidth;
    double edgeWidth2;
    private  Geometries tableParts = new Geometries();
    Point3D position;
    Vector widthVec;
    Vector lengthVec;


    /**
     * constructor
     * @param tableWidth
     * @param tableLength
     * @param holeSize
     * @param edgeWidth
     * @param edgeHeight
     * @param edgeWidth2
     * @param woodWidth
     * @param widthVec
     * @param lengthVec
     * @param position
     */
    public Table(double tableWidth, double tableLength, double holeSize, double edgeWidth, double edgeHeight,
                 double edgeWidth2,double woodWidth,
                  Vector widthVec,Vector lengthVec,
                  Point3D position) {
        this.tableWidth = tableWidth;
        this.tableLength = tableLength;
        this.holeSize = holeSize;
        this.edgeWidth = edgeWidth;
        this.edgeHeight = edgeHeight;
        this.position=position;
        this.woodWidth=woodWidth;
        this.edgeWidth2=edgeWidth2;

        this.lengthVec=lengthVec.normalize();
        this.widthVec=widthVec.normalize();

        Vector heightVec= widthVec.crossProduct(lengthVec).normalize();


        Material materialWood=new Material(0.7,0.7,200);
        Material materialGreen=new Material(0.7,0.7,100);
        Color colorWood=new Color(77,18,0);
        Color colorGreen=new Color(10,135,47);


        Point3D point1=position;
        Point3D point2=point1.add(widthVec.scale(tableWidth));
        Point3D point3=point2.add(lengthVec.scale(tableLength));
        Point3D point4=position.add(lengthVec.scale(tableLength));
        Polygon TablePlane=new Polygon(colorGreen,materialGreen,point1, point2, point3, point4);

        Point3D point111=point1.subtract(widthVec.scale(edgeWidth));
        Point3D point112=point111.add(lengthVec.scale((tableLength-holeSize)/2));
        Point3D point113=point112.add(widthVec.scale(edgeWidth));
        Polygon rect11=new Polygon(colorGreen,materialGreen,point1, point111, point112,point113);

        Point3D point121=point2.add(widthVec.scale(edgeWidth));
        Point3D point122=point121.add(lengthVec.scale((tableLength-holeSize)/2));
        Point3D point123=point122.subtract(widthVec.scale(edgeWidth));
        Polygon rect12=new Polygon(colorGreen,materialGreen,point2, point121, point122,point123);

        Point3D point131=point3.add(widthVec.scale(edgeWidth));
        Point3D point132=point131.subtract(lengthVec.scale((tableLength-holeSize)/2));
        Point3D point133=point132.subtract(widthVec.scale(edgeWidth));
        Polygon rect13=new Polygon(colorGreen,materialGreen,point3, point131, point132,point133);

        Point3D point141=point4.subtract(widthVec.scale(edgeWidth));
        Point3D point142=point141.subtract(lengthVec.scale((tableLength-holeSize)/2));
        Point3D point143=point142.add(widthVec.scale(edgeWidth));
        Polygon rect14=new Polygon(colorGreen,materialGreen,point4, point141, point142,point143);


        Point3D point151=point1.subtract(lengthVec.scale(edgeWidth));
        Point3D point152=point2.subtract(lengthVec.scale(edgeWidth));
        Polygon rect15=new Polygon(colorGreen,materialGreen,point1,point151, point152,point2);

        Point3D point161=point3.add(lengthVec.scale(edgeWidth));
        Point3D point162=point4.add(lengthVec.scale(edgeWidth));
        Polygon rect16=new Polygon(colorGreen,materialGreen,point3, point161, point162,point4);





        Point3D point211=point111.add(heightVec.add(new Vector(0.2,0,0)).normalize().scale(edgeHeight));
        Point3D point212=point211.add(lengthVec.scale((tableLength-holeSize)/2));
        Polygon rect21=new Polygon(colorGreen,materialGreen,point111, point211, point212,point112);

        Point3D point221=point121.add(heightVec.add(new Vector(-0.2,0,0)).normalize().scale(edgeHeight));
        Point3D point222=point221.add(lengthVec.scale((tableLength-holeSize)/2));
        Polygon rect22=new Polygon(colorGreen,materialGreen,point121, point221, point222,point122);

        Point3D point231=point131.add(heightVec.add(new Vector(-0.2,0,0)).normalize().scale(edgeHeight));
        Point3D point232=point231.subtract(lengthVec.scale((tableLength-holeSize)/2));
        Polygon rect23=new Polygon(colorGreen,materialGreen,point131, point231, point232,point132);

        Point3D point241=point141.add(heightVec.add(new Vector(0.2,0,0)).normalize().scale(edgeHeight));
        Point3D point242=point241.subtract(lengthVec.scale((tableLength-holeSize)/2));
        Polygon rect24=new Polygon(colorGreen,materialGreen,point141, point241, point242,point142);


        Point3D point251=point151.add(heightVec.add(new Vector(0,0,0.2)).normalize().scale(edgeHeight));
        Point3D point252=point152.add(heightVec.add(new Vector(0,0,0.2)).scale(edgeHeight));
        Polygon rect25=new Polygon(colorGreen,materialGreen,point151, point251, point252,point152);

        Point3D point261=point161.add(heightVec.add(new Vector(0,0,-0.2)).scale(edgeHeight));
        Point3D point262=point162.add(heightVec.add(new Vector(0,0,-0.2)).scale(edgeHeight));
        Polygon rect26=new Polygon(colorGreen,materialGreen,point161, point261, point262,point162);



        Point3D point311=point211.subtract(widthVec.scale(edgeWidth2));
        Point3D point312=point212.subtract(widthVec.scale(edgeWidth2));
        Polygon rect31=new Polygon(colorGreen,materialGreen,point211, point311, point312,point212);

        Point3D point321=point221.add(widthVec.scale(edgeWidth2));
        Point3D point322=point222.add(widthVec.scale(edgeWidth2));
        Polygon rect32=new Polygon(colorGreen,materialGreen,point221, point321, point322,point222);

        Point3D point331=point231.add(widthVec.scale(edgeWidth2));
        Point3D point332=point232.add(widthVec.scale(edgeWidth2));
        Polygon rect33=new Polygon(colorGreen,materialGreen,point331, point231, point232,point332);

        Point3D point341=point241.subtract(widthVec.scale(edgeWidth2));
        Point3D point342=point242.subtract(widthVec.scale(edgeWidth2));
        Polygon rect34=new Polygon(colorGreen,materialGreen,point241, point341, point342,point242);


        Point3D point351=point251.subtract(lengthVec.scale(edgeWidth2));
        Point3D point352=point252.subtract(lengthVec.scale(edgeWidth2));
        Polygon rect35=new Polygon(colorGreen,materialGreen,point351, point251, point252,point352);

        Point3D point361=point261.add(lengthVec.scale(edgeWidth2));
        Point3D point362=point262.add(lengthVec.scale(edgeWidth2));
        Polygon rect36=new Polygon(colorGreen,materialGreen,point361, point261, point262,point362);





        Point3D point411=point311.subtract(lengthVec.scale(edgeWidth+edgeWidth2));
        Point3D point412=point411.subtract(widthVec.scale(woodWidth));
        Point3D point413=point341.add(lengthVec.scale(edgeWidth+edgeWidth2));
        Point3D point414=point413.subtract(widthVec.scale(woodWidth));
        Polygon rect41=new Polygon(colorWood,materialWood,point411, point412, point414,point413);


        Point3D point421=point321.subtract(lengthVec.scale(edgeWidth+edgeWidth2));
        Point3D point422=point421.add(widthVec.scale(woodWidth));
        Point3D point423=point331.add(lengthVec.scale(edgeWidth+edgeWidth2));
        Point3D point424=point423.add(widthVec.scale(woodWidth));

        Polygon rect42=new Polygon(colorWood,materialWood,point421, point422, point424,point423);

        Point3D point431=point412.subtract(lengthVec.scale(woodWidth));
        Point3D point432=point422.subtract(lengthVec.scale(woodWidth));
        Polygon rect43=new Polygon(colorWood,materialWood,point431, point432, point422,point412);


        Point3D point441=point414.add(lengthVec.scale(woodWidth));
        Point3D point442=point424.add(lengthVec.scale(woodWidth));
        Polygon rect44=new Polygon(colorWood,materialWood,point441, point442, point424,point414);


        Point3D point511=point441.subtract(heightVec.scale(edgeHeight*2));
        Point3D point512=point442.subtract(heightVec.scale(edgeHeight*2));
        Polygon rect51=new Polygon(colorWood,materialWood,point511, point441, point442,point512);


        Point3D point521=point431.subtract(heightVec.scale(edgeHeight*2));
        Point3D point522=point432.subtract(heightVec.scale(edgeHeight*2));
        Polygon rect52=new Polygon(colorWood,materialWood,point521, point431, point432,point522);






        tableParts.add(TablePlane,rect11,rect12,rect13,rect14,rect15,rect16,rect21,rect22,rect23
        ,rect24,rect25,rect26,rect31,rect32,rect33,rect34,rect35,rect36,rect41,rect42,rect43,rect44,rect51,rect52);



    }

    public Geometries getTableParts() {
        return tableParts;
    }
}
