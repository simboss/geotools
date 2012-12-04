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

/**
 * This is the changeMatrix
 * @author DamianoG
 *
 */
public class ChangeMatrixOutput {
    
    /**
     * The implementation of the changeMatrix as a Set
     */
    private Set<ChangeMatrixElement> changeMatrix;

    /**
     * Init the changeMatrix as an empty TreeSet
     */
    public ChangeMatrixOutput() {
        super();
        changeMatrix = new TreeSet<ChangeMatrixElement>();
    }

    /**
     * Add an element to the changeMatrix
     * @param el
     */
    public void add(ChangeMatrixElement el){
        this.changeMatrix.add(el);
    }
    
    public Set<ChangeMatrixElement> getChangeMatrix(){
        return changeMatrix;
    }
    
}
