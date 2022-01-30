package io.openliberty.sample.jakarta.transaction;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;
import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import jakarta.transaction.*;
import java.sql.*;
import java.util.Hashtable;


public class demoTransaction{

	
	/* JDBC driver name and database URL */
//	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  // YICHENG
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  // LANA
	static final String DB_URL = "jdbc:mysql://localhost/";
	static final String DB_URL1 = "jdbc:mysql://localhost/jdbc_db";

	/* Database credentials */
	static final String USER = "root";
//	static final String PASS = "123456"; // YICHENG
	static final String PASS = ""; // LANA

	
	/** Set up the initial context **/
	// https://stackoverflow.com/questions/20359483/initialcontext-in-a-standalone-java-program/21733896#21733896
	private static Context setupInitialContext() {
	    try {
	        NamingManager.setInitialContextFactoryBuilder(new InitialContextFactoryBuilder() {
	
	            @Override
	            public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) throws NamingException {
	                return new InitialContextFactory() {
	
	                    @Override
	                    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
	                        return new InitialContext(){
	
	                            private Hashtable<String, DataSource> dataSources = new Hashtable<>();
	
	                            @Override
	                            public Object lookup(String name) throws NamingException {
	
	                                if (dataSources.isEmpty()) {
	                                    MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
	                                    ds.setURL(DB_URL);
	                                    ds.setUser(USER);
	                                    ds.setPassword(PASS);
	                                    dataSources.put("jdbc/jdbc_db", ds);
	                                }
	
	                                if (dataSources.containsKey(name)) {
	                                    return new InitialContext(dataSources);
	                                }
	                                
	                                throw new NamingException("Unable to find datasource: "+name);
	                            }
	                        };
	                    }
	                };
	            }
	        });
	    }
	    catch (NamingException ne) {
	        ne.printStackTrace();
	    }
		return null;
	}
	
	/** Set up the user transaction */
	private static UserTransaction setupUserTransaction(){
		
		UserTransaction userTransaction = null;
		
		try {
			
			Context initContext = setupInitialContext();
			
			// Check (if this is null, something is wrong (likely JNDI property)
			if(initContext == null) throw new NoInitialContextException();
				
			userTransaction = (UserTransaction) initContext.lookup("java:comp/UserTransaction");
			
		} catch (NoInitialContextException e){
			
			System.out.println("The InitialContext instance is null, so a UserTransaction instance cannot be instantiated.");
		
		} catch (Exception e) { e.printStackTrace(); }
		
		return userTransaction;
	}
	
	private static Connection setupJDBCConnection() {
		
		Connection conn = null;
		
		try {
		      // Register the JDBC driver
//		      Class.forName("com.mysql.jdbc.Driver"); // YICHENG
			  Class.forName("com.mysql.cj.jdbc.Driver"); // LANA

		      //Open a connection
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      
		   } catch(SQLException se) {
			   
		      //Handle errors for JDBC
		      se.printStackTrace();
		      
		   } catch(Exception e) {
			   
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }
		
		return conn;

	}
	
	private static void updateDB(Connection conn, String sql) {

		Statement stmt = null;

		try {
			
			stmt = conn.createStatement();
		    stmt.executeUpdate(sql);
		    stmt.close();
		    
		} catch (SQLException e) { e.printStackTrace(); }
		
	}
	
	public static void main(String[] args) {
		
		Connection conn = setupJDBCConnection();
		UserTransaction userTransaction = setupUserTransaction();
//		updateDB(conn, "CREATE DATABASE jdbc_db"); // Create the DB if not yet there.
		
		try {
			userTransaction.begin();
		} catch (NotSupportedException e) { 
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		try {
			
			String sql = "INSERT INTO jdbc_db " + "VALUES (100, 18)";
			updateDB(conn, sql);
			
			sql = "INSERT INTO jdbc_db " + "VALUES (101, 25)";
			updateDB(conn, sql);

			userTransaction.commit();
			
		} catch(Exception e){
			e.printStackTrace();
			
			try {
				userTransaction.rollback();
			} catch (IllegalStateException | SecurityException | SystemException e1) {
				e1.printStackTrace();
			}
					
		}
		
		try {
			conn.close();
		} catch (SQLException e) { e.printStackTrace(); }
	}
}
