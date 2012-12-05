/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008-2011 TOPP - www.openplans.org.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.process.raster.changematrix;

import java.util.Set;
import java.util.TreeSet;

import org.geotools.process.raster.changematrix.ChangeMatrixDescriptor.ChangeMatrix;

/**
 * This is the changeMatrix
 * @author Damiano Giampaoli, GeoSolutions SAS
 *
 */
public class ChangeMatrixDTO {
	
	/**
     * The implementation of the changeMatrix as a Set
     */
    private final Set<ChangeMatrixElement> changeMatrix = new TreeSet<ChangeMatrixElement>();

    /**
     * Init the changeMatrix as an empty TreeSet
     * @param classes 
     * @param cm 
     */
    public ChangeMatrixDTO(ChangeMatrix cm, Set<Integer> classes) {
        
        
        for (Integer elRef : classes) {
            for (Integer elNow : classes) {
                if (!elRef.equals(elRef)) {
                    ChangeMatrixElement cme = new ChangeMatrixElement(elRef, elNow,cm.retrievePairOccurrences(elRef, elNow));
                    add(cme);
                }
            }
        }     
    }

    /** 
     * Default constructor.
     * 
     */
    public ChangeMatrixDTO() {
		
	}

	/**
     * Add an element to the changeMatrix
     * @param el
     */
    public void add(ChangeMatrixElement el){
        this.changeMatrix.add(el);
    }
    
    /**
     * 
     * @return
     */
    public Set<ChangeMatrixElement> getChangeMatrix(){
        return changeMatrix;
    }
    
}
