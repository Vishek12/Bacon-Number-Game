package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

//Handler for the GET endpoint
public class GetHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		//TODO: Implement your GET logic here

		boolean badRequest = true; //TODO: Replace with your logic to determine if the request is bad
		boolean notFound = true; //TODO: Replace with your logic to determine if resource is not found
		boolean internalServerError = true; //TODO: Replace with your logic to determine if an internal server error occurred

		if (badRequest) {
			exchange.sendResponseHeaders(400, 0); // 400 Bad Request
			exchange.getResponseBody().close();
			return;
		}

		if (notFound) {
			exchange.sendResponseHeaders(404, 0); // 404 Not Found
			exchange.getResponseBody().close();
			return;
		}

		if (internalServerError) {
			exchange.sendResponseHeaders(500, 0); // 500 Internal Server Error
			exchange.getResponseBody().close();
			return;
		}

		// Implement response logic
		String response = "GET request successful!";
		exchange.sendResponseHeaders(200, response.length()); // 200 OK
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}


