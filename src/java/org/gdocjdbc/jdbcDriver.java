package org.gdocjdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.gdocjdbc.db.DBManager;
import org.hsqldb.Trace;
import org.hsqldb.jdbc.jdbcConnection;
import org.hsqldb.persist.HsqlProperties;



public class jdbcDriver extends org.hsqldb.jdbcDriver implements java.sql.Driver {


	static {
		try {
			java.sql.DriverManager.registerDriver(new jdbcDriver());
		} catch (SQLException E) {
			throw new RuntimeException("Can't register driver!");
		}
	}
	
	public jdbcDriver() throws SQLException {
		// Required for Class.forName().newInstance()
	}
	
	@Override
	public Connection connect(String url, Properties info) throws SQLException {

		if(!"jdbc:gdocjdbc".equals(url)) {
			throw new SQLException("For gdocjdbc currently only the URL of : \"jdbc:gdocjdbc\" is supported");
		}
		
		
		DBManager dbCreator = new DBManager(info);
		
		//creating DB for the first time
		//dbCreator.createDatabase();
		
		info.setProperty("user", "sa");
		info.setProperty("password", "");
		//String hsqlDBURL = url+":mem:"+ DBManager.scrubUserName(googleDocUsername);
		String hsqlDBURL = dbCreator.getCurrentDBUrl();
		
		return getConnection(hsqlDBURL, info);
	}

	 public static Connection getConnection(String url,
             Properties info)
             throws SQLException {

		HsqlProperties props = org.hsqldb.DatabaseURL.parseURL(url, true);
		
		if (props == null) {
		
		// supposed to be an HSQLDB driver url but has errors
		throw new SQLException(
		Trace.getMessage(Trace.INVALID_JDBC_ARGUMENT));
		} else if (props.isEmpty()) {
		
		// is not an HSQLDB driver url
		return null;
		}
		
		props.addProperties(info);
			
		return new jdbcConnection(props);
	}
	


	
	 public boolean acceptsURL(String url) {
	    	String prefix = "jdbc:gdocjdbc";
	    	
	        return url != null
	        && url.regionMatches(true, 0,prefix, 0,
	        		prefix.length());        
	    }
	
	
}
