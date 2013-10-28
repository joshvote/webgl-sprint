package au.csiro.portal.models;

public class Borehole {
    private double latitude;
    private double longitude;    
    private Point3D[] points;

    public Borehole(double latitude, double longitude, Point3D[] points) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.points = points;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Point3D[] getPoints() {
        return points;
    }

    public void setPoints(Point3D[] points) {
        this.points = points;
    }
    
    
}
