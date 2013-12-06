package au.csiro.portal.web.controllers;

import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.jai.Interpolation;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JPEGDescriptor;
import javax.media.jai.operator.LookupDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.RotateDescriptor;
import javax.media.jai.operator.ScaleDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import javax.servlet.http.HttpServletResponse;

import org.auscope.portal.core.server.controllers.BasePortalController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import au.csiro.portal.models.Borehole;
import au.csiro.portal.services.Borehole3DService;

import org.apache.commons.io.IOUtils;

import com.sun.media.jai.codec.ByteArraySeekableStream;

@Controller
public class ImageController extends BasePortalController {
	private Borehole3DService service;
    
    @Autowired
    public ImageController(Borehole3DService service) {
        super();
        this.service = service;
    }

    @RequestMapping("getBoreholesImage.do")
    public void getBoreholesImage(
    		@RequestParam(required=true,value="logId") String logId,
    		@RequestParam(required=true,value="serviceUrl") String serviceUrl,
            @RequestParam(required=true,value="depth") int depth,
            HttpServletResponse response) {
    	
        int height = 0, width = 0;
        Vector renderedOps = new Vector();
        RenderedOp op; 
    	
        try {
        	System.out.println("logId=" + logId + ",depth=" + depth);
        	
        	for (int i=0; i<depth; i++) {
        		byte[] b = service.getImageStream(serviceUrl, logId, String.valueOf(i));
        		ByteArraySeekableStream bas = new ByteArraySeekableStream(b);
        		op = JPEGDescriptor.create(bas, null);
        		
        		op = RotateDescriptor.create(op, 0F, 0F, (float) -(Math.PI / 2), Interpolation.getInstance(Interpolation.INTERP_BICUBIC), null, null);
        		
        		if (i == 0){
                    width = op.getWidth();
                    height = op.getHeight();
                } else {
                    // TRANSLATE
                    // Translate source images to correct places in the mosaic.
                    op = TranslateDescriptor.create(op,(float)(width * i),(float)(height * 0),null,null);
                }
        		
        		op = ScaleDescriptor.create(op, 0.25F, 0.25F, 0F, 0F, Interpolation.getInstance(Interpolation.INTERP_BICUBIC), null);
        		
        		
        		renderedOps.add(convert(op));
        	}
       
        
        	
            RenderedOp finalImage = MosaicDescriptor.create(
            		(RenderedImage[]) renderedOps.toArray(new RenderedOp[renderedOps.size()]),
                    MosaicDescriptor.MOSAIC_TYPE_OVERLAY,null,null,null,null,null
                    );
            response.setContentType("image/png");
            ImageIO.write(finalImage, "png", response.getOutputStream());      	
   
        	
            //InputStream is = new FileInputStream("out.png");
            //IOUtils.copy(is, response.getOutputStream());
            //response.flushBuffer();
        } catch (Exception ex) {
            log.error("Error fetching boreholes image", ex);
        }
    }
    
    private RenderedOp convert(RenderedOp image){
        // If the source image is colormapped, convert it to 3-band RGB.
        if(image.getColorModel() instanceof IndexColorModel) {
            // Retrieve the IndexColorModel
            IndexColorModel icm = (IndexColorModel)image.getColorModel();
            // Cache the number of elements in each band of the colormap.
            int mapSize = icm.getMapSize();
            // Allocate an array for the lookup table data.
            byte[][] lutData = new byte[3][mapSize];
            // Load the lookup table data from the IndexColorModel.
            icm.getReds(lutData[0]);
            icm.getGreens(lutData[1]);
            icm.getBlues(lutData[2]);
            // Create the lookup table object.
            LookupTableJAI lut = new LookupTableJAI(lutData);
            // Replace the original image with the 3-band RGB image.
            image = LookupDescriptor.create(image,lut,null);
        }
        return image;
    }    
}