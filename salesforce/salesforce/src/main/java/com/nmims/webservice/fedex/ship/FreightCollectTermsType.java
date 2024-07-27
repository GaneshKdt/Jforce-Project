
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FreightCollectTermsType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FreightCollectTermsType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NON_RECOURSE_SHIPPER_SIGNED"/>
 *     &lt;enumeration value="STANDARD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FreightCollectTermsType")
@XmlEnum
public enum FreightCollectTermsType {

    NON_RECOURSE_SHIPPER_SIGNED,
    STANDARD;

    public String value() {
        return name();
    }

    public static FreightCollectTermsType fromValue(String v) {
        return valueOf(v);
    }

}
