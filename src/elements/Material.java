package elements;

/**
 * class Material-
 *
 * params:
 *  _kD,_kS,_nShininess
 */
public class Material {

    private double _kD;
    private double _kS;
    private int _nShininess;

    /**
     *  constructor
     * @param _kD
     * @param _kS
     * @param _nShininess
     */
    public Material(double _kD, double _kS, int _nShininess) {
        this._kD = _kD;
        this._kS = _kS;
        this._nShininess = _nShininess;
    }

    /**
     * constructor
     * @param material
     */

    public Material(Material material) {
        this(material._kD, material._kS, material._nShininess);
    }

    //*********get*****************

    public double getKd() {
        return _kD;
    }

    public double getKs() {
        return _kS;
    }

    public int getnShininess() {
        return _nShininess;
    }
}