package org.gdocjdbc.test;

import junit.framework.TestCase;

import java.sql.*;
import java.util.Properties;

public class Tests extends TestCase {

	String username = null;
	String password = null;
	
	
	
	
	
	@Override
	protected void setUp() throws Exception {
		
		username = System.getProperty("google.user");
		password = System.getProperty("google.password");
		
		if(username == null) {
			System.out.println("Google Doc account info missing: You have to set the gdocjdbc.username system property (-Dgdocjdbc.username=myname@gdocjdbc.org)");
		}
		
		if(password == null) {
			System.out.println("Google Doc account info missing: You have to set the gdocjdbc.password system property (-Dgdocjdbc.password=mypassword)");
		}
		
	}

	
	/*
	 * This is the basic test you'll want to run if you uploaded the test spreadsheet included in this project
	 */
	public void testSimpsonsSelect() {

		
		try {
			Class.forName("org.gdocjdbc.jdbcDriver").newInstance();
			//create a connection using the default cache time
			Connection con = DriverManager.getConnection("jdbc:gdocjdbc", username, password);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM simpsonscharacters_Sheet1 where VOICEDBY='Dan Castellaneta'");
			
			
			while (rs.next()) {
				String s = rs.getString("CHARACTER");
				String s2 = rs.getString("FIRSTAPPEARANCE");
				String s3 = rs.getString("DESCRIPTIONROLE");
				System.out.println("CHARACTER = " + s + ", FIRSTAPPEARANCE=" + s2+ " AND DESCRIPTIONROLE=" + s3);
				}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
		
	}
	
	
	public void testGoogleDocJDBCCache() {
		try {
			//Driver driver = 
			try {
				Class.forName("org.gdocjdbc.jdbcDriver").newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			//Connection con = 
			
			
			while(true) {
				Properties dbProps = new Properties();
				dbProps.setProperty("user", username);
				dbProps.setProperty("password", password);
				dbProps.setProperty("cacheExpire", "10000");
				getResults(DriverManager.getConnection("jdbc:gdocjdbc", dbProps));
				Thread.sleep(1000 * 10);
			}

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		
	}
	
	public void testCreateTables() {
		
		
		try {
			//Driver driver = 
			try {
				Class.forName("org.gdocjdbc.jdbcDriver").newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			//Connection con = 
			
			
		
				Properties dbProps = new Properties();
				dbProps.setProperty("user", username);
				dbProps.setProperty("password", password);
				dbProps.setProperty("cacheExpire", "10000");
				getResults(DriverManager.getConnection("jdbc:gdocjdbc", dbProps));
				
				

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	private static void getResults(Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		//ResultSet rs = stmt.executeQuery("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_TYPE='TABLE'");
		ResultSet rs = stmt.executeQuery("SELECT * FROM simpsonscharacters_Sheet1 where character='Homer Simpson'");
		

		while (rs.next()) {
//			String s = rs.getString("TABLE_NAME");
//			String s2 = rs.getString("TABLE_TYPE");
			String s = rs.getString("VOICEDBY");
			String s2 = rs.getString("FIRSTAPPEARANCE");
			//System.out.println("Name = " + s + " type=" + s2 + " rowcount: " + getRowCount(s, con));
			System.out.println("VOICEDBY = " + s + " AND FIRSTAPPEARANCE=" + s2);
			
			}
	}
	
	
	public void testbreak() {
		String myString = "pickles.test@gmail.com";
		String myString2 = myString.toLowerCase();
	}
	
	private Integer getRowCount(String tablename, Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT count(*) as MYCOUNT FROM " + tablename);
		Integer count = null;
		
		while (rs.next()) {
			count = rs.getInt("MYCOUNT");
		}
		
		return count;
	}
	
}
