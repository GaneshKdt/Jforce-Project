
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CodAdjustmentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CodAdjustmentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CHARGES_ADDED"/>
 *     &lt;enumeration value="NONE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CodAdjustmentType")
@XmlEnum
public enum CodAdjustmentType {

    CHARGES_ADDED,
    NONE;

    public String value() {
        return name();
    }

    public static CodAdjustmentType fromValue(String v) {
        return valueOf(v);
    }

}
