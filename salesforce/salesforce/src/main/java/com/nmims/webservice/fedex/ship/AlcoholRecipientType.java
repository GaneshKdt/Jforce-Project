
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AlcoholRecipientType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AlcoholRecipientType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CONSUMER"/>
 *     &lt;enumeration value="LICENSEE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AlcoholRecipientType")
@XmlEnum
public enum AlcoholRecipientType {

    CONSUMER,
    LICENSEE;

    public String value() {
        return name();
    }

    public static AlcoholRecipientType fromValue(String v) {
        return valueOf(v);
    }

}
