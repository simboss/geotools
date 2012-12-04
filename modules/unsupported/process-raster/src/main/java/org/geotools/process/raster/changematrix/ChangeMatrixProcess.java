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

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.process.ProcessException;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.raster.RasterProcess;

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
    @DescribeResult(name = "changeMatrix", description = "the ChangeMatrix")
    public ChangeMatrixOutput execute(
            @DescribeParameter(name = "classes", collectionType = Integer.class, min = 1, description = "The domain of the classes used in input rasters") Set<Integer> classes,
            @DescribeParameter(name = "rasterT0", min = 1, description = "Input raster at Time 0") GridCoverage2D rasterT0,
            @DescribeParameter(name = "rasterT1", min = 1, description = "Input raster at Time 1") GridCoverage2D rasterT1,
            @DescribeParameter(name = "ROI", min=0, description = "Region Of Interest") Geometry roi)
            
            throws ProcessException {

//        RenderedImage reference = rasterT0.getRenderedImage();
//        RenderedImage source = rasterT1.getRenderedImage();
//
//        ChangeMatrix cm = new ChangeMatrix(classes);
//
//        // TODO Is really needed ???
//        // final ImageLayout layout = new ImageLayout();
//        // layout.setTileHeight(256).setTileWidth(256);
//        // final RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT,
//        // layout);
//
//        final ParameterBlockJAI pbj = new ParameterBlockJAI("ChangeMatrix");
//        pbj.addSource(reference);
//        pbj.addSource(source);
//        pbj.setParameter("result", cm);
//        final RenderedOp result = JAI.create("ChangeMatrix", pbj, null);
//
//        // try to write the resulting image before disposing the sources
//        try {
//            ImageIO.write(result, "tiff", new File(TestData.file(this, "."), "result.tif")/* Where save the file??? */);
//        } catch (FileNotFoundException e) {
//            throw new ProcessException(e.getLocalizedMessage());
//        } catch (IOException e) {
//            throw new ProcessException(e.getLocalizedMessage());
//        }
//
//        result.dispose();
//        ((RenderedOp) source).dispose();
//        ((RenderedOp) reference).dispose();

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

        s.add(new ChangeMatrixElement(35, 0, 0));
        s.add(new ChangeMatrixElement(35, 35, 7546));
        s.add(new ChangeMatrixElement(35, 1, 0));
        s.add(new ChangeMatrixElement(35, 36, 0));
        s.add(new ChangeMatrixElement(35, 37, 16));
        
        s.add(new ChangeMatrixElement(36, 0, 166));
        s.add(new ChangeMatrixElement(36, 35, 36));
        s.add(new ChangeMatrixElement(36, 1, 117));
        s.add(new ChangeMatrixElement(36, 36, 1273887));
        s.add(new ChangeMatrixElement(36, 37, 11976));
        
        s.add(new ChangeMatrixElement(37, 0, 274));
        s.add(new ChangeMatrixElement(37, 35, 16));
        s.add(new ChangeMatrixElement(37, 1, 16));
        s.add(new ChangeMatrixElement(37, 36, 28710));
        s.add(new ChangeMatrixElement(37, 37, 346154));
        
        return s;
    }
}
