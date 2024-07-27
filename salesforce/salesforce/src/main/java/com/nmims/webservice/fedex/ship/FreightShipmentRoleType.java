
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FreightShipmentRoleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FreightShipmentRoleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CONSIGNEE"/>
 *     &lt;enumeration value="SHIPPER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FreightShipmentRoleType")
@XmlEnum
public enum FreightShipmentRoleType {

    CONSIGNEE,
    SHIPPER;

    public String value() {
        return name();
    }

    public static FreightShipmentRoleType fromValue(String v) {
        return valueOf(v);
    }

}
