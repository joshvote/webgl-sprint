package au.csiro.portal.xml;

import org.auscope.portal.core.services.namespaces.IterableNamespace;

public class BoreholeNamespace extends IterableNamespace {
    public BoreholeNamespace() {
        map.put("ogc", "http://www.opengis.net/ogc");
        map.put("demo", "http://example.org/demo");
        map.put("test", "http://test.org");
        map.put("xlink", "http://www.w3.org/1999/xlink");
        map.put("wfs", "http://www.opengis.net/wfs");
        map.put("gsml", "urn:cgi:xmlns:CGI:GeoSciML:2.0");
        map.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        map.put("gml", "http://www.opengis.net/gml");
        map.put("ows", "http://www.opengis.net/ows");
        map.put("gsmlp", "http://xmlns.geosciml.org/geosciml-portrayal/2.0");
    }
}
