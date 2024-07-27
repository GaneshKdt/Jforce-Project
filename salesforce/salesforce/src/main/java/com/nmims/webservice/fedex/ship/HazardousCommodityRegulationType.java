
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HazardousCommodityRegulationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HazardousCommodityRegulationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ADR"/>
 *     &lt;enumeration value="DOT"/>
 *     &lt;enumeration value="IATA"/>
 *     &lt;enumeration value="ORMD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "HazardousCommodityRegulationType")
@XmlEnum
public enum HazardousCommodityRegulationType {

    ADR,
    DOT,
    IATA,
    ORMD;

    public String value() {
        return name();
    }

    public static HazardousCommodityRegulationType fromValue(String v) {
        return valueOf(v);
    }

}
