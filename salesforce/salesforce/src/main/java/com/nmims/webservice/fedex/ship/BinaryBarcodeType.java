
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BinaryBarcodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BinaryBarcodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="COMMON_2D"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BinaryBarcodeType")
@XmlEnum
public enum BinaryBarcodeType {

    @XmlEnumValue("COMMON_2D")
    COMMON_2_D("COMMON_2D");
    private final String value;

    BinaryBarcodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BinaryBarcodeType fromValue(String v) {
        for (BinaryBarcodeType c: BinaryBarcodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
