
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PendingShipmentProcessingOptionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PendingShipmentProcessingOptionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ALLOW_MODIFICATIONS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PendingShipmentProcessingOptionType")
@XmlEnum
public enum PendingShipmentProcessingOptionType {

    ALLOW_MODIFICATIONS;

    public String value() {
        return name();
    }

    public static PendingShipmentProcessingOptionType fromValue(String v) {
        return valueOf(v);
    }

}
