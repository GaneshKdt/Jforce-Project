
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReturnEMailAllowedSpecialServiceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ReturnEMailAllowedSpecialServiceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SATURDAY_DELIVERY"/>
 *     &lt;enumeration value="SATURDAY_PICKUP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ReturnEMailAllowedSpecialServiceType")
@XmlEnum
public enum ReturnEMailAllowedSpecialServiceType {

    SATURDAY_DELIVERY,
    SATURDAY_PICKUP;

    public String value() {
        return name();
    }

    public static ReturnEMailAllowedSpecialServiceType fromValue(String v) {
        return valueOf(v);
    }

}
