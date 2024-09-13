/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author suhan
 */
package util;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.GenericPackager;

public class PackagerUtil {
    public static ISOPackager createISOPackager() throws ISOException {
        try {
            // Specify the path to the ISO 8583 packager XML file
            String xmlFilePath = "C:\\opt\\asacgateway\\iso87ascii.xml";
            ISOPackager packager = new GenericPackager(xmlFilePath);
            return packager;
        } catch (ISOException e) {
            throw new ISOException("Failed to create ISOPackager", e);
        }
    }
}
