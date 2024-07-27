
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SurchargeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SurchargeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ADDITIONAL_HANDLING"/>
 *     &lt;enumeration value="ANCILLARY_FEE"/>
 *     &lt;enumeration value="APPOINTMENT_DELIVERY"/>
 *     &lt;enumeration value="BROKER_SELECT_OPTION"/>
 *     &lt;enumeration value="CANADIAN_DESTINATION"/>
 *     &lt;enumeration value="CLEARANCE_ENTRY_FEE"/>
 *     &lt;enumeration value="COD"/>
 *     &lt;enumeration value="CUT_FLOWERS"/>
 *     &lt;enumeration value="DANGEROUS_GOODS"/>
 *     &lt;enumeration value="DELIVERY_AREA"/>
 *     &lt;enumeration value="DELIVERY_CONFIRMATION"/>
 *     &lt;enumeration value="DELIVERY_ON_INVOICE_ACCEPTANCE"/>
 *     &lt;enumeration value="DOCUMENTATION_FEE"/>
 *     &lt;enumeration value="DRY_ICE"/>
 *     &lt;enumeration value="EMAIL_LABEL"/>
 *     &lt;enumeration value="EUROPE_FIRST"/>
 *     &lt;enumeration value="EXCESS_VALUE"/>
 *     &lt;enumeration value="EXHIBITION"/>
 *     &lt;enumeration value="EXPORT"/>
 *     &lt;enumeration value="EXTRA_SURFACE_HANDLING_CHARGE"/>
 *     &lt;enumeration value="EXTREME_LENGTH"/>
 *     &lt;enumeration value="FEDEX_INTRACOUNTRY_FEES"/>
 *     &lt;enumeration value="FEDEX_TAG"/>
 *     &lt;enumeration value="FICE"/>
 *     &lt;enumeration value="FLATBED"/>
 *     &lt;enumeration value="FREIGHT_GUARANTEE"/>
 *     &lt;enumeration value="FREIGHT_ON_VALUE"/>
 *     &lt;enumeration value="FREIGHT_TO_COLLECT"/>
 *     &lt;enumeration value="FUEL"/>
 *     &lt;enumeration value="HOLD_AT_LOCATION"/>
 *     &lt;enumeration value="HOME_DELIVERY_APPOINTMENT"/>
 *     &lt;enumeration value="HOME_DELIVERY_DATE_CERTAIN"/>
 *     &lt;enumeration value="HOME_DELIVERY_EVENING"/>
 *     &lt;enumeration value="INSIDE_DELIVERY"/>
 *     &lt;enumeration value="INSIDE_PICKUP"/>
 *     &lt;enumeration value="INSURED_VALUE"/>
 *     &lt;enumeration value="INTERHAWAII"/>
 *     &lt;enumeration value="LIFTGATE_DELIVERY"/>
 *     &lt;enumeration value="LIFTGATE_PICKUP"/>
 *     &lt;enumeration value="LIMITED_ACCESS_DELIVERY"/>
 *     &lt;enumeration value="LIMITED_ACCESS_PICKUP"/>
 *     &lt;enumeration value="METRO_DELIVERY"/>
 *     &lt;enumeration value="METRO_PICKUP"/>
 *     &lt;enumeration value="NON_MACHINABLE"/>
 *     &lt;enumeration value="OFFSHORE"/>
 *     &lt;enumeration value="ON_CALL_PICKUP"/>
 *     &lt;enumeration value="OTHER"/>
 *     &lt;enumeration value="OUT_OF_DELIVERY_AREA"/>
 *     &lt;enumeration value="OUT_OF_PICKUP_AREA"/>
 *     &lt;enumeration value="OVERSIZE"/>
 *     &lt;enumeration value="OVER_DIMENSION"/>
 *     &lt;enumeration value="PIECE_COUNT_VERIFICATION"/>
 *     &lt;enumeration value="PRE_DELIVERY_NOTIFICATION"/>
 *     &lt;enumeration value="PRIORITY_ALERT"/>
 *     &lt;enumeration value="PROTECTION_FROM_FREEZING"/>
 *     &lt;enumeration value="REGIONAL_MALL_DELIVERY"/>
 *     &lt;enumeration value="REGIONAL_MALL_PICKUP"/>
 *     &lt;enumeration value="REROUTE"/>
 *     &lt;enumeration value="RESCHEDULE"/>
 *     &lt;enumeration value="RESIDENTIAL_DELIVERY"/>
 *     &lt;enumeration value="RESIDENTIAL_PICKUP"/>
 *     &lt;enumeration value="RETURN_LABEL"/>
 *     &lt;enumeration value="SATURDAY_DELIVERY"/>
 *     &lt;enumeration value="SATURDAY_PICKUP"/>
 *     &lt;enumeration value="SIGNATURE_OPTION"/>
 *     &lt;enumeration value="TARP"/>
 *     &lt;enumeration value="THIRD_PARTY_CONSIGNEE"/>
 *     &lt;enumeration value="TRANSMART_SERVICE_FEE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SurchargeType")
@XmlEnum
public enum SurchargeType {

    ADDITIONAL_HANDLING,
    ANCILLARY_FEE,
    APPOINTMENT_DELIVERY,
    BROKER_SELECT_OPTION,
    CANADIAN_DESTINATION,
    CLEARANCE_ENTRY_FEE,
    COD,
    CUT_FLOWERS,
    DANGEROUS_GOODS,
    DELIVERY_AREA,
    DELIVERY_CONFIRMATION,
    DELIVERY_ON_INVOICE_ACCEPTANCE,
    DOCUMENTATION_FEE,
    DRY_ICE,
    EMAIL_LABEL,
    EUROPE_FIRST,
    EXCESS_VALUE,
    EXHIBITION,
    EXPORT,
    EXTRA_SURFACE_HANDLING_CHARGE,
    EXTREME_LENGTH,
    FEDEX_INTRACOUNTRY_FEES,
    FEDEX_TAG,
    FICE,
    FLATBED,
    FREIGHT_GUARANTEE,
    FREIGHT_ON_VALUE,
    FREIGHT_TO_COLLECT,
    FUEL,
    HOLD_AT_LOCATION,
    HOME_DELIVERY_APPOINTMENT,
    HOME_DELIVERY_DATE_CERTAIN,
    HOME_DELIVERY_EVENING,
    INSIDE_DELIVERY,
    INSIDE_PICKUP,
    INSURED_VALUE,
    INTERHAWAII,
    LIFTGATE_DELIVERY,
    LIFTGATE_PICKUP,
    LIMITED_ACCESS_DELIVERY,
    LIMITED_ACCESS_PICKUP,
    METRO_DELIVERY,
    METRO_PICKUP,
    NON_MACHINABLE,
    OFFSHORE,
    ON_CALL_PICKUP,
    OTHER,
    OUT_OF_DELIVERY_AREA,
    OUT_OF_PICKUP_AREA,
    OVERSIZE,
    OVER_DIMENSION,
    PIECE_COUNT_VERIFICATION,
    PRE_DELIVERY_NOTIFICATION,
    PRIORITY_ALERT,
    PROTECTION_FROM_FREEZING,
    REGIONAL_MALL_DELIVERY,
    REGIONAL_MALL_PICKUP,
    REROUTE,
    RESCHEDULE,
    RESIDENTIAL_DELIVERY,
    RESIDENTIAL_PICKUP,
    RETURN_LABEL,
    SATURDAY_DELIVERY,
    SATURDAY_PICKUP,
    SIGNATURE_OPTION,
    TARP,
    THIRD_PARTY_CONSIGNEE,
    TRANSMART_SERVICE_FEE;

    public String value() {
        return name();
    }

    public static SurchargeType fromValue(String v) {
        return valueOf(v);
    }

}
