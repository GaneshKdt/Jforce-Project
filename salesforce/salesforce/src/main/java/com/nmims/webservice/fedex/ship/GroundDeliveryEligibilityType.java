
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GroundDeliveryEligibilityType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GroundDeliveryEligibilityType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ALTERNATE_DAY_SERVICE"/>
 *     &lt;enumeration value="CARTAGE_AGENT_DELIVERY"/>
 *     &lt;enumeration value="SATURDAY_DELIVERY"/>
 *     &lt;enumeration value="USPS_DELIVERY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "GroundDeliveryEligibilityType")
@XmlEnum
public enum GroundDeliveryEligibilityType {

    ALTERNATE_DAY_SERVICE,
    CARTAGE_AGENT_DELIVERY,
    SATURDAY_DELIVERY,
    USPS_DELIVERY;

    public String value() {
        return name();
    }

    public static GroundDeliveryEligibilityType fromValue(String v) {
        return valueOf(v);
    }

}
