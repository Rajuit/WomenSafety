package csedevelopers.freaky.developers.resources;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SendSMS {

//	Session session = MyApp.getSessionfact().openSession();

    //Your authentication key
    String authkey = "132635AveyHTgB584046a9";
    //Multiple mobiles numbers separated by comma
    String mobiles;
    //Sender ID,While using route4 sender id should be 6 characters long.
    String senderId = "WMSafe";
    //Your message to send, Add URL encoding here.
    String message = "";
    //define route
    String route = "4";

    //Prepare Url
    URLConnection myURLConnection = null;
    URL myURL = null;
    BufferedReader reader = null;

    public SendSMS(String email, String message, int i) {

        mobiles = "91" + getPhoneNo(email);
        sendNow(message, mobiles);
    }

    public SendSMS(String phone, String message) {

        phone = "91" + phone;

        sendNow(message, phone);

    }


    private void sendNow(String message, String mobiles) {

        //encoding message
        String encoded_message = URLEncoder.encode(message);

        //Send SMS API
        String mainUrl = "https://control.msg91.com/api/sendhttp.php?";

        //Prepare parameter string
        StringBuilder sbPostData = new StringBuilder(mainUrl);
        sbPostData.append("authkey=" + authkey);
        sbPostData.append("&mobiles=" + mobiles);
        sbPostData.append("&message=" + encoded_message);
        sbPostData.append("&route=" + route);
        sbPostData.append("&sender=" + senderId);

        //final string
        mainUrl = sbPostData.toString();
        try {
            //prepare connection
            myURL = new URL(mainUrl);
            myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
            //reading Response
            String response;
            while ((response = reader.readLine()) != null)
                //print Response
                System.out.println(response);

            //finally close connection
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getPhoneNo(String email) {


        return "";

    }


}
