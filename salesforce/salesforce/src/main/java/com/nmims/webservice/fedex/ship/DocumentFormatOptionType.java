
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentFormatOptionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DocumentFormatOptionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SUPPRESS_ADDITIONAL_LANGUAGES"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DocumentFormatOptionType")
@XmlEnum
public enum DocumentFormatOptionType {

    SUPPRESS_ADDITIONAL_LANGUAGES;

    public String value() {
        return name();
    }

    public static DocumentFormatOptionType fromValue(String v) {
        return valueOf(v);
    }

}
