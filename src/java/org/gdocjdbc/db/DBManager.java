package org.gdocjdbc.db;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hsqldb.Token;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;


public class DBManager {

	  private static SpreadsheetService spreadsheetsService= null;
	  
	  public static final String SPREADSHEETS_SERVICE_NAME = "wise";
	  private Connection con = null;
	  private Connection infoConnection = null;
	  private static final String DEFAULT_COLUMN_TYPE="VARCHAR(100)";
	  private static Logger log = Logger.getLogger(DBManager.class.getName());
	  String infoDBUrl = "";
	  String thisInstanceDBUrl = "";
	  String username = "";
	  String password = "";
	  String userDBName = "";
	  boolean created = false;
	  public static Integer DEFAULT_CACHE_TIME = 30 * 60;
	  
	  
	  public DBManager(String username, String password, String dbURL) throws SQLException {
		  init(username, password, dbURL, DEFAULT_CACHE_TIME);
		  log.info("Using the DB URL : " + thisInstanceDBUrl);
	  }
	  

	  public DBManager(String username, String password) throws SQLException {
		  init(username, password, null, DEFAULT_CACHE_TIME);
		  log.info("Using the DB URL : " + thisInstanceDBUrl);
	  }

	  
    public DBManager(Properties dbProbs) throws SQLException {
    	Integer cacheTime = DEFAULT_CACHE_TIME;
    	if(dbProbs.getProperty("cacheExpire") != null) {
    		cacheTime =Integer.parseInt(dbProbs.getProperty("cacheExpire"));
    	}
    	init(dbProbs.getProperty("user"), dbProbs.getProperty("password"), null, cacheTime);
    }
    
	  
	private void init(String username, String password, String dbURL, Integer cacheExpireTime) throws SQLException {
		  this.username = username;
		  this.password = password;
		  this.userDBName = scrubUserName(username);
		  
		  infoDBUrl = "jdbc:hsqldb:mem:"+userDBName+"_info";
		  infoConnection = DriverManager.getConnection(infoDBUrl, "sa", "");
		  

			  if(!infoTableExists()) {
				  //must be the first time this driver is being used in this JVM
				  //create table keeping track of the DB information
					createInfoTable();
					thisInstanceDBUrl = getCurrentDBUrl();
					con = DriverManager.getConnection(thisInstanceDBUrl, "sa", "");
					
				  //Creating the database for the first time.
					createDatabase();
					DatabaseCacheControl dbCacheControl = new DatabaseCacheControl(username, password, cacheExpireTime);
					dbCacheControl.startRecachingService();
			  } else {
				  if(dbURL != null) {
					  thisInstanceDBUrl = dbURL;
					  con = DriverManager.getConnection(dbURL, "sa", "");
				  } else {
					  thisInstanceDBUrl = getCurrentDBUrl();
					  con = DriverManager.getConnection(thisInstanceDBUrl, "sa", "");
				  }
			  }
			
			
	}
    public String getCurrentDBUrl() throws SQLException {
    	Statement stmt = null;
		stmt = infoConnection.createStatement();
		String select = "SELECT PROPVALUE FROM PICKLES_TABLE_INFO where PROPNAME='CURRENT_DB_URL'";
		ResultSet rs = stmt.executeQuery(select);
		String dbURL = null;
		
		while(rs.next()) {
			dbURL =rs.getString("PROPVALUE");
		}
		stmt.close();
		
		return dbURL;
    }
	 
    
    public void switchActiveDatabase(String dbURL) throws SQLException {
    	Statement stmt = null;
		stmt = infoConnection.createStatement();
		String sql = "UPDATE PICKLES_TABLE_INFO set PROPVALUE='"+dbURL+"' where PROPNAME='CURRENT_DB_URL'";
		Integer num = stmt.executeUpdate(sql);
		
		if (stmt.executeUpdate(sql) < 1) {
			log.severe("The update to switch to the new DB did not take place");
		} else {
			log.info("Updated table with URL: "+ dbURL);
		}

		stmt.close();
		
		//clean out old database tables
    }
    
    public void deleteDatabaseTables() throws SQLException {
    	log.info(thisInstanceDBUrl + " : deleting tables");
    	//there should be an easier way to drop the schema
    	Statement stmt = null;
		stmt = con.createStatement();
		String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_TYPE='TABLE'";
    	
		ResultSet rs = stmt.executeQuery(sql);
		
		while(rs.next()) {
			Statement stmt2 = con.createStatement();
			String tableName = rs.getString("TABLE_NAME");
			log.info("dropping table name : " + tableName);
			String sql2 = "DROP TABLE " + tableName +";";
			stmt2.executeUpdate(sql2);
		}
		
    }
    
	public void createDatabase() throws SQLException {
			  log.log(Level.INFO, "Local Database of Google Doc data doesn't exist, creating it...");
			  setupGoogleDocConnection(username,password);
			  List <String> urls = new ArrayList<String>();
			  
			
			  
			    SpreadsheetFeed feed = null;
				try {
					feed = spreadsheetsService.getFeed(FeedURLFactory.getDefault().getSpreadsheetsFeedUrl(),
					        SpreadsheetFeed.class);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
  			  log.info("found " + feed.getEntries().size() + " spreadsheet(s) in your google doc account");
			  
  			  
  			  for(SpreadsheetEntry sse:feed.getEntries()) {
  				populateDBFromSpeadsheet(sse);
  			  }
  			  
  			  Statement stmt = infoConnection.createStatement();
  			  stmt.executeUpdate("UPDATE PICKLES_TABLE_INFO SET MODIFIED_DATE=now() where PROPNAME='CURRENT_DB_URL'");
  			  stmt.close();
  			  
  			  logDatabaseTables();
  			  
	  }
	  
	private boolean infoTableExists() throws SQLException {
		Statement stmt = null;
		stmt = infoConnection.createStatement();
		String select = "SELECT COUNT(*) as MYCOUNT FROM INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_NAME=\'PICKLES_TABLE_INFO\'";
		boolean tableExists = false;
		ResultSet rs = stmt.executeQuery(select);
		
		while(rs.next()) {
			Integer count =rs.getInt("MYCOUNT");
			tableExists = count > 0;
		}
		
		return tableExists;
	}
	

	
	private void createInfoTable() throws SQLException {
		log.info("Creating info database for the first time");
		StringBuffer createString = new StringBuffer();
		createString.append("create table PICKLES_TABLE_INFO (");
		createString.append("PROPNAME VARCHAR(100),");
		createString.append("PROPVALUE VARCHAR(100),");				
		createString.append("MODIFIED_DATE DATE");
		createString.append(");");
				


		Statement stmt = null;
		stmt = infoConnection.createStatement();
		stmt.executeUpdate(createString+"");
		stmt.close();
		
		stmt = infoConnection.createStatement();
		stmt.executeUpdate("INSERT INTO PICKLES_TABLE_INFO (PROPNAME, MODIFIED_DATE) VALUES('INFO_CREATE_DATE', SYSDATE)");
		stmt.close();
		
		stmt = infoConnection.createStatement();
		stmt.executeUpdate("INSERT INTO PICKLES_TABLE_INFO (PROPNAME, PROPVALUE, MODIFIED_DATE) VALUES('CURRENT_DB_URL','jdbc:hsqldb:mem:"+userDBName+"_0', SYSDATE)");
		stmt.close();
	}

	  
	  
	public void setupGoogleDocConnection(String username, String password) {
		 spreadsheetsService = new SpreadsheetService("NikeProductFinder");
		    

		    try {

			    spreadsheetsService.setUserCredentials(username, password);
			
			} catch (AuthenticationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		
	}
	

	
private Map<String, String> createTable(String tablename, ListEntry entry) throws SQLException {
	StringBuffer createString = new StringBuffer();
	createString.append("create table "+tablename+" (");
	String colType = "";
	
	Map<String, String> columnNames = new HashMap<String, String>();
	
	try {
		Class.forName("org.hsqldb.jdbcDriver" );
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	Statement stmt = null;


		for (String tag : entry.getCustomElements().getTags()) {
			if(tag != null && tag.length() > 0) {
				//to support overriding later on
				colType = "null";
				columnNames.put(tag.toUpperCase(), null);
				createString.append(scrubReservedWords(tag.toUpperCase()) +" "+((colType.equals("null"))?DEFAULT_COLUMN_TYPE:colType)+", ");
				if(!tag.toUpperCase().equals(scrubReservedWords(tag.toUpperCase()))) {
					log.log(Level.WARNING, "Column name : " + tag.toUpperCase() + " contains a resereved word and was changed to :" + scrubReservedWords(tag.toUpperCase()));
				}
			}
	     }
    createString.delete(createString.length()-2, createString.length()-1);

	createString.append(");");
	
	stmt = con.createStatement();
	stmt.executeUpdate(createString+"");
	stmt.close();
	
	return columnNames;
	
}


private String scrubReservedWords(String name) {
	String newName = name+"_COL";
	if(Token.isKeyword(name)) {
		return newName;
	} else {
		return name;
	}
}

private void populateDBFromSpeadsheet(SpreadsheetEntry sse) throws SQLException {
	
		try {
			List<WorksheetEntry> worksheetList = sse.getWorksheets();
			
			String documentName = scrubTableName(sse.getTitle().getPlainText());
			
			URL columnListFeedUrl = worksheetList.get(0).getListFeedUrl();
		    ListFeed columnFeed = spreadsheetsService.getFeed(columnListFeedUrl, ListFeed.class);
			
		    
			for(WorksheetEntry worksheet : worksheetList) {
			   
				
				String workSheetTitle = scrubTableName(worksheet.getTitle().getPlainText());
				try {
					URL listFeedUrl = worksheet.getListFeedUrl();
				    ListFeed feed = spreadsheetsService.getFeed(listFeedUrl, ListFeed.class);
				    if(feed.getEntries().size() < 1) {
				    	log.info("Worksheet: " + workSheetTitle + " from document: "+documentName + " is empty so skipping it.");
				    	break;
				    }
				    Map<String, String> columnNames = createTable(documentName+"_"+workSheetTitle,feed.getEntries().get(0));
				    log.info(thisInstanceDBUrl+": populating table : "+documentName+"_"+workSheetTitle);
				    for (ListEntry entry : feed.getEntries()) {
				    	insertEntry(documentName, workSheetTitle, entry, columnNames);
				    }
				    
				} catch (SQLException sqe) {
					log.log(Level.SEVERE, "Problem creating table : " + documentName+"_"+workSheetTitle, sqe);
				}
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}




	private void insertEntry(String tablePrefix, String worksheetName, ListEntry entry, Map<String, String> columnNames) throws SQLException {
		
		String colNames = "";
		String values = "";
		
		String tempStr = "";
		
		for (String tag : entry.getCustomElements().getTags()) {
			//making sure no columns are set within the body of the spreadsheet.
			if(columnNames.containsKey(tag.toUpperCase())) {
				tempStr = entry.getCustomElements().getValue(tag);
				tempStr = (tempStr==null)?"":tempStr;
				colNames += scrubReservedWords(tag.toUpperCase())+", ";
				values += "\'"+tempStr.replaceAll("\'", "\'\'")+"\', ";
			}
	      }
		colNames = colNames.substring(0, colNames.length()-2);
		values = values.substring(0, values.length()-2);	
		

		
		String insertString = "INSERT INTO "+tablePrefix+"_"+worksheetName+" ("+colNames+") VALUES ("+ values+");";
		
		log.fine(insertString);
	
		Statement stmt = con.createStatement();
   		stmt = con.createStatement();
   		stmt.executeUpdate(insertString);
   		stmt.close();
	}

	
	private String scrubTableName(String origName) {
		return origName.replaceAll("[^a-zA-Z0-9]", "");

	}

	public static String scrubUserName(String origName) {
		return origName.replaceAll("[^a-zA-Z0-9]", "");
	}
	
	public String getNextDBUrl() throws SQLException {
		String url = getCurrentDBUrl();
		String urlNum = url.substring(url.lastIndexOf('_')+1, url.length());
		Integer num = Integer.parseInt(urlNum);
		num = num == 0?1:0;
		
		return url.substring(0, url.lastIndexOf('_')) + "_" + num;
	}
	
	public void logDatabaseTables() throws SQLException {
    	Statement stmt = null;
		stmt = con.createStatement();
		String sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME NOT LIKE 'SYSTEM_%'";
    	
		ResultSet rs = stmt.executeQuery(sql);
		Map<String, List> tableMap = new HashMap<String, List>();
		
		
		while(rs.next()) {
			String tableName = rs.getString("TABLE_NAME");
			String columnName = rs.getString("COLUMN_NAME");
			
			if(tableMap.containsKey(tableName)) {
				List columnList = tableMap.get(tableName);
				columnList.add(columnName);
			} else {
				List columnList = new ArrayList();
				columnList.add(columnName);
				tableMap.put(tableName, columnList);
			}
		}
		//print out tables
		log.info("The tables created from the google doc spreadsheet are :" );
		
		for(Iterator<String> it = tableMap.keySet().iterator();it.hasNext();) {
			String tableName = it.next();
			List columnList = tableMap.get(tableName);
			log.info("Tablename: "+tableName+ "    Columns : " + columnList);
		}
	}
}
