package at.htlgkr.aems.firebase;

import java.time.Duration;

import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.client.settings.PropertiesBasedSettings;
import de.bytefish.fcmjava.model.options.FcmMessageOptions;
import de.bytefish.fcmjava.requests.notification.NotificationPayload;
import de.bytefish.fcmjava.requests.notification.NotificationUnicastMessage;
import de.bytefish.fcmjava.responses.FcmMessageResponse;

public class Trigger {

	public static boolean notify(String token, String title, String msg) {
		//new File(System.getProperty("user.home") + "/.fcmjava/fcmjava.properties").createNewFile();
		// Creates the Client using the default settings location, which is
		//System.getProperty("user.home") + "/.fcmjava/fcmjava.properties":
		PropertiesBasedSettings settings = PropertiesBasedSettings.createFromDefault();
		
		FcmClient client = new FcmClient(settings);

		// Message Options:
		FcmMessageOptions options = FcmMessageOptions.builder().setTimeToLive(Duration.ofHours(1)).build();
		
		// Send a Message:
		//TopicUnicastMessage msg = new TopicUnicastMessage(options, new Topic("news"), new PersonData("Philipp", "Wagner"));
		NotificationPayload payload = new NotificationPayload(title, msg, null, null, null, null, null, null, null, null, null, null);
		NotificationUnicastMessage notification = new NotificationUnicastMessage(options, token, payload);
		FcmMessageResponse response = client.send(notification);
		try {
			client.close();
		} catch(Exception e) {
			return false;
		}
		
		if(response.getNumberOfFailure() > 0) {
			return false;
		}
		
		return true;
	}
	
}
