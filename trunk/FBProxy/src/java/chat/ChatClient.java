/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import com.google.code.facebookapi.FacebookException;
import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import com.google.code.facebookapi.FacebookJsonRestClient;
import com.google.code.facebookapi.ProfileField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Activedd2
 */
public class ChatClient implements MessageListener {

    private ArrayList<FriendBuddy> list = new ArrayList<FriendBuddy>();
    private XMPPConnection connection;
    private SecurityMode securityMode = SecurityMode.enabled;
    private boolean isSaslAuthenticationEnabled = true;
    private boolean isCompressionEnabled = false;
    private boolean isReconnectionAllowed = false;
    private int port = 5222;
    String host = "";
    private String domain = "chat.facebook.com";
    private String apiKey = "76f98c6f348e8d27ed504ae74da69cea";
    private String apiSecret = "c4cc0e40e6f8f17362685640a9b0adb4";
    private String resource = "Facebook Group Chat";
    private String rcvedMessage = "";
    public static boolean connected = false;
    FacebookJsonRestClient facebook;
    HashMap<Long, String> profilepictures = new HashMap<Long, String>();

    public void login(String token) throws XMPPException, InterruptedException, FacebookException {

        SASLAuthentication.registerSASLMechanism("X-FACEBOOK-PLATFORM",
                FacebookConnectSASLMechanism.class);
        SASLAuthentication.supportSASLMechanism("X-FACEBOOK-PLATFORM", 0);

        ConnectionConfiguration config = null;

        config = new ConnectionConfiguration(domain, port);

        config.setSecurityMode(securityMode);
        config.setSASLAuthenticationEnabled(isSaslAuthenticationEnabled);
        config.setCompressionEnabled(isCompressionEnabled);
        config.setReconnectionAllowed(isReconnectionAllowed);

        connection = new XMPPConnection(config);

        try {
            connection.connect();
        } catch (XMPPException e) {
            System.out.println("error");
        }
        facebook = new FacebookJsonRestClient(apiKey, apiSecret);
        String FB_SESSION_KEY = facebook.auth_getSession(token);


        String username = apiKey + "|" + FB_SESSION_KEY;
        try {
            connection.login(apiKey + "|" + FB_SESSION_KEY, apiSecret, resource);
            Presence packet = new Presence(Presence.Type.available);
            connection.sendPacket(packet);
            GetprofilesPictures();
        } catch (XMPPException e) {
            System.out.println("error2");
        }
    }

    void GetprofilesPictures() {
        try {
            ArrayList<Long> friendsID = new ArrayList<Long>();
            JSONArray friendsid = facebook.friends_get();
            for (int i = 0; i < friendsid.length(); i++) {
                Object object = friendsid.get(i);
                friendsID.add(new Long(object.toString()));
            }
            ArrayList<ProfileField> pf = new ArrayList<ProfileField>();
            pf.add(ProfileField.PIC);
            friendsid = facebook.users_getInfo(friendsID, pf);
            for (int i = 0; i < friendsid.length(); i++) {
                JSONObject jSONObject = friendsid.getJSONObject(i);
                String pic = (String) jSONObject.get("pic");
                String id = ((Object) jSONObject.get("uid")).toString();
                profilepictures.put(new Long(id), pic);
            }

        } catch (Exception ex) {
            System.out.println(ex.toString());

        }

    }

    String getprofilepic(long id) {
        String pic = profilepictures.get(id);
        return pic;
    }

    public void loginkaman() throws XMPPException, InterruptedException, FacebookException {
        //   SASLAuthentication.registerSASLMechanism("X-FACEBOOK-PLATFORM",
        // FacebookConnectSASLMechanism.class);
        //SASLAuthentication.supportSASLMechanism("X-FACEBOOK-PLATFORM", 0);
        ConnectionConfiguration config = null;
        config = new ConnectionConfiguration(host, port);

        config.setSecurityMode(securityMode);
        config.setSASLAuthenticationEnabled(isSaslAuthenticationEnabled);
        config.setCompressionEnabled(isCompressionEnabled);
        config.setReconnectionAllowed(isReconnectionAllowed);

        connection = new XMPPConnection(config);



        String FB_SESSION_KEY = "7f605feabdb3e1c7eb1efd43-1198560721";
        try {
            connection.connect();

            FacebookJsonRestClient facebook = new FacebookJsonRestClient(apiKey, apiSecret, FB_SESSION_KEY);
            facebook.isDesktop();
            facebook.users_setStatus("ya moshel wel application  dah y5las b2aa :D");

            String username = apiKey + "|" + FB_SESSION_KEY;
            connection.login(username, apiSecret);
            /*
             * XMPPError connecting to :5222.: remote-server-error(502) XMPPError connecting to :5222.
            -- caused by: java.net.ConnectException: Connection refused
             */
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void loginagain(String username, String pass) {
        SASLAuthentication.registerSASLMechanism("DIGEST-MD5", MySASLDigestMD5Mechanism.class);

        ConnectionConfiguration config = new ConnectionConfiguration("chat.facebook.com", 5222);
        //config.setSASLAuthenticationEnabled(true);

        connection = new XMPPConnection(config);
        try {
            connection.connect();
            connection.login(username, pass);
        } catch (Exception e) {
            System.out.println("hi");
        }

    }

    public void sendMessage(String message, String to) throws XMPPException {
        Chat chat = connection.getChatManager().createChat(to, this);
        chat.sendMessage(message);
    }

    public void displayBuddyList() {
        list.clear();
        Roster roster = connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();

        for (RosterEntry r : entries) {
            Presence presence = roster.getPresence(r.getUser());
            FriendBuddy friend = new FriendBuddy();
            friend.setId(r.getUser());
            try {
                this.sendMessage("", r.getUser());
            } catch (XMPPException ex) {
                Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            friend.setName(r.getName());
            String temp = r.getUser();
            String id = (String) temp.subSequence(1, temp.lastIndexOf("@"));
            friend.setPic(getprofilepic(new Long(id)));
            if (presence.getType() == Presence.Type.available) {
                friend.setStaus("1");

            } else {
                friend.setStaus("0");
            }
            list.add(friend);
        }

    }

    public void disconnect() {
        connection.disconnect();
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        System.out.println(message.getType().name());
        if (message.getType() == Message.Type.chat && message.getBody() != null) {

            System.out.println("xml:" + message.toXML());
            System.out.println(chat.getParticipant() + " says: " + message.getBody() + " to :" + connection.getUser());
            CreatJsonFile c = new CreatJsonFile();
            try {

                c.createJsonFile(chat.getParticipant(), message.getBody());
            } catch (Exception ex) {
                Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public static void main(String args[]) throws XMPPException, IOException, InterruptedException, FacebookException {

        ChatClient c = new ChatClient();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String msg;
        String sessionKey = "7f605feabdb3e1c7eb1efd43-1198560721";
        //c.loginagain("azouz", "7117340");
        c.loginkaman();
        c.displayBuddyList();
        System.out.println("-----");
        System.out.println("Enter your message in the console.");
        System.out.println("-----\n");
        for (int i = 0; i < c.getList().size(); i++) {
            System.out.println(c.getList().get(i).getName());
        }

        /*while (!(msg = br.readLine()).equals("bye")) {

        c.sendMessage(msg, "-100001513410529@chat.facebook.com");
        }*/
        c.disconnect();
    }

    public void signout() {
        Presence packet = new Presence(Presence.Type.unavailable);
        connection.sendPacket(packet);
        connection.disconnect();
        ChatClient.connected = false;
    }

    /**
     * @return the list
     */
    public ArrayList<FriendBuddy> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(ArrayList<FriendBuddy> list) {
        this.list = list;
    }

    /**
     * @return the rcvedMessage
     */
    public String getRcvedMessage() {
        return rcvedMessage;
    }

    /**
     * @param rcvedMessage the rcvedMessage to set
     */
    public void setRcvedMessage(String rcvedMessage) {
        this.rcvedMessage = rcvedMessage;
    }
}
