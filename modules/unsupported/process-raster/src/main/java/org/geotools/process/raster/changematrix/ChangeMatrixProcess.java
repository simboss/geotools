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

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;

import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.process.ProcessException;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.raster.RasterProcess;
import org.geotools.process.raster.changematrix.ChangeMatrixDescriptor.ChangeMatrix;
import org.geotools.test.TestData;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This is the endpoint that exposes through WPS the changeMatrix process
 * 
 * @author DamianoG
 * 
 */
@DescribeProcess(title = "ChangeMatrix", description = "The changeMatrix process")
public class ChangeMatrixProcess implements RasterProcess {
    /** 
     * @param classes representing the domain of the classes (Mandatory, not empty)
     * @param rasterT0 that is the reference Image (Mandatory)
     * @param rasterT1 rasterT1 that is the update situation (Mandatory)
     * @param roi that identifies the optional ROI (so that could be null)
     * @return
     */
    @DescribeResult(name = "changeMatrix", type=ChangeMatrixOutput.class, description = "the ChangeMatrix")
    public ChangeMatrixOutput execute(
            @DescribeParameter(name = "classes", collectionType=Integer.class, min=1, description = "The domain of the classes used in input rasters") Set<Integer> classes,
            @DescribeParameter(name = "rasterT0", min=1, description = "Input raster at Time 0") GridCoverage2D rasterT0,
            @DescribeParameter(name = "rasterT1", min=1, description = "Input raster at Time 1") GridCoverage2D rasterT1,
            @DescribeParameter(name = "ROI", min=1, description = "Region Of Interest") Geometry roi) throws ProcessException{
        
        
        // implementation...
        
        
        return getTestMap();
    }
    
    /**
     * @return an hardcoded changeMatrix
     */
    private static ChangeMatrixOutput getTestMap() {

        ChangeMatrixOutput s = new ChangeMatrixOutput();

        s.add(new ChangeMatrixElement(0, 0, 16002481));
        s.add(new ChangeMatrixElement(0, 35, 0));
        s.add(new ChangeMatrixElement(0, 1, 0));
        s.add(new ChangeMatrixElement(0, 36, 4));
        s.add(new ChangeMatrixElement(0, 37, 4));

        s.add(new ChangeMatrixElement(1, 0, 0));
        s.add(new ChangeMatrixElement(1, 35, 0));
        s.add(new ChangeMatrixElement(1, 1, 3192));
        s.add(new ChangeMatrixElement(1, 36, 15));
        s.add(new ChangeMatrixElement(1, 37, 0));
        
        return s;
    }
}
