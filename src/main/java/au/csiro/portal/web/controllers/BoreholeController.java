package au.csiro.portal.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.auscope.portal.core.server.controllers.BasePortalController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import au.csiro.portal.models.Borehole;
import au.csiro.portal.models.Point3D;

@Controller
public class BoreholeController extends BasePortalController {
    @RequestMapping("getBoreholes.do")
    public ModelAndView getBoreholes(@RequestParam(required=false,value="maxFeatures") Integer maxFeatures,
            @RequestParam(required=false,value="featureId") String featureId) {
        
        List<Borehole> fakeData = new ArrayList<Borehole>();
        
        Point3D[] p1 = new Point3D[] {new Point3D(1.0, 1.0, 1.0), 
                new Point3D(5.0, 3.0, 2.0),
                new Point3D(5.0, 5.0, 3.0)};
        
        Point3D[] p2 = new Point3D[] {new Point3D(1.0, 1.0, 1.0), 
                new Point3D(2.0, 9.0, 18.0),
                new Point3D(15.0, 10.0, 16.0)};
        
        fakeData.add(new Borehole(-32.0, 160.0, p1));
        fakeData.add(new Borehole(-34.0, 175.0, p2));
        
        return generateJSONResponseMAV(true, fakeData, "");
    }
}
