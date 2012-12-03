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
    
    
}
