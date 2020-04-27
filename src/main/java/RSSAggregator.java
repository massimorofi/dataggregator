import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RSSAggregator {

	/**
	 * Aggregate RSS feeds
	 */
	public static void run() {
		try {
			String httpsURL = "https://news.yahoo.com/rss/latest";
			URL myUrl = new URL(httpsURL);
			HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String inputLine;
			BufferedWriter f = new BufferedWriter(new FileWriter("dist/data/RSS_News.xml"));
			while ((inputLine = br.readLine()) != null) {
				//System.out.println(inputLine);
				f.write(inputLine);
			}
			br.close();
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
