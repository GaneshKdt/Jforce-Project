
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RecipientCustomsIdType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RecipientCustomsIdType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="COMPANY"/>
 *     &lt;enumeration value="INDIVIDUAL"/>
 *     &lt;enumeration value="PASSPORT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RecipientCustomsIdType")
@XmlEnum
public enum RecipientCustomsIdType {

    COMPANY,
    INDIVIDUAL,
    PASSPORT;

    public String value() {
        return name();
    }

    public static RecipientCustomsIdType fromValue(String v) {
        return valueOf(v);
    }

}
