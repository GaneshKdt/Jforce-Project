
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BarcodeSymbologyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BarcodeSymbologyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CODABAR"/>
 *     &lt;enumeration value="CODE128"/>
 *     &lt;enumeration value="CODE128B"/>
 *     &lt;enumeration value="CODE128C"/>
 *     &lt;enumeration value="CODE128_WIDEBAR"/>
 *     &lt;enumeration value="CODE39"/>
 *     &lt;enumeration value="CODE93"/>
 *     &lt;enumeration value="I2OF5"/>
 *     &lt;enumeration value="PDF417"/>
 *     &lt;enumeration value="POSTNET"/>
 *     &lt;enumeration value="QR_CODE"/>
 *     &lt;enumeration value="UCC128"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BarcodeSymbologyType")
@XmlEnum
public enum BarcodeSymbologyType {

    CODABAR("CODABAR"),
    @XmlEnumValue("CODE128")
    CODE_128("CODE128"),
    @XmlEnumValue("CODE128B")
    CODE_128_B("CODE128B"),
    @XmlEnumValue("CODE128C")
    CODE_128_C("CODE128C"),
    @XmlEnumValue("CODE128_WIDEBAR")
    CODE_128_WIDEBAR("CODE128_WIDEBAR"),
    @XmlEnumValue("CODE39")
    CODE_39("CODE39"),
    @XmlEnumValue("CODE93")
    CODE_93("CODE93"),
    @XmlEnumValue("I2OF5")
    I_2_OF_5("I2OF5"),
    @XmlEnumValue("PDF417")
    PDF_417("PDF417"),
    POSTNET("POSTNET"),
    QR_CODE("QR_CODE"),
    @XmlEnumValue("UCC128")
    UCC_128("UCC128");
    private final String value;

    BarcodeSymbologyType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BarcodeSymbologyType fromValue(String v) {
        for (BarcodeSymbologyType c: BarcodeSymbologyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
