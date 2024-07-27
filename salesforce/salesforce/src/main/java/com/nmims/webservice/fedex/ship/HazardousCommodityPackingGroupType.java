
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HazardousCommodityPackingGroupType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HazardousCommodityPackingGroupType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DEFAULT"/>
 *     &lt;enumeration value="I"/>
 *     &lt;enumeration value="II"/>
 *     &lt;enumeration value="III"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "HazardousCommodityPackingGroupType")
@XmlEnum
public enum HazardousCommodityPackingGroupType {

    DEFAULT,
    I,
    II,
    III;

    public String value() {
        return name();
    }

    public static HazardousCommodityPackingGroupType fromValue(String v) {
        return valueOf(v);
    }

}
