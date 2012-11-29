/* 
 *  Copyright (c) 2009-2011, Michael Bedward. All rights reserved. 
 *   
 *  Redistribution and use in source and binary forms, with or without modification, 
 *  are permitted provided that the following conditions are met: 
 *   
 *  - Redistributions of source code must retain the above copyright notice, this  
 *    list of conditions and the following disclaimer. 
 *   
 *  - Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.   
 *   
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR 
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */   

package org.geotools.process.raster.changematrix;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;

import javax.media.jai.AreaOpImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;

import org.geotools.process.raster.changematrix.ChangeMatrixDescriptor.ChangeMatrix;
import org.jaitools.imageutils.iterator.SimpleIterator;
import org.jaitools.numeric.Statistic;


/**
 * An operator to calculate change in pixels between two classified images
 * relying on the {@link ChangeMatrix} element.
 *
 * @see ChangeMatrixDescriptor Description 
 * 
 * @author Simone Giannecchini, GeoSolutions SAS
 * @since 9.0
 */
public class ChangeMatrixOpImage extends AreaOpImage {

	
    private ROI roi;
	private int bandSource;
	private int bandReference;
	private RenderedImage referenceImage;
	private RenderedImage source;
	private ChangeMatrix result;
    
    /**
     * Creates a new instance.
     * 
     * @param source the source image
     * 
     * @param config configurable attributes of the image (see {@link AreaOpImage})
     * 
     * @param layout an optional ImageLayout object; if the layout specifies a SampleModel
     *     and / or ColorModel that are invalid for the requested statistics (e.g. wrong 
     *     number of bands) these will be overridden
     * 
     * @param bandSource the source image band to process
     * 
     * @param bandReference the reference image band to process
     * 
     * @param roi an optional {@code ROI} or {@code null}
     * 
     * @param result a {@link ChangeMatrix} object to compute and hold the results
     * 
     * @throws IllegalArgumentException if the ROI's bounds do not contain the entire
     *     source image
     * 
     * @see ChangeMatrixDescriptor
     * @see ChangeMatrix
     */
    public ChangeMatrixOpImage(RenderedImage source,
    		RenderedImage referenceImage,
            Map config,
            ImageLayout layout,
            int bandSource,
            int bandReference,
            ROI roi,
            ChangeMatrix result) {

        super(source,
              layout,
              config,
              true,
              BorderExtender.createInstance(BorderExtender.BORDER_ZERO),
              0,
              0,
              0,
              0);

        this.bandSource = bandSource;
        this.bandReference = bandReference;
        this.referenceImage=referenceImage;
        this.source=source;
        this.result=result;


        this.roi = roi;
        if (roi != null) {
            // check that the ROI contains the source image bounds
            Rectangle sourceBounds = new Rectangle(
                    source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());

            if (!roi.getBounds().contains(sourceBounds)) {
                throw new IllegalArgumentException("The bounds of the ROI must contain the source image");
            }
        }
        
        // where do we put the final elements?
    }
    

	@Override
	public Raster computeTile(int tileX, int tileY) {
		// compute bounds
		final int minx=PlanarImage.tileXToX(tileX, this.getTileGridXOffset(), this.getTileWidth());
		final int miny=PlanarImage.tileYToY(tileY, this.getTileGridYOffset(), this.getTileHeight());
		final Rectangle sourceBounds= new Rectangle(minx,miny,this.getTileWidth(),this.getTileHeight());

		// check roi 
		if(roi==null||roi.intersects(sourceBounds)){
//		
//		  // loop on tile and check
//		  final SimpleIterator  iteratorSource=new SimpleIterator(source,sourceBounds,Integer.valueOf(-1));//.create(source, sourceBounds);
//		  final SimpleIterator iteratorReference=new SimpleIterator(referenceImage,sourceBounds,Integer.valueOf(-1));//RectIterFactory.create(referenceImage, sourceBounds);
//		
//		  // loop
//		  do {
//			  // in ROI?
//			  if(roi!=null&&!roi.contains(iteratorSource.getPos())){
//				  continue;
//			  }
//			  int sampleSource = iteratorSource.getSample(bandSource).intValue();
//			  int sampleReference = iteratorReference.getSample(bandReference).intValue();
//			  result.registerPair(sampleReference, sampleSource);
//
//			} while (iteratorSource.next());
//		}
		
		  // loop on tile and check
		final RectIter  iteratorSource=RectIterFactory.create(source, sourceBounds);
		final RectIter iteratorReference=RectIterFactory.create(referenceImage, sourceBounds);
				
		  // loop
		  int row=0;
		  int col=0;
		  while(!iteratorSource.finishedLines()){
			  while(!iteratorSource.finishedPixels()){
				  
				  // in ROI?
				  if(roi!=null&&!roi.contains(col, row)){
					  continue;
				  }

				  // extract value
				  try{
				  int sampleSource=iteratorSource.getSample(bandSource);
				  int sampleReference=iteratorReference.getSample(bandReference);

				  result.registerPair(sampleReference, sampleSource);
				  }catch(Exception e){
					  System.out.println(col+","+row);
				  }

				
				  // progress pixels
				  iteratorSource.nextPixel();
				  iteratorReference.nextPixel();
				  col++;
			  }
			  // progress lines
			  iteratorSource.nextLine();
			  iteratorSource.startPixels();
			  iteratorReference.nextLine();
		  	  iteratorReference.startPixels();
		  	  col=0;
		  	  row++;
		  }
		
		}
		// return source image
		return source.getData(sourceBounds);
	}
}

