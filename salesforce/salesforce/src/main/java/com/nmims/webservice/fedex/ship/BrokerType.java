
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BrokerType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BrokerType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EXPORT"/>
 *     &lt;enumeration value="IMPORT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BrokerType")
@XmlEnum
public enum BrokerType {

    EXPORT,
    IMPORT;

    public String value() {
        return name();
    }

    public static BrokerType fromValue(String v) {
        return valueOf(v);
    }

}
