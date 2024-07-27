
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NaftaImporterSpecificationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NaftaImporterSpecificationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="IMPORTER_OF_RECORD"/>
 *     &lt;enumeration value="RECIPIENT"/>
 *     &lt;enumeration value="UNKNOWN"/>
 *     &lt;enumeration value="VARIOUS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "NaftaImporterSpecificationType")
@XmlEnum
public enum NaftaImporterSpecificationType {

    IMPORTER_OF_RECORD,
    RECIPIENT,
    UNKNOWN,
    VARIOUS;

    public String value() {
        return name();
    }

    public static NaftaImporterSpecificationType fromValue(String v) {
        return valueOf(v);
    }

}
