
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LabelPrintingOrientationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LabelPrintingOrientationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BOTTOM_EDGE_OF_TEXT_FIRST"/>
 *     &lt;enumeration value="TOP_EDGE_OF_TEXT_FIRST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LabelPrintingOrientationType")
@XmlEnum
public enum LabelPrintingOrientationType {

    BOTTOM_EDGE_OF_TEXT_FIRST,
    TOP_EDGE_OF_TEXT_FIRST;

    public String value() {
        return name();
    }

    public static LabelPrintingOrientationType fromValue(String v) {
        return valueOf(v);
    }

}
