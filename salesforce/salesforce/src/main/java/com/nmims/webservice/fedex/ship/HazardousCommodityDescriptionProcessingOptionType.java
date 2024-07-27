
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HazardousCommodityDescriptionProcessingOptionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HazardousCommodityDescriptionProcessingOptionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INCLUDE_SPECIAL_PROVISIONS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "HazardousCommodityDescriptionProcessingOptionType")
@XmlEnum
public enum HazardousCommodityDescriptionProcessingOptionType {

    INCLUDE_SPECIAL_PROVISIONS;

    public String value() {
        return name();
    }

    public static HazardousCommodityDescriptionProcessingOptionType fromValue(String v) {
        return valueOf(v);
    }

}
