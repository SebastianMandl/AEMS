package at.htlgkr.aems.firebase;

import org.json.JSONObject;

public class Trigger {

	public static boolean notify(String token, String title, String msg) {
		
		/**
		 * POST https://fcm.googleapis.com/v1/projects/myproject-b5ae1/messages:send HTTP/1.1

			Content-Type: application/json
			Authorization: Bearer ya29.ElqKBGN2Ri_Uz...HnS_uNreA
			
			{
			  "message":{
			    "token" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
			    "notification" : {
			      "body" : "This is an FCM notification message!",
			      "title" : "FCM Message",
			      }
			   }
			}
		 */
		
		try {
//			HttpsURLConnection con = (HttpsURLConnection) new URL("link").openConnection();
//			con.setRequestMethod("POST");
//			con.setRequestProperty("content-type", "application/json");
//			con.setDoOutput(true);
//			
//			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
			JSONObject object = new JSONObject();
			JSONObject msgObj  = new JSONObject();
			JSONObject notificationObj = new JSONObject();
			msgObj.put("token", token);
			notificationObj.put("body", msg);
			notificationObj.put("title", title);			
			msgObj.put("notification", notificationObj);
			object.put("message", msgObj);
			
//			writer.write(object.toString());
//			writer.flush();
//			writer.close();
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
}
