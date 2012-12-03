/*
 *  GeoBatch - Open Source geospatial batch processing system
 *  https://github.com/nfms4redd/nfms-geobatch
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
