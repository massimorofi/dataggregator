import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.tools.Csv;

public class COVID_Aggregate {

	Connection dbCon;
	String dbUrl = "jdbc:h2:mem:";

	public COVID_Aggregate() throws SQLException {
		dbCon = DriverManager.getConnection(dbUrl);
	}

	public void loadData() throws SQLException {
		Statement stm = dbCon.createStatement();
		stm.executeUpdate("CREATE TABLE \"PUBLIC\".\"DATA\"\r\n" + 
				"(\r\n" + 
				"   DATEREP date NOT NULL,\r\n" + 
				"   DAY integer NOT NULL,\r\n" + 
				"   MONTH integer NOT NULL,\r\n" + 
				"   YEAR integer NOT NULL,\r\n" + 
				"   CASES integer NOT NULL,\r\n" + 
				"   DEATHS integer NOT NULL,\r\n" + 
				"   COUNTRIESANDTERRITORIES varchar(42) NOT NULL,\r\n" + 
				"   GEOID varchar(8) NOT NULL,\r\n" + 
				"   COUNTRYTERRITORYCODE varchar(3),\r\n" + 
				"   POPDATA2018 integer,\r\n" + 
				"   CONTINENTEXP varchar(7) NOT NULL\r\n" + 
				");");
		stm.executeUpdate("INSERT INTO DATA (DATEREP,DAY,MONTH,YEAR,CASES,DEATHS,COUNTRIESANDTERRITORIES,GEOID,COUNTRYTERRITORYCODE,POPDATA2018,CONTINENTEXP) \r\n" + 
				"SELECT parsedatetime(\"DATEREP\",'d/M/y'),DAY,MONTH,YEAR,CASES,DEATHS,COUNTRIESANDTERRITORIES,GEOID,COUNTRYTERRITORYCODE,POPDATA2018,CONTINENTEXP \r\n" + 
				"FROM CSVREAD('tmp/world.csv');");
	}

	public void aggregate() throws SQLException {
		Statement stm = dbCon.createStatement();
		ResultSet rs = stm.executeQuery("select COUNTRIESANDTERRITORIES,SUM(cases) as CASES, SUM(DEATHS) as DEATHS, max(DATEREP) as DATEREP,POPDATA2018 from data group by COUNTRIESANDTERRITORIES;");
		(new Csv()).write("dist/data/world-covid.csv", rs, null);
	}

	public static void main(String[] args) {
		run();

	}

	/**
	 * 
	 */
	public static void run() {
		try {
			COVID_Aggregate agg = new COVID_Aggregate();
			agg.loadData();
			agg.aggregate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
