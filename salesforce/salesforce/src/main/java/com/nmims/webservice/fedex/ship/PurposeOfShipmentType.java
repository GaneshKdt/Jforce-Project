
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PurposeOfShipmentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PurposeOfShipmentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="GIFT"/>
 *     &lt;enumeration value="NOT_SOLD"/>
 *     &lt;enumeration value="PERSONAL_EFFECTS"/>
 *     &lt;enumeration value="REPAIR_AND_RETURN"/>
 *     &lt;enumeration value="SAMPLE"/>
 *     &lt;enumeration value="SOLD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PurposeOfShipmentType")
@XmlEnum
public enum PurposeOfShipmentType {

    GIFT,
    NOT_SOLD,
    PERSONAL_EFFECTS,
    REPAIR_AND_RETURN,
    SAMPLE,
    SOLD;

    public String value() {
        return name();
    }

    public static PurposeOfShipmentType fromValue(String v) {
        return valueOf(v);
    }

}
