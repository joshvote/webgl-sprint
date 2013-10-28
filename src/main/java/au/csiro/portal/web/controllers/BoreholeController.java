package au.csiro.portal.web.controllers;

import java.util.List;

import org.auscope.portal.core.server.controllers.BasePortalController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import au.csiro.portal.models.Borehole;
import au.csiro.portal.services.Borehole3DService;

@Controller
public class BoreholeController extends BasePortalController {
    
    private Borehole3DService service;
    
    @Autowired
    public BoreholeController(Borehole3DService service) {
        super();
        this.service = service;
    }

    @RequestMapping("getBoreholes.do")
    public ModelAndView getBoreholes(@RequestParam(required=false,value="maxFeatures") Integer maxFeatures,
            @RequestParam(required=false,value="featureId") String featureId) {
        try {
            List<Borehole> data = service.getBoreholes(featureId, maxFeatures); 
            return generateJSONResponseMAV(true, data, "");
        } catch (Exception ex) {
            log.error("Error fetching boreholes", ex);
            return generateJSONResponseMAV(false, null, ex.getMessage());
        }
    }
}
