package org.gdrive.jdbc;

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class jdbcDriver extends org.hsqldb.jdbcDriver {
    public jdbcDriver() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Connection connect(String s, Properties properties) throws SQLException {
        return super.connect(s, properties);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean acceptsURL(String s) {
        return super.acceptsURL(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String s, Properties properties) {
        return super.getPropertyInfo(s, properties);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int getMajorVersion() {
        return super.getMajorVersion();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int getMinorVersion() {
        return super.getMinorVersion();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean jdbcCompliant() {
        return super.jdbcCompliant();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return super.getParentLogger();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
