
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CodCollectionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CodCollectionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ANY"/>
 *     &lt;enumeration value="CASH"/>
 *     &lt;enumeration value="COMPANY_CHECK"/>
 *     &lt;enumeration value="GUARANTEED_FUNDS"/>
 *     &lt;enumeration value="PERSONAL_CHECK"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CodCollectionType")
@XmlEnum
public enum CodCollectionType {

    ANY,
    CASH,
    COMPANY_CHECK,
    GUARANTEED_FUNDS,
    PERSONAL_CHECK;

    public String value() {
        return name();
    }

    public static CodCollectionType fromValue(String v) {
        return valueOf(v);
    }

}
