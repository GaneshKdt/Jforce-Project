
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EMailNotificationAggregationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EMailNotificationAggregationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PER_PACKAGE"/>
 *     &lt;enumeration value="PER_SHIPMENT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EMailNotificationAggregationType")
@XmlEnum
public enum EMailNotificationAggregationType {

    PER_PACKAGE,
    PER_SHIPMENT;

    public String value() {
        return name();
    }

    public static EMailNotificationAggregationType fromValue(String v) {
        return valueOf(v);
    }

}
