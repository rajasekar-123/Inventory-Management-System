package com.example.InventoryMangement.WhatsappService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.Collections;

@Service
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String from;

    @Value("${twilio.whatsapp.to}")
    private String to;

    public void sendPdf(String fileUrl) {
        Twilio.init(accountSid, authToken);

        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(to),
                        new com.twilio.type.PhoneNumber(from),
                        "📄 Here’s your invoice from Inventory Management System.")
                .setMediaUrl(Collections.singletonList(URI.create(fileUrl)))
                .create();

        System.out.println("✅ WhatsApp message sent! SID: " + message.getSid());
    }
}


