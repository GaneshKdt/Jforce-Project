
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TinType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TinType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BUSINESS_NATIONAL"/>
 *     &lt;enumeration value="BUSINESS_STATE"/>
 *     &lt;enumeration value="BUSINESS_UNION"/>
 *     &lt;enumeration value="PERSONAL_NATIONAL"/>
 *     &lt;enumeration value="PERSONAL_STATE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TinType")
@XmlEnum
public enum TinType {

    BUSINESS_NATIONAL,
    BUSINESS_STATE,
    BUSINESS_UNION,
    PERSONAL_NATIONAL,
    PERSONAL_STATE;

    public String value() {
        return name();
    }

    public static TinType fromValue(String v) {
        return valueOf(v);
    }

}
