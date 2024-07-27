
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LabelMaskableDataType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LabelMaskableDataType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CUSTOMS_VALUE"/>
 *     &lt;enumeration value="DUTIES_AND_TAXES_PAYOR_ACCOUNT_NUMBER"/>
 *     &lt;enumeration value="SECONDARY_BARCODE"/>
 *     &lt;enumeration value="SHIPPER_ACCOUNT_NUMBER"/>
 *     &lt;enumeration value="TERMS_AND_CONDITIONS"/>
 *     &lt;enumeration value="TRANSPORTATION_CHARGES_PAYOR_ACCOUNT_NUMBER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LabelMaskableDataType")
@XmlEnum
public enum LabelMaskableDataType {

    CUSTOMS_VALUE,
    DUTIES_AND_TAXES_PAYOR_ACCOUNT_NUMBER,
    SECONDARY_BARCODE,
    SHIPPER_ACCOUNT_NUMBER,
    TERMS_AND_CONDITIONS,
    TRANSPORTATION_CHARGES_PAYOR_ACCOUNT_NUMBER;

    public String value() {
        return name();
    }

    public static LabelMaskableDataType fromValue(String v) {
        return valueOf(v);
    }

}
