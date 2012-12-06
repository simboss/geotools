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

import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ROI;
import javax.media.jai.registry.RenderedRegistryMode;

/**
 * An {@code OperationDescriptor} for the "ChangeMatrix" operation.
 * <p>
 * This operation will work on two images with samples that are integer values
 * (classes) and will compute a {@link ChangeMatrix} that would tell us how many
 * pixel changed classes or not.
 * <p>
 * The {@link ChangeMatrix} class must be initialised with the integer classes
 * for which we are interested in registering the changes. The reference image
 * parameter represent the situation at time T0 and the source image for the
 * operation represents the situation at time T1 > T0 (usually present time).
 * <p>
 * For each pair of classes <Ci,Cj> the change matrix record the number of
 * pixels that went from Ci in the reference image to Cj in the source image. If
 * Ci and Cj are the same class that evaluates how many pixel of class Ci
 * remaine in the same class.
 * <p>
 * The operation support providing a ROI.
 * <p>
 * <b>Summary of parameters:</b>
 * <table border="1", cellpadding="3">
 * <tr>
 * <th>Name</th>
 * <th>Class</th>
 * <th>Default</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>bandSource</td>
 * <td>Integer</td>
 * <td>0</td>
 * <td>Integer that tells us which band to use for the source RenderedImage</td>
 * </tr>
 * <tr>
 * <td>bandReference</td>
 * <td>Integer</td>
 * <td>0</td>
 * <td>Integer that tells us which band to use for the source RenderedImage</td>
 * </tr>
 * <tr>
 * <td>roi</td>
 * <td>ROI</td>
 * <td>null</td>
 * <td>An optional ROI defining the area to process</td>
 * </tr>
 * <tr>
 * <td>referenceImage</td>
 * <td>RenderedImage</td>
 * <td>null</td>
 * <td>The reference image for the computation of changes</td>
 * </tr>
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
 */
public static class ChangeMatrix {

/**
 * This one is used to signal this class that we should not update the internal
 * counters any longer.
 */
private boolean frozen = false;

/** Used to signal non existing values in the results. */
public static final long NO_VALUE = -1;

/** Mappings between real-world classes and indexes in the sparse matrix */
private Map<Integer, Integer> classesMappings = new HashMap<Integer, Integer>();

/** Sparse matrix to hold the results */
private List<AtomicLong> matrix;

/** Number of classes. */
private int classesNumber;

/**
 * Constructor
 * 
 * @param classes the {@link Set} of {@link Integer} classes to register changes
 *        for
 */
public ChangeMatrix(Set<Integer> classes) {
    // checks
    if (classes == null) {
        throw new IllegalArgumentException("The provided classes set is null");
    }
    if (classes.isEmpty()) {
        throw new IllegalArgumentException("The provided classes set is empty");
    }

    // build the mappings and the matrix to hold the result

    // matrix as list
    classesNumber = classes.size();
    matrix = new ArrayList<AtomicLong>(classesNumber * classesNumber);
    for (int i = classesNumber * classesNumber - 1; i >= 0; i--) {
        matrix.add(new AtomicLong(0));
    }

    // mappings
    int k = 0;
    for (Integer clazz : classes) {
        classesMappings.put(clazz, k++);
    }

}

/**
 * Register the change, if the two classes are within those we were asked to
 * compute changes for.
 * 
 * @param reference, the initial class
 * @param newSample, the landing class
 */
public void registerPair(int reference, int now) {
    if (frozen) {
        return;
    }
    Integer row = classesMappings.get(reference);
    Integer col = classesMappings.get(now);
    if (row != null && col != null) {
        matrix.get(col + row * classesNumber).incrementAndGet();
    }

}

/**
 * Retrieves the change value for a certain order pair of classes.
 * <p>
 * In case one of both classes weren't part of the set of classes for which we
 * were asked to compute changes, {@link #NO_VALUE} is returned.
 * 
 * @param reference, the value for the reference image
 * @param now, the value for the second image
 * @return a <code>long</code> that holds the number of pixels that changed
 *         class as per the provided ones, or {@link #NO_VALUE} in case one of
 *         the two, or both, classes weren't in the initial set of classes to
 *         register changes for.
 */
public long retrievePairOccurrences(int reference, int now) {
    Integer row = classesMappings.get(reference);
    Integer col = classesMappings.get(now);
    if (row != null && col != null) {
        return matrix.get(col + row * classesNumber).get();
    } else {
        return NO_VALUE;
    }
}

/**
 * Retrieves the number of classes we have been asked to register changes for
 * 
 * @return int, the number of classes we have been asked to register changes for
 */
public int getClassesNumber() {
    return classesNumber;
}

/**
 * This is used to indicate to the underlying code to stop registering values as
 * the computation has been performed already.
 */
public void freeze() {
    frozen = true;
}
}

/**
	 * 
	 */
private static final long serialVersionUID = -6996896157854316840L;

public static final int ROI_ARG_INDEX = 0;

public static final int RESULT_ARG_INDEX = 1;

public static final String[] PARAM_NAMES = { "roi", "result" };

private static final Class[] PARAM_CLASSES = { javax.media.jai.ROI.class,
        ChangeMatrix.class };

private static final Object[] PARAM_DEFAULTS = { (ROI) null,
        NO_PARAMETER_DEFAULT };

/** Constructor. */
public ChangeMatrixDescriptor() {
    super(
            new String[][] {
                    { "GlobalName", "ChangeMatrix" },
                    { "LocalName", "ChangeMatrix" },
                    { "Vendor", "org.jaitools.media.jai" },
                    { "Description",
                            "Calculate change matrix between two images" },
                    { "DocURL", "http://www.geotools.org" },
                    { "Version", "1.0.0" },
                    {
                            "arg0Desc",
                            "roi (default null) - an optional ROI object for source and/or"
                                    + "destination masking" },
                    {
                            "arg1Desc",
                            "result (ChangeMatrix) -"
                                    + "a sparse matrix as a Map holding the count of change pixels" } },

            new String[] { RenderedRegistryMode.MODE_NAME }, // supported modes

            1, // number of sources

            PARAM_NAMES, PARAM_CLASSES, PARAM_DEFAULTS,

            null // valid values (none defined)
    );
}

/**
 * Validates supplied parameters.
 * 
 * @param modeName the rendering mode
 * @param pb the parameter block
 * @param msg a {@code StringBuffer} to receive error messages
 * @return {@code true} if parameters are valid; {@code false} otherwise
 */
@Override
public boolean validateArguments(String modeName, ParameterBlock pb,
        StringBuffer msg) {
    if (!super.validateArguments(modeName, pb, msg)) {
        return false;
    }

    // result
    // TODO improve checks on type, etc...
    final Object o2 = pb.getObjectParameter(RESULT_ARG_INDEX);
    if (o2 != null && o2 instanceof ChangeMatrix) {
        return true;
    } else {
        msg.append("result is null or not of type ChangeMatrix");
        return false;
    }

}

}
