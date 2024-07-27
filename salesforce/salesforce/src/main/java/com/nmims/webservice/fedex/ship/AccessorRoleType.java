
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AccessorRoleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AccessorRoleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SHIPMENT_COMPLETOR"/>
 *     &lt;enumeration value="SHIPMENT_INITIATOR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AccessorRoleType")
@XmlEnum
public enum AccessorRoleType {

    SHIPMENT_COMPLETOR,
    SHIPMENT_INITIATOR;

    public String value() {
        return name();
    }

    public static AccessorRoleType fromValue(String v) {
        return valueOf(v);
    }

}
