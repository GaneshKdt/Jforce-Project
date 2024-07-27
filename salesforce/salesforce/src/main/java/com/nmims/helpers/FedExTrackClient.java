package com.nmims.helpers;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;

import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.nmims.webservice.fedex.track.TrackReply;
import com.nmims.webservice.fedex.track.TrackRequest;

@Service("fedExTrackClient")
public class FedExTrackClient {
	private WebServiceTemplate wsTemplate;

	private Marshaller marshaller; 

	public void setWsTemplate(WebServiceTemplate wsTemplate) {
		this.wsTemplate = wsTemplate;
		HttpComponentsMessageSender sender = (HttpComponentsMessageSender) wsTemplate.getMessageSenders()[0];
		
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}
	
	public TrackReply trackShipment(TrackRequest request){
		try {
			
			JAXBContext jaxbContext = JAXBContext.newInstance(TrackRequest.class);
			//jaxbContext.createMarshaller().marshal(request, System.out);
			
			//System.out.println("wsTemplate = "+wsTemplate);
			//System.out.println("request = "+request);
			JAXBElement<TrackReply> resp = (JAXBElement<TrackReply>)wsTemplate.marshalSendAndReceive(request);
			//ProcessShipmentReply response = (ProcessShipmentReply)wsTemplate.marshalSendAndReceive(request);
			TrackReply response = resp.getValue();
			//System.out.println("response "+response);
			
			jaxbContext = JAXBContext.newInstance(TrackReply.class);
			//jaxbContext.createMarshaller().marshal(response, System.out);

			
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
}
