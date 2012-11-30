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

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;

import javax.media.jai.AreaOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.ROI;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

import org.geotools.process.raster.changematrix.ChangeMatrixDescriptor.ChangeMatrix;

/**
 * An operator to calculate change in pixels between two classified images
 * relying on the {@link ChangeMatrix} element.
 * 
 * @see ChangeMatrixDescriptor Description
 * @author Simone Giannecchini, GeoSolutions SAS
 * @since 9.0
 */
public class ChangeMatrixOpImage extends PointOpImage {

private ROI roi;

private ChangeMatrix result;

/**
 * Creates a new instance.
 * 
 * @param now the source image
 * @param config configurable attributes of the image (see {@link AreaOpImage})
 * @param layout an optional ImageLayout object; if the layout specifies a
 *        SampleModel and / or ColorModel that are invalid for the requested
 *        statistics (e.g. wrong number of bands) these will be overridden
 * @param bandSource the source image band to process
 * @param bandReference the reference image band to process
 * @param roi an optional {@code ROI} or {@code null}
 * @param result a {@link ChangeMatrix} object to compute and hold the results
 * @throws IllegalArgumentException if the ROI's bounds do not contain the entire
 *         source image
 * @see ChangeMatrixDescriptor
 * @see ChangeMatrix
 */
public ChangeMatrixOpImage(RenderedImage reference, RenderedImage now,
        Map config, ImageLayout layout, ROI roi, ChangeMatrix result) {

    super(reference, now, layout, config, true);

    this.result = result;

    this.roi = roi;
    if (roi != null) {
        // check that the ROI contains the source image bounds
        Rectangle sourceBounds = new Rectangle(now.getMinX(), now.getMinY(),
                now.getWidth(), now.getHeight());

        if (!roi.getBounds().contains(sourceBounds)) {
            throw new IllegalArgumentException(
                    "The bounds of the ROI must contain the source image");
        }
    }

    // where do we put the final elements?
}

/**
 * Multiplies the pixel values of two source images within a specified
 * rectangle.
 * 
 * @param sources Cobbled sources, guaranteed to provide all the source data
 *        necessary for computing the rectangle.
 * @param dest The tile containing the rectangle to be computed.
 * @param destRect The rectangle within the tile to be computed.
 */
protected void computeRect(Raster[] sources, WritableRaster dest,
        Rectangle destRect) {
    // Retrieve format tags.
    RasterFormatTag[] formatTags = getFormatTags();

    /* For PointOpImage, srcRect = destRect. */
    RasterAccessor s1 = new RasterAccessor(sources[0], destRect, formatTags[0],
            getSourceImage(0).getColorModel());
    RasterAccessor s2 = new RasterAccessor(sources[1], destRect, formatTags[1],
            getSourceImage(1).getColorModel());
    RasterAccessor d = new RasterAccessor(dest, destRect, formatTags[2],
            getColorModel());
    
    int src1LineStride = s1.getScanlineStride();
    int src1PixelStride = s1.getPixelStride();
    int[] src1BandOffsets = s1.getBandOffsets();

    int src2LineStride = s2.getScanlineStride();
    int src2PixelStride = s2.getPixelStride();
    int[] src2BandOffsets = s2.getBandOffsets();

    int dstNumBands = d.getNumBands();
    int dstWidth = d.getWidth();
    int dstHeight = d.getHeight();
    int dstLineStride = d.getScanlineStride();
    int dstPixelStride = d.getPixelStride();
    int[] dstBandOffsets = d.getBandOffsets();

    switch (s1.getDataType()) {

    case DataBuffer.TYPE_BYTE:
        byteLoop(dstNumBands, dstWidth, dstHeight, src1LineStride,
                src1PixelStride, src1BandOffsets, s1.getByteDataArrays(),
                src2LineStride, src2PixelStride, src2BandOffsets,
                s2.getByteDataArrays(), dstLineStride, dstPixelStride,
                dstBandOffsets, d.getByteDataArrays());
        break;

    case DataBuffer.TYPE_USHORT:
    case DataBuffer.TYPE_SHORT:
        shortLoop(dstNumBands, dstWidth, dstHeight, src1LineStride,
                src1PixelStride, src1BandOffsets, s1.getShortDataArrays(),
                src2LineStride, src2PixelStride, src2BandOffsets,
                s2.getShortDataArrays(), dstLineStride, dstPixelStride,
                dstBandOffsets, d.getShortDataArrays());
        break;

    case DataBuffer.TYPE_INT:
        intLoop(dstNumBands, dstWidth, dstHeight, src1LineStride,
                src1PixelStride, src1BandOffsets, s1.getIntDataArrays(),
                src2LineStride, src2PixelStride, src2BandOffsets,
                s2.getIntDataArrays(), dstLineStride, dstPixelStride,
                dstBandOffsets, d.getIntDataArrays());
        break;
    }

    d.copyBinaryDataToRaster();
}

private void intLoop(int dstNumBands, int dstWidth, int dstHeight,
        int src1LineStride, int src1PixelStride, int[] src1BandOffsets,
        int[][] src1Data, int src2LineStride, int src2PixelStride,
        int[] src2BandOffsets, int[][] src2Data, int dstLineStride,
        int dstPixelStride, int[] dstBandOffsets, int[][] dstData) {

    for (int b = 0; b < dstNumBands; b++) {
        int[] s1 = src1Data[b];
        int[] s2 = src2Data[b];
        int[] d = dstData[b];
        int src1LineOffset = src1BandOffsets[b];
        int src2LineOffset = src2BandOffsets[b];
        int dstLineOffset = dstBandOffsets[b];

        for (int h = 0; h < dstHeight; h++) {
            int src1PixelOffset = src1LineOffset;
            int src2PixelOffset = src2LineOffset;
            int dstPixelOffset = dstLineOffset;
            src1LineOffset += src1LineStride;
            src2LineOffset += src2LineStride;
            dstLineOffset += dstLineStride;

            for (int w = 0; w < dstWidth; w++) {
                final int before = (s1[src1PixelOffset]);
                final int after = (s2[src1PixelOffset]);
                d[dstPixelOffset] = before == after ? 0 : 1;
                // if(roi==null||roi.contains(x, y)){
                result.registerPair(before, after);
                // }

                src1PixelOffset += src1PixelStride;
                src2PixelOffset += src2PixelStride;
                dstPixelOffset += dstPixelStride;
            }
        }
    }
}

private void byteLoop(int dstNumBands, int dstWidth, int dstHeight,
        int src1LineStride, int src1PixelStride, int[] src1BandOffsets,
        byte[][] src1Data, int src2LineStride, int src2PixelStride,
        int[] src2BandOffsets, byte[][] src2Data, int dstLineStride,
        int dstPixelStride, int[] dstBandOffsets, byte[][] dstData) {

    for (int b = 0; b < dstNumBands; b++) {
        byte[] s1 = src1Data[b];
        byte[] s2 = src2Data[b];
        byte[] d = dstData[b];
        int src1LineOffset = src1BandOffsets[b];
        int src2LineOffset = src2BandOffsets[b];
        int dstLineOffset = dstBandOffsets[b];

        for (int h = 0; h < dstHeight; h++) {
            int src1PixelOffset = src1LineOffset;
            int src2PixelOffset = src2LineOffset;
            int dstPixelOffset = dstLineOffset;
            src1LineOffset += src1LineStride;
            src2LineOffset += src2LineStride;
            dstLineOffset += dstLineStride;

            for (int w = 0; w < dstWidth; w++) {
                final byte before = (byte) (s1[src1PixelOffset]);
                final byte after = (byte) (s2[src1PixelOffset]);
                d[dstPixelOffset] = before == after ? (byte) 0 : (byte) 1;
                // if(roi==null||roi.contains(x, y)){
                result.registerPair(s1[src1PixelOffset], s2[src2PixelOffset]);
                // }
                src1PixelOffset += src1PixelStride;
                src2PixelOffset += src2PixelStride;
                dstPixelOffset += dstPixelStride;
            }
        }
    }
}

private void shortLoop(int dstNumBands, int dstWidth, int dstHeight,
        int src1LineStride, int src1PixelStride, int[] src1BandOffsets,
        short[][] src1Data, int src2LineStride, int src2PixelStride,
        int[] src2BandOffsets, short[][] src2Data, int dstLineStride,
        int dstPixelStride, int[] dstBandOffsets, short[][] dstData) {

    for (int b = 0; b < dstNumBands; b++) {
        short[] s1 = src1Data[b];
        short[] s2 = src2Data[b];
        short[] d = dstData[b];
        int src1LineOffset = src1BandOffsets[b];
        int src2LineOffset = src2BandOffsets[b];
        int dstLineOffset = dstBandOffsets[b];

        for (int h = 0; h < dstHeight; h++) {
            int src1PixelOffset = src1LineOffset;
            int src2PixelOffset = src2LineOffset;
            int dstPixelOffset = dstLineOffset;
            src1LineOffset += src1LineStride;
            src2LineOffset += src2LineStride;
            dstLineOffset += dstLineStride;

            for (int w = 0; w < dstWidth; w++) {
                final short before = (short) (s1[src1PixelOffset]);
                final short after = (short) (s2[src1PixelOffset]);
                d[dstPixelOffset] = before == after ? (short) 0 : (short) 1;
                // if(roi==null||roi.contains(x, y)){
                result.registerPair(s1[src1PixelOffset], s2[src2PixelOffset]);
                // }
                src1PixelOffset += src1PixelStride;
                src2PixelOffset += src2PixelStride;
                dstPixelOffset += dstPixelStride;
            }
        }
    }
}
}
