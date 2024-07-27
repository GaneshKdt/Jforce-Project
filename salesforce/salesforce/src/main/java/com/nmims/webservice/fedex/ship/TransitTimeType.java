
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransitTimeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransitTimeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EIGHTEEN_DAYS"/>
 *     &lt;enumeration value="EIGHT_DAYS"/>
 *     &lt;enumeration value="ELEVEN_DAYS"/>
 *     &lt;enumeration value="FIFTEEN_DAYS"/>
 *     &lt;enumeration value="FIVE_DAYS"/>
 *     &lt;enumeration value="FOURTEEN_DAYS"/>
 *     &lt;enumeration value="FOUR_DAYS"/>
 *     &lt;enumeration value="NINETEEN_DAYS"/>
 *     &lt;enumeration value="NINE_DAYS"/>
 *     &lt;enumeration value="ONE_DAY"/>
 *     &lt;enumeration value="SEVENTEEN_DAYS"/>
 *     &lt;enumeration value="SEVEN_DAYS"/>
 *     &lt;enumeration value="SIXTEEN_DAYS"/>
 *     &lt;enumeration value="SIX_DAYS"/>
 *     &lt;enumeration value="TEN_DAYS"/>
 *     &lt;enumeration value="THIRTEEN_DAYS"/>
 *     &lt;enumeration value="THREE_DAYS"/>
 *     &lt;enumeration value="TWELVE_DAYS"/>
 *     &lt;enumeration value="TWENTY_DAYS"/>
 *     &lt;enumeration value="TWO_DAYS"/>
 *     &lt;enumeration value="UNKNOWN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransitTimeType")
@XmlEnum
public enum TransitTimeType {

    EIGHTEEN_DAYS,
    EIGHT_DAYS,
    ELEVEN_DAYS,
    FIFTEEN_DAYS,
    FIVE_DAYS,
    FOURTEEN_DAYS,
    FOUR_DAYS,
    NINETEEN_DAYS,
    NINE_DAYS,
    ONE_DAY,
    SEVENTEEN_DAYS,
    SEVEN_DAYS,
    SIXTEEN_DAYS,
    SIX_DAYS,
    TEN_DAYS,
    THIRTEEN_DAYS,
    THREE_DAYS,
    TWELVE_DAYS,
    TWENTY_DAYS,
    TWO_DAYS,
    UNKNOWN;

    public String value() {
        return name();
    }

    public static TransitTimeType fromValue(String v) {
        return valueOf(v);
    }

}
