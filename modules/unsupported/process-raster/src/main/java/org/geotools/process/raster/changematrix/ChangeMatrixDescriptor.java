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

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ROI;
import javax.media.jai.registry.RenderedRegistryMode;

import org.jaitools.numeric.Statistic;


/**
 * An {@code OperationDescriptor} for the "ChangeMatrix" operation.
 * <p>
 * This operation will work on two images with samples that are integer values (classes) and will
 * compute a {@link ChangeMatrix} that would tell us how many pixel changed classes or not.
 * <p>
 * The {@link ChangeMatrix} class must be initialised with the integer classes for which we are interested
 * in registering the changes. The reference image parameter represent the situation at time T0 and the source
 * image for the operation represents the situation at time T1 > T0 (usually present time).
 * <p>
 * For each pair of classes <Ci,Cj> the change  matrix record the number of pixels that went from Ci in the reference image
 * to Cj in the source image. If Ci and Cj are the same class that evaluates how many pixel of class Ci remaine in the same 
 * class.
 * <p>
 * The operation support providing a ROI.
 *
 * <p>
 * <b>Summary of parameters:</b>
 * <table border="1", cellpadding="3">
 * <tr>
 * <th>Name</th>
 * <th>Class</th>
 * <th>Default</th>
 * <th>Description</th>
 * </tr>
 * 
 * <tr>
 * <td>bandSource</td>
 * <td>Integer</td>
 * <td>0</td>
 * <td>Integer that tells us which band to use for the source RenderedImage</td>
 * </tr>
 * 
 * <tr>
 * <td>bandReference</td>
 * <td>Integer</td>
 * <td>0</td>
 * <td>Integer  that tells us which band to use for the source RenderedImage</td>
 * </tr>
 * 
 * 
 * <tr>
 * <td>roi</td>
 * <td>ROI</td>
 * <td>null</td>
 * <td>An optional ROI defining the area to process</td>
 * </tr>
 * 
 * <tr>
 * <td>referenceImage</td>
 * <td>RenderedImage</td>
 * <td>null</td>
 * <td>The reference image for the computation of changes</td>
 * </tr>
 * 
 * <tr>
 * <td>result</td>
 * <td>Changematrix</td>
 * <td>null</td>
 * <td>The {@link ChangeMatrix} object to collect the changes.</td>
 * </tr>
 * </table>
 * 
 * @author Simone Giannecchini, GeoSolutions
 * @since 9.0
 */
public class ChangeMatrixDescriptor extends OperationDescriptorImpl {
	
	/**
	 * This class is used to capture the changes in classes between two images.
	 * 
	 * @author Simone Giannecchini, GeoSolutions SAS
	 *
	 */
	public static class ChangeMatrix {

		/**Used to signal non existing values in the results.*/
		public static final int NO_VALUE=-1;
		
		/**Mappings between real-world classes and indexes in the sparse matrix*/
		private Map<Integer,Integer> classesMappings= new HashMap<Integer, Integer>();
		
		/**Sparse matrix to hold the results*/
		private List<AtomicInteger> matrix;

		/**Number of classes.*/
		private int classesNumber;
		
		/**
		 * Constructor
		 * 
		 * @param classes the {@link Set} of {@link Integer} classes to register changes for
		 */
		public ChangeMatrix(Set<Integer> classes){
			//checks
			if(classes==null){
				throw new IllegalArgumentException("The provided classes set is null");
			}
			if(classes.isEmpty()){
				throw new IllegalArgumentException("The provided classes set is empty");
			}	
			
			// build the mappings and the matrix to hold the result
			
			// matrix as list
			classesNumber= classes.size();
			matrix = new ArrayList<AtomicInteger>(classesNumber*classesNumber);
			for(int i=classesNumber*classesNumber-1;i>=0;i--){
				matrix.add(new AtomicInteger(0));
			}
			
			//mappings
			int k=0;
	        for(Integer clazz:classes){
	        		classesMappings.put(clazz,k++);
	        }
			
		}
		
		/**
		 * Register the change, if the two values fall
		 * @param reference
		 * @param newSample
		 */
		public void registerPair(int reference, int newSample){
			Integer row=classesMappings.get(reference);
			Integer col=classesMappings.get(newSample);
			if(row!=null&&col!=null){
				matrix.get(col+row*classesNumber).incrementAndGet();
			}
		}
		
		public int retrievePairOccurrences(int reference, int newSample){
			Integer row=classesMappings.get(reference);
			Integer col=classesMappings.get(newSample);
			if(row!=null&&col!=null){
				return matrix.get(col+row*classesNumber).get();
			} else {
				return NO_VALUE;
			}
		}
		
		public boolean isClassUsed(int clazz){
			Integer mapping=classesMappings.get(clazz);
			if(mapping!=null){
				return true;
			} else {
				return false;
			}
		}
				
		public Map<Integer, Integer> getClassesMappings() {
			return new HashMap<Integer, Integer>(classesMappings);
		}

		public int getClassesNumber() {
			return classesNumber;
		}
	}

    /**
	 * 
	 */
	private static final long serialVersionUID = -6996896157854316840L;
	
	public static final int BAND_SOURCE_ARG_INDEX = 0;
	public static final int BAND_REFERENCE_ARG_INDEX = 1;
	public static final int ROI_ARG_INDEX = 2;
	public static final int REFIMAGE_ARG_INDEX = 3;
	public static final int RESULT_ARG_INDEX = 4;

	public static final String[] PARAM_NAMES =
        {"bandSource",
         "bandReference",
         "roi",
         "referenceImage",
         "result"
        };

    private static final Class[] PARAM_CLASSES =
        {Integer.class,
         Integer.class,
         javax.media.jai.ROI.class,
         RenderedImage.class,
         ChangeMatrix.class
        };

    private static final Object[] PARAM_DEFAULTS =
        {Integer.valueOf(0),
         Integer.valueOf(0),         
         (ROI) null,
         NO_PARAMETER_DEFAULT,
         NO_PARAMETER_DEFAULT};

    /** Constructor. */
    public ChangeMatrixDescriptor() {
        super(new String[][]{
                    {"GlobalName", "ChangeMatrix"},
                    {"LocalName", "ChangeMatrix"},
                    {"Vendor", "org.jaitools.media.jai"},
                    {"Description", "Calculate change matrix between two images"},
                    {"DocURL", "http://www.geotools.org"},
                    {"Version", "1.0.0"},
                    {"arg0Desc", "band (Integer, default 0) - the source image band to process"},
                    {"arg1Desc", "band (Integer, default 0) - the reference image band to process"},
                    {"arg2Desc", "roi (default null) - an optional ROI object for source and/or" +
                             "destination masking"},
                    {"arg3Desc", "RenderedImage (RenderedImage) -" +
                             "the reference image"},
                    {"arg4Desc", "result (ChangeMatrix) -" +
                             "a sparse matrix as a Map holding the count of change pixels"}
                },

                new String[]{RenderedRegistryMode.MODE_NAME},   // supported modes
                
                1,                                              // number of sources
                
                PARAM_NAMES,
                PARAM_CLASSES,
                PARAM_DEFAULTS,
                
                null                                            // valid values (none defined)
                );
    }

    /**
     * Validates supplied parameters.
     * 
     * @param modeName the rendering mode
     * @param pb the parameter block
     * @param msg a {@code StringBuffer} to receive error messages
     * 
     * @return {@code true} if parameters are valid; {@code false} otherwise
     */
	@Override
    public boolean validateArguments(String modeName, ParameterBlock pb, StringBuffer msg) {
        if (!super.validateArguments(modeName, pb, msg)) {
            return false;
        }
        // Reference Image
        final Object o=pb.getObjectParameter(REFIMAGE_ARG_INDEX);
        RenderedImage referenceImage=null;
        if(o!=null&&o instanceof RenderedImage){
        	referenceImage=(RenderedImage) o;
        } else {
        	msg.append(" null reference image");
        	return false;
        }
        
        // bands
        int band0 = pb.getIntParameter(BAND_SOURCE_ARG_INDEX);
        int band1 = pb.getIntParameter(BAND_REFERENCE_ARG_INDEX);
        final int numBands=referenceImage.getSampleModel().getNumBands();
        if (band0 < 0 || band0 >= numBands) {
            msg.append("band arg out of bounds for reference image: ").append(band0);
            return false;
        }
        if (band1 < 0 || band1 >= numBands) {
            msg.append("band arg out of bounds for reference image: ").append(band0);
            return false;
        }
                
        // result
        // TODO improve checks on type, etc...
        final Object o2=pb.getObjectParameter(RESULT_ARG_INDEX);
        if(o2!=null&& o2 instanceof ChangeMatrix){
        	return true;
        } else {
            msg.append("result is null or not of type ChangeMatrix");
        	return false;
        }     
           
    }


}

