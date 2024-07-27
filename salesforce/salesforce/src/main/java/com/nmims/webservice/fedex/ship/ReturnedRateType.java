
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReturnedRateType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ReturnedRateType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INCENTIVE"/>
 *     &lt;enumeration value="PAYOR_ACCOUNT_PACKAGE"/>
 *     &lt;enumeration value="PAYOR_ACCOUNT_SHIPMENT"/>
 *     &lt;enumeration value="PAYOR_LIST_PACKAGE"/>
 *     &lt;enumeration value="PAYOR_LIST_SHIPMENT"/>
 *     &lt;enumeration value="PREFERRED_ACCOUNT_PACKAGE"/>
 *     &lt;enumeration value="PREFERRED_ACCOUNT_SHIPMENT"/>
 *     &lt;enumeration value="PREFERRED_LIST_PACKAGE"/>
 *     &lt;enumeration value="PREFERRED_LIST_SHIPMENT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ReturnedRateType")
@XmlEnum
public enum ReturnedRateType {

    INCENTIVE,
    PAYOR_ACCOUNT_PACKAGE,
    PAYOR_ACCOUNT_SHIPMENT,
    PAYOR_LIST_PACKAGE,
    PAYOR_LIST_SHIPMENT,
    PREFERRED_ACCOUNT_PACKAGE,
    PREFERRED_ACCOUNT_SHIPMENT,
    PREFERRED_LIST_PACKAGE,
    PREFERRED_LIST_SHIPMENT;

    public String value() {
        return name();
    }

    public static ReturnedRateType fromValue(String v) {
        return valueOf(v);
    }

}
