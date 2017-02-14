package eu.watchme.modules.nlp.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xz905577 on 9/16/2015.
 */
public class NetworkingUtils {
    private Logger logger = LoggerFactory.getLogger(NetworkingUtils.class);
    public String sendPost(String url,String urlParameters) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "watchme");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        if(responseCode==200){
            logger.info("Succefull POST request to {}",url);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return (response.toString());
        }
        else{
            logger.error("failed POST request to {} with URL parameters \" {} \" and response code {}",url,urlParameters,responseCode);
            return "";
        }
    }
}
