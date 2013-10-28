package au.csiro.portal.web.controllers;

import java.awt.Menu;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.auscope.portal.core.server.PortalPropertyPlaceholderConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that handles all {@link Menu}-related requests,
 *
 * @author Jarek Sanders
 * @author Josh Vote
 */
@Controller
public class MenuController {

   protected final Log logger = LogFactory.getLog(getClass());

   private PortalPropertyPlaceholderConfigurer hostConfigurer;

   @Autowired
   public MenuController(PortalPropertyPlaceholderConfigurer hostConfigurer) {
       this.hostConfigurer = hostConfigurer;
   }

   /**
    * Handles all HTML page requests by mapping them to an appropriate view (and adding other details).
    * @param request
    * @param response
    * @return
    * @throws IOException
    */
   @RequestMapping("/*.html")
   public ModelAndView handleHtmlToView(HttpServletRequest request, HttpServletResponse response) throws IOException {
       //Detect whether this is a new session or not...
       HttpSession session = request.getSession();
       boolean isNewSession = session.getAttribute("existingSession") == null;
       session.setAttribute("existingSession", true);

       //Decode our request to get the view name we are actually requesting
       String requestUri = request.getRequestURI();
       String[] requestComponents = requestUri.split("/");
       if (requestComponents.length == 0) {
           logger.debug(String.format("request '%1$s' doesnt contain any extractable components", requestUri));
           response.sendError(HttpStatus.SC_NOT_FOUND, "Resource not found : " + requestUri);
           return null;
       }
       String requestedResource = requestComponents[requestComponents.length - 1];
       String resourceName = requestedResource.replace(".html", "");

       logger.trace(String.format("view name '%1$s' extracted from request '%2$s'", resourceName, requestUri));

       //Give the user the view they are actually requesting
       ModelAndView mav = new ModelAndView(resourceName);

       mav.addObject("isNewSession", isNewSession);

       return mav;
   }
}
