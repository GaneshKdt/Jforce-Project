
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DangerousGoodsPackingOptionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DangerousGoodsPackingOptionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OVERPACK"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DangerousGoodsPackingOptionType")
@XmlEnum
public enum DangerousGoodsPackingOptionType {

    OVERPACK;

    public String value() {
        return name();
    }

    public static DangerousGoodsPackingOptionType fromValue(String v) {
        return valueOf(v);
    }

}
