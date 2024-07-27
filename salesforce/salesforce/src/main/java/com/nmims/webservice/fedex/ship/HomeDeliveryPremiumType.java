
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HomeDeliveryPremiumType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HomeDeliveryPremiumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="APPOINTMENT"/>
 *     &lt;enumeration value="DATE_CERTAIN"/>
 *     &lt;enumeration value="EVENING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "HomeDeliveryPremiumType")
@XmlEnum
public enum HomeDeliveryPremiumType {

    APPOINTMENT,
    DATE_CERTAIN,
    EVENING;

    public String value() {
        return name();
    }

    public static HomeDeliveryPremiumType fromValue(String v) {
        return valueOf(v);
    }

}
