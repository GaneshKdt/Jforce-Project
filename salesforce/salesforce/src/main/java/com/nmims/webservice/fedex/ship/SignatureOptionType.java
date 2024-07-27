
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SignatureOptionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SignatureOptionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ADULT"/>
 *     &lt;enumeration value="DIRECT"/>
 *     &lt;enumeration value="INDIRECT"/>
 *     &lt;enumeration value="NO_SIGNATURE_REQUIRED"/>
 *     &lt;enumeration value="SERVICE_DEFAULT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SignatureOptionType")
@XmlEnum
public enum SignatureOptionType {

    ADULT,
    DIRECT,
    INDIRECT,
    NO_SIGNATURE_REQUIRED,
    SERVICE_DEFAULT;

    public String value() {
        return name();
    }

    public static SignatureOptionType fromValue(String v) {
        return valueOf(v);
    }

}
