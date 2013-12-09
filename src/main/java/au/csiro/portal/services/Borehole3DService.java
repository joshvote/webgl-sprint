package au.csiro.portal.services;

import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.media.jai.LookupTableJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.FileLoadDescriptor;
import javax.media.jai.operator.LookupDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.auscope.portal.core.server.http.HttpServiceCaller;
import org.auscope.portal.core.services.BaseWFSService;
import org.auscope.portal.core.services.PortalServiceException;
import org.auscope.portal.core.services.methodmakers.WFSGetFeatureMethodMaker;
import org.auscope.portal.core.services.methodmakers.WFSGetFeatureMethodMaker.ResultType;
import org.auscope.portal.core.services.responses.ows.OWSExceptionParser;
import org.auscope.portal.core.util.DOMUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import au.csiro.portal.models.Borehole;
import au.csiro.portal.models.Point3D;
import au.csiro.portal.xml.BoreholeNamespace;

@Service
public class Borehole3DService extends BaseWFSService {

    private static final String ENDPOINT = "http://services-test.auscope.org/nvcl/wfs";
    private static final String FEATURE_TYPE = "demo:boreholes";

    //To generate a fake 1-1 correspondence with these boreholes
    private List<Map<String,String>> fakeNvclData;

    
    @Autowired
    public Borehole3DService(HttpServiceCaller httpServiceCaller,
            WFSGetFeatureMethodMaker wfsMethodMaker) {
        super(httpServiceCaller, wfsMethodMaker);
        
        fakeNvclData = new ArrayList<Map<String,String>>();
        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", "5be524cf-99c7-4306-824a-cefd56119be");
        map.put("imageId", "3e2f3fd5-f1c6-464f-9d18-8e0c14be0d6");
        map.put("wfsUri", "http://geossdi.dmp.wa.gov.au/resource/feature/gswa/borehole/rdh01");
        map.put("name", "rdh01");
        map.put("dataserviceUrl", "http://geossdi.dmp.wa.gov.au/NVCLDataServices/");
        fakeNvclData.add(map);
        
        map = new HashMap<String, String>();
        map.put("id", "545679fc-e67a-4ebe-9a04-1c6aeb84f02");
        map.put("imageId", "b2c6845f-9286-4fff-a16c-e6031779bc9");
        map.put("wfsUri", "http://geossdi.dmp.wa.gov.au/resource/feature/gswa/borehole/GSWADonnybrookDNB4");
        map.put("name", "GSWADonnybrookDNB4");
        map.put("dataserviceUrl", "http://geossdi.dmp.wa.gov.au/NVCLDataServices/");
        fakeNvclData.add(map);
        
        map = new HashMap<String, String>();
        map.put("id", "6feb401e-84a2-4541-8949-3681854d6b5");
        map.put("imageId", "c0a0f7ff-91d7-4ecc-a5dd-6d950ad1f51");
        map.put("wfsUri", "http://geossdi.dmp.wa.gov.au/resource/feature/gswa/borehole/msd9");
        map.put("name", "msd9");
        map.put("dataserviceUrl", "http://geossdi.dmp.wa.gov.au/NVCLDataServices/");
        fakeNvclData.add(map);
        
        map = new HashMap<String, String>();
        map.put("id", "4266aa64-b6ea-42e3-814e-40d1f83d383");
        map.put("imageId", "7ec27575-c3fa-4ccf-873a-0641a6cd275");
        map.put("wfsUri", "http://geossdi.dmp.wa.gov.au/resource/feature/gswa/borehole/DX00");
        map.put("name", "dx00");
        map.put("dataserviceUrl", "http://geossdi.dmp.wa.gov.au/NVCLDataServices/");
        fakeNvclData.add(map);
        
        map = new HashMap<String, String>();
        map.put("id", "6dd70215-fe38-457c-be42-3b165fd98c7");
        map.put("imageId", "fae8f90d-2015-4200-908a-b30da787f01");
        map.put("wfsUri", "http://nvclwebservices.vm.csiro.au/resource/feature/CSIRO/borehole/WTB5");
        map.put("name", "WTB5");
        map.put("dataserviceUrl", "http://nvclwebservices.vm.csiro.au/NVCLDataServices/");
        fakeNvclData.add(map);
        
        
    }
    
    

    public List<Borehole> getBoreholes(String featureId, Integer maxFeatures) throws PortalServiceException {
        HttpRequestBase request = null;
        List<Borehole> boreholes = new ArrayList<Borehole>();
        
        Random nvclRandomIdGenerator = new Random();
        
        try {
            request = this.generateWFSRequest(ENDPOINT, FEATURE_TYPE, featureId, null, maxFeatures, "http://www.opengis.net/gml/srs/epsg.xml#700001", ResultType.Results);

            String response = this.httpServiceCaller.getMethodResponseAsString(request);
            Document responseDoc = DOMUtil.buildDomFromString(response, true);

            OWSExceptionParser.checkForExceptionResponse(responseDoc);

            NamespaceContext nc = new BoreholeNamespace();

            XPathExpression exprBoreholes = DOMUtil.compileXPathExpr("wfs:FeatureCollection/gml:featureMembers/demo:boreholes", nc);
            NodeList boreholeNodes = (NodeList) exprBoreholes.evaluate(responseDoc, XPathConstants.NODESET);
            for (int i = 0; i < boreholeNodes.getLength(); i++) {
                Node boreholeNode = boreholeNodes.item(i);

                String name = ((Node) DOMUtil.compileXPathExpr("gml:name", nc).evaluate(boreholeNode, XPathConstants.NODE)).getTextContent();
                String depth = ((Node) DOMUtil.compileXPathExpr("demo:totaldepth", nc).evaluate(boreholeNode, XPathConstants.NODE)).getTextContent();
                String pointList = ((Node) DOMUtil.compileXPathExpr("demo:shape/gml:LineString/gml:posList", nc).evaluate(boreholeNode, XPathConstants.NODE)).getTextContent();

                String[] pointValues = pointList.split(" ");
                Point3D[] points = new Point3D[pointValues.length / 3];

                for (int j = 0; j < points.length; j++) {
                    points[j] = new Point3D(Double.parseDouble(pointValues[j * 3]),
                                            Double.parseDouble(pointValues[(j * 3) + 1]),
                                            Double.parseDouble(pointValues[(j * 3) + 2]));
                }

                
                Borehole bh = new Borehole(name, Double.parseDouble(depth), points);
                
                int fakeNvclIndex = nvclRandomIdGenerator.nextInt(fakeNvclData.size());
                bh.setNvclId(fakeNvclData.get(fakeNvclIndex).get("id"));
                bh.setNvclFeatureUri(fakeNvclData.get(fakeNvclIndex).get("wfsUri"));
                bh.setNvclName(fakeNvclData.get(fakeNvclIndex).get("name"));
                bh.setNvclDataUrl(fakeNvclData.get(fakeNvclIndex).get("dataserviceUrl"));
                bh.setNvclImageId(fakeNvclData.get(fakeNvclIndex).get("imageId"));
                boreholes.add(bh);
            }

            return boreholes;
        } catch (Exception ex) {
            throw new PortalServiceException(request, ex);
        }
    }

    public byte[] getImageStream(String serviceUrl, String boreholeId, String sampleNo) throws Exception{
    	 HttpGet method = new HttpGet();
         URIBuilder builder = new URIBuilder(serviceUrl + "Display_Tray_Thumb.html");
         builder.setParameter("logid", boreholeId); //The access token I am getting after the Login
         builder.setParameter("sampleno", sampleNo);
         method.setURI(builder.build());
         return this.httpServiceCaller.getMethodResponseAsBytes(method);
    }


    public File getImageFile(String boreholeId, String sampleNo) throws Exception{
   	    HttpGet method = new HttpGet();
        URIBuilder builder = new URIBuilder("http://nvclwebservices.vm.csiro.au/NVCLDataServices/Display_Tray_Thumb.html");
        builder.setParameter("logid", boreholeId); //The access token I am getting after the Login
        builder.setParameter("sampleno", sampleNo);
        method.setURI(builder.build());
        InputStream in = this.httpServiceCaller.getMethodResponseAsStream(method);
        File file = File.createTempFile("sprint_", "png");
        FileOutputStream out = new FileOutputStream(file);
        IOUtils.copy(in,out);
        out.flush();
        return file;
   }
    
}
