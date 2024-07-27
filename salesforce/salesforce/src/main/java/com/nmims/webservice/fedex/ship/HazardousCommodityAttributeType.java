
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HazardousCommodityAttributeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HazardousCommodityAttributeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NOT_SUBJECT_TO_REGULATIONS"/>
 *     &lt;enumeration value="PLACARDED_VEHICLE_REQUIRED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "HazardousCommodityAttributeType")
@XmlEnum
public enum HazardousCommodityAttributeType {

    NOT_SUBJECT_TO_REGULATIONS,
    PLACARDED_VEHICLE_REQUIRED;

    public String value() {
        return name();
    }

    public static HazardousCommodityAttributeType fromValue(String v) {
        return valueOf(v);
    }

}
