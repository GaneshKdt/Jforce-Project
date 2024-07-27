
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SmartPostShipmentProcessingOptionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SmartPostShipmentProcessingOptionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="GROUND_TRACKING_NUMBER_REQUESTED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SmartPostShipmentProcessingOptionType")
@XmlEnum
public enum SmartPostShipmentProcessingOptionType {

    GROUND_TRACKING_NUMBER_REQUESTED;

    public String value() {
        return name();
    }

    public static SmartPostShipmentProcessingOptionType fromValue(String v) {
        return valueOf(v);
    }

}
