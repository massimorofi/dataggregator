
import java.net.URL;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

public class DataUpdater {

	public static void main(String[] args) {
		try {
			String httpsURL = "https://news.yahoo.com/rss/mostviewed";
			URL myUrl = new URL(httpsURL);
			HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String inputLine;
			BufferedWriter f = new BufferedWriter(new FileWriter("dist/data/RSS_News.xml"));
			while ((inputLine = br.readLine()) != null) {
				System.out.println(inputLine);
				f.write(inputLine);
			}
			br.close();
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}