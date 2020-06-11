package primitives;

/**
 *class Vector
 * comments are missing
 *
 */
public class Vector {
    Point3D _head;

    /**
     * constructor
     * @param p - point
     * @throws   IllegalArgumentException Point3D(0.0,0.0,0.0) not valid for vector head
     */
    public Vector(Point3D p) {
        if (p.equals(Point3D.ZERO)) {
            throw new IllegalArgumentException("Point3D(0.0,0.0,0.0) not valid for vector head");
        }
        this._head = new Point3D(p._x._coord, p._y._coord, p._z._coord);
    }

    /**
     * constructor
     * @param v
     * @throws   IllegalArgumentException Point3D(0.0,0.0,0.0) not valid for vector head
     */
    public Vector(Vector v) {
        this(v._head);
    }

    /**
     * constructor
     *Subtraction between 2 points
     * @param p1
     * @param p2
     * @throws   IllegalArgumentException Point3D(0.0,0.0,0.0) not valid for vector head
     */
    public Vector(Point3D p1, Point3D p2) {
        this(p1.subtract(p2));
    }

    /**
     * constructor
     * 3 double
     * @param x
     * @param y
     * @param z
     */
    public Vector(double x,double y, double z) {
        this(new Point3D(x,y,z));
    }

    /**
     * add vector to vector
     * @param vector
     */
    public Vector  add(Vector vector)
    {
        return  new Vector(this._head.add(vector));
    }
//*******get*******
    public Point3D getHead() {
        return new Point3D(_head._x._coord, _head._y._coord, _head._z._coord);
    }

    /**
     * subtract vector to vector
     * @param vector
     */
    public Vector subtract(Vector vector) {
        return  this._head.subtract(vector._head);
    }

    /**
     * subtract vector to scalar
     * @param scalingFacor
     */
    public Vector scale(double scalingFacor) {
        return new Vector(
                new Point3D(
                        new Coordinate(scalingFacor * _head._x._coord),
                        new Coordinate(scalingFacor * _head._y._coord),
                        new Coordinate(scalingFacor * _head._z._coord)));
    }

    /**
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return _head.equals(vector._head);
    }

    /**
     * dotProduct vector to vector
     * @param v
     * @return double
     */
    public double dotProduct(Vector v) {
        return (this._head._x._coord * v._head._x._coord +
                this._head._y._coord * v._head._y._coord +
                this._head._z._coord * v._head._z._coord);
    }

    /**
     * @param v the second vector
     * @return new Vector for the crossproduct (this X v)
     */
    public Vector crossProduct(Vector v) {
        double w1 = this._head._y._coord * v._head._z._coord - this._head._z._coord * v._head._y._coord;
        double w2 = this._head._z._coord * v._head._x._coord - this._head._x._coord * v._head._z._coord;
        double w3 = this._head._x._coord * v._head._y._coord - this._head._y._coord * v._head._x._coord;

        return new Vector(new Point3D(w1, w2, w3));
    }

    /**
     *
     * @return the length Squared
     */
    public double lengthSquared() {
        double xx = this._head._x._coord * this._head._x._coord;
        double yy = this._head._y._coord * this._head._y._coord;
        double zz = this._head._z._coord * this._head._z._coord;

        return xx + yy + zz;

    }

    /**
     * @return the length
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * @return the same Vector after normalisation
     * @throws ArithmeticException if length = 0
     */
    public Vector normalize() {

        double x = this._head._x._coord;
        double y = this._head._y._coord;
        double z = this._head._z._coord;

        double length = this.length();

        if (length == 0)
            throw new ArithmeticException("divide by Zero");

        this._head._x = new Coordinate(x / length);
        this._head._y = new Coordinate(y / length);
        this._head._z = new Coordinate(z / length);

        return this;
    }

    /**
     * @return the new Vector after normalisation
     * @throws ArithmeticException if length = 0
     */
    public Vector normalized() {
        Vector vector = new Vector(this);
        vector.normalize();
        return vector;
    }

    @Override
    public String toString() {
        return  _head .toString();
    }
}