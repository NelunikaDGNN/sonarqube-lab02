package com.example;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    
    private static final Logger logger = Logger.getLogger(App.class.getName());
    
    public static void main(String[] args) {
        
        Calculator calc = new Calculator();
        
        if (logger.isLoggable(Level.INFO)) {
            int result = calc.calculate(10, 5, "add-again");
            logger.log(Level.INFO, "Calculation result: {0}", result);
        }
        
        UserService service = null;
        try {
            service = new UserService();
        } catch (UserService.UserServiceException e) {
            
            String errorMessage = "Failed to initialize UserService: " + e.getMessage();
            logger.log(Level.SEVERE, errorMessage, e);
            
            logger.severe("Application cannot start due to configuration error.");
            logger.severe("Please check your database configuration.");
            System.exit(1);
            return; 
        }
        
        try {
            service.findUser("admin");
            service.deleteUser("admin");
        } catch (UserService.UserServiceException e) {
            
            logger.log(Level.SEVERE, "Error in application while processing user operations", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in application", e);
        }
    }
}