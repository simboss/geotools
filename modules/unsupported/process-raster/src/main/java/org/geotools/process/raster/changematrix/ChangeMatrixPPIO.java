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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.geoserver.wps.ppio.CDataPPIO;

/**
 * @author Damiano Giampaoli, GeoSolutions SAS
 * 
 */
public class ChangeMatrixPPIO extends CDataPPIO {

    public ChangeMatrixPPIO() {
        super(ChangeMatrixOutput.class, ChangeMatrixOutput.class, "application/json");
    }

    @Override
    public Object decode(InputStream input) throws Exception {
        return null;
    }

    @Override
    public void encode(Object value, OutputStream os) throws Exception {
        PrintWriter pw = new PrintWriter(os);
        try{

            pw.write(JSONSerializer.toJSON(((ChangeMatrixOutput)value).getChangeMatrix()).toString());
        } finally{
        	IOUtils.closeQuietly(pw);
        	IOUtils.closeQuietly(os);
        }
    }

    @Override
    public String getFileExtension() {
        return "json";
    }

    /* (non-Javadoc)
     * @see org.geoserver.wps.ppio.CDataPPIO#decode(java.lang.String)
     */
    @Override
    public Object decode(String input) throws Exception {
        return null;
    }

}
