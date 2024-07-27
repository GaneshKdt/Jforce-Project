
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomsRoleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CustomsRoleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EXPORTER"/>
 *     &lt;enumeration value="IMPORTER"/>
 *     &lt;enumeration value="LEGAL_AGENT"/>
 *     &lt;enumeration value="PRODUCER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CustomsRoleType")
@XmlEnum
public enum CustomsRoleType {

    EXPORTER,
    IMPORTER,
    LEGAL_AGENT,
    PRODUCER;

    public String value() {
        return name();
    }

    public static CustomsRoleType fromValue(String v) {
        return valueOf(v);
    }

}
