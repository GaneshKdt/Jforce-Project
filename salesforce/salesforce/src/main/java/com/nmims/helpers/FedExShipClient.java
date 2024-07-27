package com.nmims.helpers;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.springframework.oxm.Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.nmims.webservice.fedex.ship.ProcessShipmentReply;
import com.nmims.webservice.fedex.ship.ProcessShipmentRequest;

public class FedExShipClient {
	private WebServiceTemplate wsTemplate;

	private Marshaller marshaller; 

	public void setWsTemplate(WebServiceTemplate wsTemplate) {
		this.wsTemplate = wsTemplate;
		HttpComponentsMessageSender sender = (HttpComponentsMessageSender) wsTemplate.getMessageSenders()[0];
		
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}
	
	public ProcessShipmentReply createShipment(ProcessShipmentRequest request){
		ProcessShipmentReply response = null;
		JAXBContext jaxbContext = null;
		try {
			
			jaxbContext = JAXBContext.newInstance(ProcessShipmentRequest.class);
			jaxbContext.createMarshaller().marshal(request, System.out);
			
			System.out.println("wsTemplate = "+wsTemplate);
			System.out.println("request = "+request);
			JAXBElement<ProcessShipmentReply> resp = (JAXBElement<ProcessShipmentReply>)wsTemplate.marshalSendAndReceive(request);
			//ProcessShipmentReply response = (ProcessShipmentReply)wsTemplate.marshalSendAndReceive(request);
			response = resp.getValue();
			
			
			return response;
		} catch (Exception e) {
			System.out.println("response "+response);
			try {
				jaxbContext = JAXBContext.newInstance(ProcessShipmentReply.class);
				jaxbContext.createMarshaller().marshal(response, System.out);
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			e.printStackTrace();
			
		}
		return null;
		
	}
	
}
