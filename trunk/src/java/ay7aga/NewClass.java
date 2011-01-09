/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ay7aga;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

/**
 *
 * @author Activedd2
 */
public class NewClass {

    public static void main(String[] args) {
        JID jid = new JID("mohamedazouz@gmail.com");
        String msgBody = "Someone has sent you a gift on Example.com. To view: http://example.com/gifts/";
        Message msg = new MessageBuilder().withRecipientJids(jid).withBody(msgBody).build();

        boolean messageSent = false;
        XMPPService xmpp = XMPPServiceFactory.getXMPPService();
       // if (xmpp.getPresence(jid).isAvailable()) {
            SendResponse status = xmpp.sendMessage(msg);
            messageSent = (status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS);
       // }

        if (!messageSent) {
            System.out.println("hi");
            // Send an email message instead...
        }
    }
}
