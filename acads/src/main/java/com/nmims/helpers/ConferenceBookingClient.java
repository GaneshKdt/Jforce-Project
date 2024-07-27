package com.nmims.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.params.AuthPolicy;
import org.springframework.oxm.Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import bookingservice.wsdl.Conference;
import bookingservice.wsdl.ContextHeader;
import bookingservice.wsdl.ExternalAPIVersionSoapHeader;
import bookingservice.wsdl.ObjectFactory;
import bookingservice.wsdl.SaveConferenceWithMode;
import bookingservice.wsdl.SaveConferenceWithModeResponse;
import bookingservice.wsdl.SaveConferences;
import bookingservice.wsdl.SaveConferencesResponse;

public class ConferenceBookingClient {

	private WebServiceTemplate wsTemplate;

	private Marshaller marshaller; 

	public void setWsTemplate(WebServiceTemplate wsTemplate) {
		this.wsTemplate = wsTemplate;

		HttpComponentsMessageSender sender = (HttpComponentsMessageSender) wsTemplate.getMessageSenders()[0];
		Credentials credentials = new NTCredentials("testuser", "pass@123", "", "svkmgrp");
		sender.setCredentials(credentials);
		List<String> authpref = new ArrayList<String>();
		authpref.add(AuthPolicy.NTLM);
		sender.getHttpClient().getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authpref);
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public Conference saveConference(Conference conference) {

		SaveConferenceWithMode request = new SaveConferenceWithMode();
		//request.setBookingMode(BookingMode.STRICT);
		request.setConference(conference);
		final ContextHeader contextHeader = new ContextHeader();
		contextHeader.setSendConfirmationMail(false);
		contextHeader.setExcludeConferenceInformation(false);
		final ExternalAPIVersionSoapHeader apiVersionHeader = new ExternalAPIVersionSoapHeader();
		apiVersionHeader.setClientVersionIn(14);

		SaveConferenceWithModeResponse response = (SaveConferenceWithModeResponse) wsTemplate.marshalSendAndReceive(request, new WebServiceMessageCallback(){

			@Override
			public void doWithMessage(WebServiceMessage message)
					throws IOException, TransformerException {
				((SoapMessage)message).setSoapAction("http://tandberg.net/2004/02/tms/external/booking/SaveConference");
				SoapHeader soapHeader = ((SoapMessage)message).getSoapHeader();
				try {
					JAXBContext context = JAXBContext.newInstance( ContextHeader.class, ExternalAPIVersionSoapHeader.class );
					ObjectFactory objectFactory = new ObjectFactory();
					JAXBElement<ContextHeader> contextHeaderJE = objectFactory.createContextHeader(contextHeader);
					JAXBElement<ExternalAPIVersionSoapHeader> apiVersionHeaderJE = objectFactory.createExternalAPIVersionSoapHeader(apiVersionHeader);
					javax.xml.bind.Marshaller marshaller = context.createMarshaller();
					marshaller.marshal(apiVersionHeaderJE, soapHeader.getResult());
					marshaller.marshal( contextHeaderJE, soapHeader.getResult() );

				} catch (JAXBException e) {
					// TODO Auto-generated catch block
					  
				}

			}

		});

		return response.getSaveConferenceWithModeResult().getConference();

	}

	public List<Conference> saveConferences(List<Conference> conferences) {

		try {
			final SaveConferences request = new SaveConferences();
			request.setOneTransaction(true);
			request.getConferences().addAll(conferences);


			final ContextHeader contextHeader = new ContextHeader();
			contextHeader.setSendConfirmationMail(false);
			contextHeader.setExcludeConferenceInformation(false);
			final ExternalAPIVersionSoapHeader apiVersionHeader = new ExternalAPIVersionSoapHeader();
			apiVersionHeader.setClientVersionIn(14);

			SaveConferencesResponse response = (SaveConferencesResponse) wsTemplate.marshalSendAndReceive(request, new WebServiceMessageCallback(){

				@Override
				public void doWithMessage(WebServiceMessage message)
						throws IOException, TransformerException {
					((SoapMessage)message).setSoapAction("http://tandberg.net/2004/02/tms/external/booking/SaveConferences");
					/*StringBuffer sbHeader = new StringBuffer();
					sbHeader.append("<book:ExternalAPIVersionSoapHeader xmlns:book=\"http://tandberg.net/2004/02/tms/external/booking/\">");
					sbHeader.append("<book:ClientVersionIn>14</book:ClientVersionIn>");
					sbHeader.append("</book:ExternalAPIVersionSoapHeader>");
					
					StringSource mefHeaderSource = new StringSource(sbHeader.toString());
		            SoapHeader soapHeader = ((SoapMessage) message).getSoapHeader();
		            Transformer transformer = TransformerFactory.newInstance().newTransformer();
		            transformer.transform(mefHeaderSource, soapHeader.getResult());*/
					
			        /* 
			      
			      <book:ContextHeader>
			         <book:SendConfirmationMail>false</book:SendConfirmationMail>
			         <book:ExcludeConferenceInformation>false</book:ExcludeConferenceInformation>
			      </book:ContextHeader>*/
					SoapHeader soapHeader = ((SoapMessage)message).getSoapHeader();
					try {
						JAXBContext context = JAXBContext.newInstance( ContextHeader.class, ExternalAPIVersionSoapHeader.class);
						ObjectFactory objectFactory = new ObjectFactory();
						JAXBElement<ContextHeader> contextHeaderJE = objectFactory.createContextHeader(contextHeader);
						JAXBElement<ExternalAPIVersionSoapHeader> apiVersionHeaderJE = objectFactory.createExternalAPIVersionSoapHeader(apiVersionHeader);
						javax.xml.bind.Marshaller marshaller = context.createMarshaller();
						marshaller.marshal(apiVersionHeaderJE, soapHeader.getResult());
						marshaller.marshal( contextHeaderJE, soapHeader.getResult());
						// marshaller.marshal(request, ((SoapMessage)message).getSoapBody().getPayloadResult());
						((SaajSoapMessage)message).getSaajMessage().saveChanges();

					} catch (JAXBException e) {
						// TODO Auto-generated catch block
						  
					} catch (SOAPException e) {
						// TODO Auto-generated catch block
						  
					}

					/*try {
						SaajSoapMessage soapMessage = (SaajSoapMessage)message;
						SOAPEnvelope soapEnvelope = soapMessage.getSaajMessage().getSOAPPart().getEnvelope();
						SOAPHeaderElement soapHeaderElement = soapMessage.getSaajMessage().getSOAPHeader().addHeaderElement(soapEnvelope.createName("ExternalAPIVersionSoapHeader "));
						soapHeaderElement.addAttribute(soapEnvelope.createName("xmlns"), "http://tandberg.net/2004/02/tms/external/booking/");
						SOAPElement clientVersion = soapHeaderElement.addChildElement(soapEnvelope.createName("ClientVersionIn"));
						clientVersion.addTextNode("14");
					} catch (SOAPException e) {
						// TODO Auto-generated catch block
						  
					}*/
					
					/*SOAPMessage soapMessage = ((SaajSoapMessage)message).getSaajMessage();
					try {
						SOAPHeader header = soapMessage.getSOAPHeader();
						SOAPElement apiVersion = header.addChildElement(new QName("http://tandberg.net/2004/02/tms/external/booking/", "ExternalAPIVersionSoapHeader", "ns2"));
						SOAPElement clientVersion = apiVersion.addChildElement("ClientVersionIn", "ns2");
						clientVersion.setTextContent("14");
						soapMessage.saveChanges();
					} catch (SOAPException e) {
						// TODO Auto-generated catch block
						  
					}*/
					
				}

			});
			return response.getSaveConferencesResult().getConference();
		} catch (Exception e) {
			  
		}

		return null;
	}
}
