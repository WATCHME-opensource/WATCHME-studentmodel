package eu.watchme.sm.helpers;

import eu.watchme.sm.models.KAF;
import eu.watchme.sm.models.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GeneralUtils {
    Logger logger = LoggerFactory.getLogger(GeneralUtils.class);

	GeneralConfigs configs = null;

	public GeneralUtils(String parent) {
	configs = new GeneralConfigs(parent);
    }

    public KAF loadKAF(String xml) {
		KAF kaf = new KAF();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xml);
			kaf = (KAF) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return kaf;
    }

    public  String removeExtension(String name){
	return name.substring(0, name.indexOf("."));
    }

	/**
     * @param url : Url endpoint
     * @param urlParameters: parameters for processing
     * @return : this function initiates POST request with URL endpoint.
     * @throws Exception
     */
    public String sendPost(String url,String urlParameters) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// Add Request Header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", configs.getUserAgent());
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		// Send POST Request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, StandardCharsets.UTF_8));
		writer.write(urlParameters);
		writer.flush();
		writer.close();
		wr.flush();
		wr.close();
		int responseCode = con.getResponseCode();
		if(responseCode==200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			in.close();

			return (response.toString());
		} else {
			logger.error("failed POST request to {} with URL parameters \" {} \" and response code {}", url, urlParameters, responseCode);
			return "";
		}
	}
}
