package au.csiro.portal.models;

public class BoreholeClassification {
    private double startDepth;
    private double endDepth;
    private String classification;
    public BoreholeClassification(double startDepth, double endDepth,
            String classification) {
        super();
        this.startDepth = startDepth;
        this.endDepth = endDepth;
        this.classification = classification;
    }
    public double getStartDepth() {
        return startDepth;
    }
    public void setStartDepth(double startDepth) {
        this.startDepth = startDepth;
    }
    public double getEndDepth() {
        return endDepth;
    }
    public void setEndDepth(double endDepth) {
        this.endDepth = endDepth;
    }
    public String getClassification() {
        return classification;
    }
    public void setClassification(String classification) {
        this.classification = classification;
    }
    
    

    
    
}
