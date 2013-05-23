package org.gdocjdbc.db;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseCacheControl {

	String username = "";
	String password = "";
	Integer period = null;
	private static Logger log = Logger.getLogger(DatabaseCacheControl.class.getName());
	
	private final ScheduledExecutorService scheduler = 
	       Executors.newScheduledThreadPool(1);

	public DatabaseCacheControl(String username, String password, Integer period) {
		this.username = username;
		this.password = password;
		this.period = period;
	}
	
	    public void startRecachingService() {
	    	log.info("Starting the recaching service at an interval of " + period + " seconds.");
	        final Runnable cacheControl = new Runnable() {
	                public void run() {
	                	log.info("Yeah look at me");
	                	log.info("Username = " + username);
	                	DBManager dbCreatorOld;
	                	DBManager dbCreatorNew;
						try {
							dbCreatorOld = new DBManager(username, password);
								String nextDBURL = dbCreatorOld.getNextDBUrl();
								log.info("Next database URL = " + nextDBURL);
								log.info("Cache has expired, recaching the data from Google Docs");
								dbCreatorNew = new DBManager(username, password, nextDBURL);
								dbCreatorNew.createDatabase();
								dbCreatorNew.switchActiveDatabase(dbCreatorNew.getNextDBUrl());
								
								dbCreatorOld.deleteDatabaseTables();
//	
//								//create data into that url
//								//switch the DB URL in the pickles info table.
//							
						} catch (SQLException e) {
							log.log(Level.SEVERE, "Problem recaching", e);
						}
	                	
	                }
	            };
	        final ScheduledFuture<?> dbCacheControlHandle = 
	            scheduler.scheduleAtFixedRate(cacheControl, period, period, SECONDS);
	    }


}
