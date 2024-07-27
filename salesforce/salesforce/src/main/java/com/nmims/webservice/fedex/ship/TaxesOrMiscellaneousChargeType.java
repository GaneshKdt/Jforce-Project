
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TaxesOrMiscellaneousChargeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TaxesOrMiscellaneousChargeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="COMMISSIONS"/>
 *     &lt;enumeration value="DISCOUNTS"/>
 *     &lt;enumeration value="HANDLING_FEES"/>
 *     &lt;enumeration value="OTHER"/>
 *     &lt;enumeration value="ROYALTIES_AND_LICENSE_FEES"/>
 *     &lt;enumeration value="TAXES"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TaxesOrMiscellaneousChargeType")
@XmlEnum
public enum TaxesOrMiscellaneousChargeType {

    COMMISSIONS,
    DISCOUNTS,
    HANDLING_FEES,
    OTHER,
    ROYALTIES_AND_LICENSE_FEES,
    TAXES;

    public String value() {
        return name();
    }

    public static TaxesOrMiscellaneousChargeType fromValue(String v) {
        return valueOf(v);
    }

}
