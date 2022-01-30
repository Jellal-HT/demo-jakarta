package io.openliberty.sample.jakarta.transaction;

import javax.naming.Context;
import javax.naming.InitialContext;
import jakarta.transaction.*;
import java.sql.*;




public class demoTransaction{
	   // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://localhost/";
	   static final String DB_URL1 = "jdbc:mysql://localhost/jdbc_db";



	   //  Database credentials
	   static final String USER = "root";
	   static final String PASS = "123456";


	
	private static UserTransaction getUserTransaction(){
		UserTransaction userTransaction = null;
		try {
			Context initContext = new InitialContext();
			userTransaction = (UserTransaction) initContext.lookup("java:comp/UserTransaction");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userTransaction;
	}
	
	public static Connection createDB() {
		Connection conn = null;
		Statement stmt = null;
		try{
		      //Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //Open a connection
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      stmt = conn.createStatement();
		      // create a database
		      String sql = "CREATE DATABASE jdbc_db";
		      stmt.executeUpdate(sql);
		      stmt.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
			      //finally block used to close resources
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			         se.printStackTrace();
			      }//end finally try
			   }
		try{
		      //Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //Open a connection
		      conn = DriverManager.getConnection(DB_URL1, USER, PASS);
		      stmt = conn.createStatement();
		      String sql = "CREATE TABLE student " +
		                   "(id INTEGER not NULL, " +
		                   " age INTEGER, " + 
		                   " PRIMARY KEY ( id ))"; 
		      stmt.executeUpdate(sql);
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }
		return conn;

	}
	
	public static void main(String[] args) {
		UserTransaction userTransaction = null;
		Connection conn = createDB();
		try {
			userTransaction.begin();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Statement stm = conn.createStatement();
			
			String sql = "INSERT INTO student " + "VALUES (100, 18)";
			stm.executeQuery(sql);
			sql = "INSERT INTO student " + "VALUES (101, 25)";
			stm.executeQuery(sql);
 
			userTransaction.commit();
			
			stm.close();
			conn.close();
		} catch(Exception e){
			e.printStackTrace();
			try {
				userTransaction.rollback();
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("error");				
		}
	}
}
