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

import java.awt.image.RenderedImage;
import java.util.Set;

import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.process.ProcessException;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.raster.RasterProcess;
import org.geotools.process.raster.changematrix.ChangeMatrixDescriptor.ChangeMatrix;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This is the endpoint that exposes through WPS the changeMatrix process
 * 
 * @author DamianoG
 * 
 */
@DescribeProcess(title = "ChangeMatrix", description = "The changeMatrix process")
public class ChangeMatrixProcess implements RasterProcess {

    public final static String CHANGE_MATRIX_DEBUG_MODE = "CHANGE_MATRIX_DEBUG_MODE";
    
    public final static boolean debugmode=Boolean.getBoolean(CHANGE_MATRIX_DEBUG_MODE);

    /**
     * @param classes representing the domain of the classes (Mandatory, not empty)
     * @param rasterT0 that is the reference Image (Mandatory)
     * @param rasterT1 rasterT1 that is the update situation (Mandatory)
     * @param roi that identifies the optional ROI (so that could be null)
     * @return
     */
    @DescribeResult(name = "changeMatrix", description = "the ChangeMatrix")
    public ChangeMatrixDTO execute(
            @DescribeParameter(name = "classes", collectionType = Integer.class, min = 1, description = "The domain of the classes used in input rasters") Set<Integer> classes,
            @DescribeParameter(name = "rasterT0", min = 1, description = "Input raster at Time 0") GridCoverage2D rasterT0,
            @DescribeParameter(name = "rasterT1", min = 1, description = "Input raster at Time 1") GridCoverage2D rasterT1,
            @DescribeParameter(name = "ROI", min = 0, description = "Region Of Interest") Geometry roi)

    throws ProcessException {

        // handle the debug mode
        if (debugmode) {
            return getTestMap();
        }

        final RenderedImage reference = rasterT0.getRenderedImage();
        final RenderedImage source = rasterT1.getRenderedImage();
        final ChangeMatrix cm = new ChangeMatrix(classes);

        // TODO Is really needed ???
        // final ImageLayout layout = new ImageLayout();
        // layout.setTileHeight(256).setTileWidth(256);
        // final RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT,
        // layout);

        final ParameterBlockJAI pbj = new ParameterBlockJAI("ChangeMatrix");
        pbj.addSource(reference);
        pbj.addSource(source);
        pbj.setParameter("result", cm);
        // TODO handle Region Of Interest
        // pbj.setParameter("ROI", roi);
        final RenderedOp result = JAI.create("ChangeMatrix", pbj, null);

        // TODO try to write the resulting image before disposing the sources, maybe better create a new geoserver layer
        // try {
        // ImageIO.write(result, "tiff", new File("XXXXX")/* TODO Where save the file??? */);
        // } catch (FileNotFoundException e) {
        // throw new ProcessException(e.getLoc	alizedMessage());
        // } catch (IOException e) {
        // throw new ProcessException(e.getLocalizedMessage());
        // }
        result.getTiles(); // this loads the result!
        result.dispose();
        // TODO Is needed to dispose the rendered image?
        ((RenderedOp) source).dispose();
        ((RenderedOp) reference).dispose();

        return new ChangeMatrixDTO(cm, classes);
    }



    /**
     * @return an hardcoded ChangeMatrixOutput usefull for testing
     */
    private static final ChangeMatrixDTO getTestMap() {

        ChangeMatrixDTO s = new ChangeMatrixDTO();

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
