
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EmailOptionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EmailOptionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SUPPRESS_ACCESS_EMAILS"/>
 *     &lt;enumeration value="SUPPRESS_ADDITIONAL_LANGUAGES"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EmailOptionType")
@XmlEnum
public enum EmailOptionType {

    SUPPRESS_ACCESS_EMAILS,
    SUPPRESS_ADDITIONAL_LANGUAGES;

    public String value() {
        return name();
    }

    public static EmailOptionType fromValue(String v) {
        return valueOf(v);
    }

}
