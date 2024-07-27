
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LiabilityCoverageType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LiabilityCoverageType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NEW"/>
 *     &lt;enumeration value="USED_OR_RECONDITIONED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LiabilityCoverageType")
@XmlEnum
public enum LiabilityCoverageType {

    NEW,
    USED_OR_RECONDITIONED;

    public String value() {
        return name();
    }

    public static LiabilityCoverageType fromValue(String v) {
        return valueOf(v);
    }

}
