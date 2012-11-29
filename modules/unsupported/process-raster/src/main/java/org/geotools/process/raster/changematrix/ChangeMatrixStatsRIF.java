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

import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;

import javax.media.jai.ImageLayout;
import javax.media.jai.ROI;

import org.geotools.process.raster.changematrix.ChangeMatrixDescriptor.ChangeMatrix;

import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.util.ImageUtil;

/**
 * The image factory for the {@link ChangeMatrixOpImage} operation.
 *
 * @author Simone Giannecchini, GeoSolutions SAS
 * @since 9.0
 */
public class ChangeMatrixStatsRIF implements RenderedImageFactory {

    /** Constructor */
    public ChangeMatrixStatsRIF() {
    }

    /**
     * Create a new instance of {@link ChangeMatrixOpImage} in the rendered layer.
     *
     * @param paramBlock specifies the source image and the  parameters
     *
     * @param renderHints mostly useless with this image
     */
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {

        RenderedImage reference = paramBlock.getRenderedSource(0);
        if(reference.getSampleModel().getNumBands()>1){
        	throw new IllegalArgumentException("Unable to process image with more than one band (source[0])");
        }
        final int referenceDataType=reference.getSampleModel().getDataType();
        if(referenceDataType!=DataBuffer.TYPE_BYTE&&
        		referenceDataType!=DataBuffer.TYPE_INT&&
        		referenceDataType!=DataBuffer.TYPE_SHORT&&
        		referenceDataType!=DataBuffer.TYPE_USHORT){
        	throw new IllegalArgumentException("Unable to process image (source[0]) as it has a non integer data type");
        }
        RenderedImage now = paramBlock.getRenderedSource(1);
        if(now.getSampleModel().getNumBands()>1){
        	throw new IllegalArgumentException("Unable to process image with more than one band (source[0])");
        }
        final int nowDataType=now.getSampleModel().getDataType();
        if(nowDataType!=DataBuffer.TYPE_BYTE&&
        		nowDataType!=DataBuffer.TYPE_INT&&
        		nowDataType!=DataBuffer.TYPE_SHORT&&
        		nowDataType!=DataBuffer.TYPE_USHORT){
        	throw new IllegalArgumentException("Unable to process image (source[1]) as it has a non integer data type");
        }        
        // same data type
        if(referenceDataType!=nowDataType){
        	throw new IllegalArgumentException("Unable to process images with different data type");
        }
        
        // same size
        if(now.getWidth()!=reference.getWidth()||now.getHeight()!=reference.getHeight()){
        	throw new IllegalArgumentException("Unable to process images with different raster dimensions");
        }
        
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        if (layout == null) layout = new ImageLayout();

        
        // result
        final ChangeMatrix result= (ChangeMatrix) paramBlock.getObjectParameter(ChangeMatrixDescriptor.RESULT_ARG_INDEX);

        ROI roi = (ROI) paramBlock.getObjectParameter(ChangeMatrixDescriptor.ROI_ARG_INDEX);


        return new ChangeMatrixOpImage(
        		reference,
        		now,
                renderHints,
                layout,
                roi,
                result);
    }
}

