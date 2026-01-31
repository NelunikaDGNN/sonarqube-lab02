package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.logging.Level;

public class UserService {
  
  private static final Logger logger = Logger.getLogger(UserService.class.getName());
  private final DatabaseConfig dbConfig;
  
  public UserService() throws UserServiceException {
    this.dbConfig = loadDatabaseConfig();
  }
  
  /**
   * Loads database configuration from external properties file
   * Removes hardcoded credentials security issue
   */
  private DatabaseConfig loadDatabaseConfig() throws UserServiceException {
   
    String envUrl = System.getenv("DB_URL");
    String envUser = System.getenv("DB_USERNAME");
    String envPassword = System.getenv("DB_PASSWORD");
    
    if (envUrl != null && envUser != null && envPassword != null) {
      if (logger.isLoggable(Level.CONFIG)) {
        logger.config("Loading database config from environment variables");
      }
      return new DatabaseConfig(envUrl, envUser, envPassword);
    }
    
   
    try (InputStream input = getClass().getClassLoader()
        .getResourceAsStream("db.properties")) {
      
      if (input == null) {
        throw new UserServiceException(
          "Unable to find database configuration. " +
          "Set DB_URL, DB_USERNAME, DB_PASSWORD environment variables " +
          "or create src/main/resources/db.properties"
        );
      }
      
      Properties prop = new Properties();
      prop.load(input);
      
      String url = prop.getProperty("db.url");
      String username = prop.getProperty("db.username");
      String password = prop.getProperty("db.password");
      
      if (url == null || username == null || password == null) {
        throw new UserServiceException(
          "Missing required properties in db.properties. " +
          "Need: db.url, db.username, db.password"
        );
      }
      
      if (logger.isLoggable(Level.CONFIG)) {
        logger.config("Database configuration loaded from properties file");
      }
      
      return new DatabaseConfig(url, username, password);
      
    } catch (IOException e) {
      throw new UserServiceException("Failed to load database configuration", e);
    }
  }
  
  /**
   * Finds a user by username using prepared statement to prevent SQL injection
   * @param username the username to search for
   * @throws UserServiceException if user lookup fails
   */
  public void findUser(String username) throws UserServiceException {
    // FIXED: Don't use SELECT *, specify specific columns
    String query = "SELECT id, username, email, created_at FROM users WHERE username = ?";
    
    if (!logger.isLoggable(Level.INFO)) {
      return; 
    }
    
    try (Connection conn = DriverManager.getConnection(
           dbConfig.getUrl(),
           dbConfig.getUsername(),
           dbConfig.getPassword());
         PreparedStatement stmt = conn.prepareStatement(query)) {
      
      stmt.setString(1, username);
      
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          int id = rs.getInt("id");
          String foundUsername = rs.getString("username");
          String email = rs.getString("email");
          java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
          
          logger.log(Level.INFO, 
            "Found user - ID: {0}, Username: {1}, Email: {2}, Created: {3}",
            new Object[]{id, foundUsername, email, createdAt}
          );
        } else {
          logger.log(Level.INFO, "User not found: {0}", username);
        }
      }
      
    } catch (SQLException e) {
      String errorMessage = String.format("Failed to find user: %s", username);
      throw new UserServiceException(errorMessage, e);
    }
  }
  
  /**
   * Deletes a user by username using prepared statement to prevent SQL injection
   * @param username the username to delete
   * @throws UserServiceException if deletion fails
   */
  public void deleteUser(String username) throws UserServiceException {
    String query = "DELETE FROM users WHERE username = ?";
    
    if (!logger.isLoggable(Level.INFO)) {
      return; 
    }
    
    try (Connection conn = DriverManager.getConnection(
           dbConfig.getUrl(),
           dbConfig.getUsername(),
           dbConfig.getPassword());
         PreparedStatement stmt = conn.prepareStatement(query)) {
      
      stmt.setString(1, username);
      int rowsAffected = stmt.executeUpdate();
      
      if (rowsAffected == 0) {
        String errorMessage = String.format("User not found: %s", username);
        logger.warning(errorMessage);
        throw new UserServiceException(errorMessage);
      }
      
      logger.log(Level.INFO, "Deleted {0} user(s) with username: {1}", 
                new Object[]{rowsAffected, username});
      
    } catch (SQLException e) {
      String errorMessage = String.format("Failed to delete user: %s", username);
      throw new UserServiceException(errorMessage, e);
    }
  }
  
//  Internal class to hold database configuration
  private static class DatabaseConfig {
    private final String url;
    private final String username;
    private final String password;
    
    public DatabaseConfig(String url, String username, String password) {
      this.url = url;
      this.username = username;
      this.password = password;
    }
    
    public String getUrl() {
      return url;
    }
    
    public String getUsername() {
      return username;
    }
    
    public String getPassword() {
      return password;
    }
  }
  
  
//  Custom exception for UserService operations
   
   
  public static class UserServiceException extends Exception {
    public UserServiceException(String message) {
      super(message);
    }
    
    public UserServiceException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}