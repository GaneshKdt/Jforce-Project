
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HazardousCommodityOptionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HazardousCommodityOptionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BATTERY"/>
 *     &lt;enumeration value="HAZARDOUS_MATERIALS"/>
 *     &lt;enumeration value="LIMITED_QUANTITIES_COMMODITIES"/>
 *     &lt;enumeration value="ORM_D"/>
 *     &lt;enumeration value="REPORTABLE_QUANTITIES"/>
 *     &lt;enumeration value="SMALL_QUANTITY_EXCEPTION"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "HazardousCommodityOptionType")
@XmlEnum
public enum HazardousCommodityOptionType {

    BATTERY,
    HAZARDOUS_MATERIALS,
    LIMITED_QUANTITIES_COMMODITIES,
    ORM_D,
    REPORTABLE_QUANTITIES,
    SMALL_QUANTITY_EXCEPTION;

    public String value() {
        return name();
    }

    public static HazardousCommodityOptionType fromValue(String v) {
        return valueOf(v);
    }

}
