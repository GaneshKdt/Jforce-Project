
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SecondaryBarcodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SecondaryBarcodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="COMMON_2D"/>
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="SSCC_18"/>
 *     &lt;enumeration value="USPS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SecondaryBarcodeType")
@XmlEnum
public enum SecondaryBarcodeType {

    @XmlEnumValue("COMMON_2D")
    COMMON_2_D("COMMON_2D"),
    NONE("NONE"),
    SSCC_18("SSCC_18"),
    USPS("USPS");
    private final String value;

    SecondaryBarcodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SecondaryBarcodeType fromValue(String v) {
        for (SecondaryBarcodeType c: SecondaryBarcodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
