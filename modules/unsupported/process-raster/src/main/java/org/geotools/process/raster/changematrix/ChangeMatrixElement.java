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

/**
 * This Bean represent a single element of a ChangeMatrix
 * @author DamianoG
 *
 */
public class ChangeMatrixElement implements Comparable<ChangeMatrixElement>{

    /**
     * The Pixel class type, Image at time 0
     */
    private int ref;
    /**
     * The Pixel class type, Image at time 1 
     */
    private int now;
    /**
     * The number of pixel that has been changed from class ref to class now
     */
    private int pixels;
    
    /**
     * @param ref
     * @param now
     * @param pixels
     */
    public ChangeMatrixElement(int ref, int now, int pixels) {
        this.ref = ref;
        this.now = now;
        this.pixels = pixels;
    }

    /**
     * @return the ref
     */
    public int getRef() {
        return ref;
    }

    /**
     * @return the now
     */
    public int getNow() {
        return now;
    }

    /**
     * @return the pixels
     */
    public int getPixels() {
        return pixels;
    }

    /**
     * @param ref the ref to set
     */
    public void setRef(int ref) {
        this.ref = ref;
    }

    /**
     * @param now the now to set
     */
    public void setNow(int now) {
        this.now = now;
    }

    /**
     * @param pixels the pixels to set
     */
    public void setPixels(int pixels) {
        this.pixels = pixels;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ChangeMatrixElement o) {
        if(o.getRef()==this.getRef()&&o.getNow()==this.getNow()&&o.getPixels()==this.getPixels()){
            return 0;
        }
        return 1;
    }    
}
