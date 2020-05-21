import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.tools.Csv;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Splitter;

public class HTMLRemoteLoader {

	public static void main(String[] args) {
		run();

	}

	/**
	 * run the data collection and aggregation
	 */
	public static void run() {
		try {
			// Fetch Data from website
			htmlToCsv();
			// Aggregate data and save
			aggregate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void aggregate() throws SQLException {
		Connection dbCon = DriverManager.getConnection("jdbc:h2:mem:");
		Statement stm = dbCon.createStatement();
		stm.executeUpdate("CREATE TABLE covid(\r\n" + "   ORD INTEGER\r\n" + "  ,Country           VARCHAR(30)\r\n"
				+ "  ,Total_Cases       INTEGER \r\n" + "  ,New_Cases         INTEGER \r\n"
				+ "  ,Total_Deaths      INTEGER \r\n" + "  ,New_Deaths        INTEGER \r\n"
				+ "  ,Total_Recovered   INTEGER \r\n" + "  ,Active_Cases      INTEGER \r\n"
				+ "  ,Serious_Critical  INTEGER \r\n" + "  ,Tot_Cases_1M_pop  VARCHAR(30) \r\n"
				+ "  ,Deaths_1M_pop     VARCHAR(30) \r\n" + "  ,Total_Tests       INTEGER \r\n"
				+ "  ,Tests_1M_pop      INTEGER \r\n" + "  ,Population          INTEGER\r\n"
				+ "  ,Continent         VARCHAR(30) \r\n" + ");");
		stm.executeUpdate(
				"INSERT INTO \"PUBLIC\".\"COVID\" (ORD,COUNTRY,TOTAL_CASES,NEW_CASES,TOTAL_DEATHS,NEW_DEATHS,TOTAL_RECOVERED,ACTIVE_CASES,SERIOUS_CRITICAL,TOT_CASES_1M_POP,DEATHS_1M_POP,TOTAL_TESTS,TESTS_1M_POP,POPULATION,CONTINENT) \r\n"
						+ "SELECT ORD,COUNTRY,TOTAL_CASES,NEW_CASES,TOTAL_DEATHS,NEW_DEATHS,TOTAL_RECOVERED,ACTIVE_CASES,SERIOUS_CRITICAL,TOT_CASES_1M_POP,DEATHS_1M_POP,TOTAL_TESTS,TESTS_1M_POP,POPULATION,CONTINENT \r\n"
						+ "FROM CSVREAD('tmp/covid_world_today.csv','ORD;COUNTRY;TOTAL_CASES;NEW_CASES;TOTAL_DEATHS;NEW_DEATHS;TOTAL_RECOVERED;ACTIVE_CASES;SERIOUS_CRITICAL;TOT_CASES_1M_POP;DEATHS_1M_POP;TOTAL_TESTS;TESTS_1M_POP;POPULATION;CONTINENT','fieldSeparator=;');");
		// AGGREGATE DATA
		ResultSet rs = stm.executeQuery("SELECT *, CURRENT_DATE() AS DATE FROM COVID \r\n"
				+ "WHERE COUNTRY IN ('Italy','Germany','Spain','France', 'UK','USA','Russia','Sweden','Japan','Brazil') \r\n"
				+ "ORDER BY TESTS_1M_POP DESC;");
		(new Csv()).write("dist/data/covid-stats.csv", rs, null);
	}

	/**
	 * get the data from the table and save it to CSV
	 */
	public static void htmlToCsv() {
		try {
			RandomAccessFile f = new RandomAccessFile("tmp/covid_world_today.csv", "rw");
			Elements doc = getHtmlDoc().select("#main_table_countries_today");
			// Tables 0=today, 1=sumary today; 2= total; 3= yesterday
			Element table = doc.select("tbody").get(0);
			Elements rows = table.select("tr");
			Elements ths = doc.select("th");

			/*
			 * String thstr = ""; int eol = ths.size() - 1; System.out.println(eol); int
			 * counter = 0; for (Element th : ths) { if (counter < eol) { thstr += th.text()
			 * + ";"; } else { thstr += th.text() + "\n"; } counter++; } thstr =
			 * thstr.trim().replaceAll(",", "_").replaceAll("/", "_").replaceAll("", "_") +
			 * "\n";
			 * 
			 * f.write(thstr.getBytes());
			 */
			System.out.println("-->" + doc.select("tbody").size());
			for (Element row : rows) {
				Elements tds = row.select("td");
				String line = "";
				int eol = tds.size() - 1;
				int counter = 0;
				for (Element td : tds) {
					if (counter < eol) {
						line += td.text() + ";"; // --> This will print them individually
					} else {
						line += td.text() + "\n";
					}
					counter++;
				}
				line = line.replaceAll(",", "").replaceAll("N/A", "").replaceAll("\\+", "");
				f.write(line.getBytes()); // --> This will print everything
				// in the row
			}
			f.close();
			// System.out.println(table);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public static void getHTMLData() {
		try {

			Document doc = getHtmlDoc();
			String result = doc.select("#main_table_countries_today").toString();
			RandomAccessFile f = new RandomAccessFile("tmp/covid_worldometer.html", "rw");
			int l = result.length() / 64000;
			Iterable<String> toSave = Splitter.fixedLength(l).split(result);
			for (String s : toSave) {
				f.write(s.getBytes());
			}
			f.close();
		} catch (Exception ioe) {
			System.out.println("Exception: " + ioe);
		}
	}

	/**
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	public static Document getHtmlDoc() throws MalformedURLException, IOException, ProtocolException {
		URL url = new URL("https://www.worldometers.info/coronavirus/");
		// Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy-here",
		// 8080));
		HttpURLConnection uc = (HttpURLConnection) url.openConnection();
		uc.setRequestProperty("User-Agent", "Mozilla/5.0");
		uc.setRequestProperty("Content-Language", "en-US");
		uc.setRequestMethod("GET");
		uc.connect();
		Document doc = Jsoup.parse(uc.getInputStream(), "UTF-8", "https://www.worldometers.info/coronavirus/");
		return doc;
	}

}
