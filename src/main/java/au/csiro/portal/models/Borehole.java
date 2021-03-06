package au.csiro.portal.models;

public class Borehole {
    private String name;
    private Double totalDepth;
    private Point3D[] points;
    private String nvclFeatureUri;
    private String nvclName;
    private String nvclId;
    private String nvclImageId;
    private String nvclDataUrl;
    
    
    public Borehole(String name, Double totalDepth, Point3D[] points) {
        super();
        this.name = name;
        this.totalDepth = totalDepth;
        this.points = points;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Double getTotalDepth() {
        return totalDepth;
    }
    public void setTotalDepth(Double totalDepth) {
        this.totalDepth = totalDepth;
    }
    public Point3D[] getPoints() {
        return points;
    }
    public void setPoints(Point3D[] points) {
        this.points = points;
    }
    public String getNvclFeatureUri() {
        return nvclFeatureUri;
    }
    public void setNvclFeatureUri(String nvclFeatureUri) {
        this.nvclFeatureUri = nvclFeatureUri;
    }
    public String getNvclName() {
        return nvclName;
    }
    public void setNvclName(String nvclName) {
        this.nvclName = nvclName;
    }
    public String getNvclId() {
        return nvclId;
    }
    public void setNvclId(String nvclId) {
        this.nvclId = nvclId;
    }
    public String getNvclDataUrl() {
        return nvclDataUrl;
    }
    public void setNvclDataUrl(String nvclDataUrl) {
        this.nvclDataUrl = nvclDataUrl;
    }
    public String getNvclImageId() {
        return nvclImageId;
    }
    public void setNvclImageId(String nvclImageId) {
        this.nvclImageId = nvclImageId;
    }
}
