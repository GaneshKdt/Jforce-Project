
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HazardousContainerPackingType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HazardousContainerPackingType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ALL_PACKED_IN_ONE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "HazardousContainerPackingType")
@XmlEnum
public enum HazardousContainerPackingType {

    ALL_PACKED_IN_ONE;

    public String value() {
        return name();
    }

    public static HazardousContainerPackingType fromValue(String v) {
        return valueOf(v);
    }

}
