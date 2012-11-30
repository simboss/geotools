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

import java.awt.image.renderable.RenderedImageFactory;

import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationRegistry;
import javax.media.jai.OperationRegistrySpi;
import javax.media.jai.registry.RenderedRegistryMode;

/**
 * OperationRegistrySpi implementation to register the "ChangeMatrix" operation
 * and its associated image factories.
 * 
 * @author Simone Giannecchini, GeoSolutions
 * @since 9.0
 */
public class ChangeMatrixSpi implements OperationRegistrySpi {

/** The name of the product to which these operations belong. */
private String productName = "org.jaitools.media.jai";

/** Default constructor. */
public ChangeMatrixSpi() {
}

/**
 * Registers the ChangeMatrix operation and its associated image factories
 * across all supported operation modes.
 * 
 * @param registry The registry with which to register the operations and their
 *        factories.
 */
public void updateRegistry(OperationRegistry registry) {
    OperationDescriptor op = new ChangeMatrixDescriptor();
    registry.registerDescriptor(op);
    String descName = op.getName();

    RenderedImageFactory rif = new ChangeMatrixStatsRIF();

    registry.registerFactory(RenderedRegistryMode.MODE_NAME, descName,
            productName, rif);

}
}
