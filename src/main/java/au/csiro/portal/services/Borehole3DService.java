package au.csiro.portal.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    public Borehole3DService(HttpServiceCaller httpServiceCaller,
            WFSGetFeatureMethodMaker wfsMethodMaker) {
        super(httpServiceCaller, wfsMethodMaker);
    }

    public List<Borehole> getBoreholes(String featureId, Integer maxFeatures) throws PortalServiceException {
        HttpRequestBase request = null;
        List<Borehole> boreholes = new ArrayList<Borehole>();
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

                boreholes.add(new Borehole(name, Double.parseDouble(depth), points));
            }

            return boreholes;
        } catch (Exception ex) {
            throw new PortalServiceException(request, ex);
        }
    }

    public InputStream getImageStream(String boreholeId) throws Exception{

    	 HttpGet method = new HttpGet();
         URIBuilder builder = new URIBuilder("http://nvclwebservices.vm.csiro.au/NVCLDataServices/Display_Tray_Thumb.html");
         builder.setParameter("logid", boreholeId); //The access token I am getting after the Login
         builder.setParameter("sampleno", "1");
         method.setURI(builder.build());
         return this.httpServiceCaller.getMethodResponseAsStream(method);

    }


    public File getImageFile(String boreholeId) throws Exception{

   	 HttpGet method = new HttpGet();
        URIBuilder builder = new URIBuilder("http://nvclwebservices.vm.csiro.au/NVCLDataServices/Display_Tray_Thumb.html");
        builder.setParameter("logid", boreholeId); //The access token I am getting after the Login
        builder.setParameter("sampleno", "1");
        method.setURI(builder.build());
        InputStream in = this.httpServiceCaller.getMethodResponseAsStream(method);
        File file = File.createTempFile("sprint", "png");
        FileOutputStream out = new FileOutputStream(file);
        IOUtils.copy(in,out);

        out.flush();

        return file;

   }
}
