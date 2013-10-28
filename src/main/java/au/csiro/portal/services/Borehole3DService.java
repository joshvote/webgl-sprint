package au.csiro.portal.services;

import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.methods.HttpRequestBase;
import org.auscope.portal.core.server.http.HttpServiceCaller;
import org.auscope.portal.core.services.BaseWFSService;
import org.auscope.portal.core.services.methodmakers.WFSGetFeatureMethodMaker;
import org.auscope.portal.core.services.methodmakers.WFSGetFeatureMethodMaker.ResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.csiro.portal.models.Borehole;

@Service
public class Borehole3DService extends BaseWFSService {

    private static final String ENDPOINT = "http://services-test.auscope.org/nvcl/wfs";
    private static final String FEATURE_TYPE = "demo:boreholes";
    
    @Autowired
    public Borehole3DService(HttpServiceCaller httpServiceCaller,
            WFSGetFeatureMethodMaker wfsMethodMaker) {
        super(httpServiceCaller, wfsMethodMaker);
    }

    /*public List<Borehole> getBoreholes(String featureId, Integer maxFeatures) {
        try {
            HttpRequestBase request = this.generateWFSRequest(ENDPOINT, FEATURE_TYPE, featureId, null, maxFeatures, "EPSG:4326", ResultType.Results);
            
            
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/
}
