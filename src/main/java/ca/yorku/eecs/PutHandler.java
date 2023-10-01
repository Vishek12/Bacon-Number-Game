package ca.yorku.eecs;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class PutHandler implements HttpHandler {
	
	static int PORT = 8080;

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
        //TODO: Implement your PUT logic here
        // For simplicity, I assume that the request body contains required information
        
        try {
            // Simulate adding logic
            boolean addSuccessful = true; //TODO: Replace with your logic
            boolean notFound = true; //TODO: implement the not found error
            
            if (notFound) {
            	exchange.sendResponseHeaders(404,0); //You can change the trailing number to the length of outputting message
            }
            if (addSuccessful) {
                // Successful add
                exchange.sendResponseHeaders(200, 0);
            } else {
                // Internal server error
                exchange.sendResponseHeaders(500, 0);
            }
        } catch (Exception e) {
            // Bad request (improperly formatted request body)
            exchange.sendResponseHeaders(400, 0);
        }

        exchange.getResponseBody().close();
    }
}
