package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static org.neo4j.driver.v1.Values.parameters;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HandleRequest implements HttpHandler{
	
	// PUT /api/v1/addActor is an endpoint
	// ex.		localhost:8080/api/v1/addActor
	// use postman to PUT data into the database/server
	// need to setup methods here to accept requests from user and send to server
	
//	private String name;
//	private String actorId;
//	private HashMap<String, String> actors;
	private Driver driver;
	private String uriDb;
	public Utils u;
	
	
	public HandleRequest() {
		uriDb = "bolt://localhost:7687";
		Config config = Config.builder().withoutEncryption().build();
		driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j", "12345678"), config);
	}

	@Override
	public void handle(HttpExchange request) throws IOException {
		// TODO Auto-generated method stub
		
		try {
			if (request.getRequestMethod().equals("PUT")) {
				handlePut(request);
				System.out.println("PUT request");
			}
			else if (request.getRequestMethod().equals("GET")) {
				handleGet(request);
				System.out.println("GET request");
			}
			else {
				sendString(request, "Unimplemented method\n", 501);
			}
		} catch (Exception e) {
			e.printStackTrace();
			sendString(request, "Server error\n", 500);
		}
	}
	
	private void sendString(HttpExchange request, String data, int restCode) throws IOException {
		request.sendResponseHeaders(restCode, data.length());
		OutputStream os = request.getResponseBody();
		os.write(data.getBytes());
		os.close();
	}

	private void handleGet(HttpExchange request) throws IOException {
		try {
			// TODO Auto-generated method stub
			URI uri = request.getRequestURI();
			String path = uri.getPath();
			System.out.println(path);
			if (path.contentEquals("/api/v1/getActor")) {
				try {
					String actorId = Utils.getQueryParameter(uri, "actorId");
					System.out.println("actorId: " + actorId);
					if (actorId == null || actorId.isEmpty()) {
						sendString(request, "Bad request \n", 400);
						return;
					}
					
					if(actorExist(actorId)) {
						// Retrieve actor information and movies if available
						JSONObject actorInfo = getActorInfo(actorId);
						if (actorInfo != null) {
							System.out.println("actorInfo: " + actorInfo);
							sendString(request, actorInfo.toString(), 200);
						}
						else {
							sendString(request, "Actor not found first if\n", 404);
						}
					}
					else {
						sendString(request, "Actor not found actorId error\n", 404);
					}			
				} 
				catch(Exception e) {
					e.printStackTrace();
					sendString(request, "Bad request \n", 400);
					return;
				}
			}
			else if (path.contentEquals("/api/v1/getMovie")) {
				try {
					String movieId = Utils.getQueryParameter(uri, "movieId");
					
			        if (movieId == null || movieId.isEmpty()) {
			            sendString(request, "Missing movieId parameter\n", 400);
			            return;
			        }
			        
			        if (movieExist(movieId)) {
			            JSONObject movieInfo = getMovieInfo(movieId);
			            if (movieInfo != null) {
			                sendString(request, movieInfo.toString(), 200);
			            } else {
			                sendString(request, "Movie not found\n", 404);
			            }
			        } else {
			            sendString(request, "Movie not found\n", 404);
			        }
				}
				catch(Exception e) {
					e.printStackTrace();
					sendString(request, "Bad request \n", 400);
					return;
				}
			}
			else if (path.contentEquals("/api/v1/hasRelationship")) {
				try {
					String actorId = Utils.getQueryParameter(uri, "actorId");
					String movieId = Utils.getQueryParameter(uri, "movieId");
					
			        if (actorId == null || actorId.isEmpty() || movieId == null || movieId.isEmpty()) {
			            sendString(request, "Missing actorId or movieId parameter\n", 400);
			            return;
			        }

			        if (hasRelationship(actorId, movieId)) {
			        	JSONObject relationshipInfo = new JSONObject();
			        	relationshipInfo.put("actorId", actorId);
			        	relationshipInfo.put("movieId", movieId);
			        	relationshipInfo.put("hasRelationship", true);
			        	sendString(request, relationshipInfo.toString(), 200);			        	
			        }
			        else {
			        	JSONObject relationshipInfo = new JSONObject();
			        	relationshipInfo.put("actorId", actorId);
			        	relationshipInfo.put("movieId", movieId);
			        	relationshipInfo.put("hasRelationship", false);
			        	sendString(request, relationshipInfo.toString(), 404);	
			        }
					
				}
				catch(Exception e) {
					e.printStackTrace();
					sendString(request, "Bad request \n", 400);
					return;
				}
			}
			else if (path.contentEquals("/api/v1/computeBaconNumber")) {
				String jsonString = Utils.getBody(request);
				try {
					String actorId = Utils.getQueryParameter(uri, "actorId");
					
			        if (actorId == null || actorId.isEmpty()) {
			            sendString(request, "Missing actorId parameter\n", 400);
			            return;
			        }
			        
			        int baconNumber = computeBaconNumber(actorId);
			        if (baconNumber == -1) {
			        	sendString(request, "Actor not found or no path to Kevin Bacon\n", 404);
			        } 
			        else {
			        	JSONObject response = new JSONObject();
			        	response.put("baconNumber", baconNumber);
			        	sendString(request, response.toString(), 200);
			        }
				}
				catch(Exception e) {
					e.printStackTrace();
					sendString(request, "Bad request \n", 400);
					return;
				}
			}
			else if (path.contentEquals("/api/v1/computeBaconPath")) {
				try {
					String actorId = Utils.getQueryParameter(uri, "actorId");
					
			        if (actorId == null || actorId.isEmpty()) {
			            sendString(request, "Missing actorId parameter\n", 400);
			            return;
			        }
					
			        List<String> baconPath = computeBaconPath(actorId);
			        if (baconPath == null) {
			            sendString(request, "No path to Kevin Bacon\n", 404);
			        } 
			        else {
			            JSONObject response = new JSONObject();
			            response.put("baconPath", baconPath);
			            sendString(request, response.toString(), 200);
			        }
				}
				catch(Exception e) {
					e.printStackTrace();
					sendString(request, "Bad request \n", 400);
					return;
				}
			}
			else if (path.contentEquals("/api/v1/getActorsbyAge")) {

				try {
		            String minAgeStr = Utils.getQueryParameter(uri, "minAge");
		            String maxAgeStr = Utils.getQueryParameter(uri, "maxAge");
		            
		            int minAge = Integer.parseInt(minAgeStr);
		            int maxAge = Integer.parseInt(maxAgeStr);
		            
		            if (minAgeStr == null || maxAgeStr == null) {
		                sendString(request, "Missing minAge or maxAge parameter\n", 400);
		                return;
		            }
		            
		            List<String> actors = getActorsInAgeRange(minAge, maxAge);
		            
		            if (actors != null) {
		                JSONObject responseArray = new JSONObject();
		                responseArray.put("actors", actors);
		                sendString(request, responseArray.toString(), 200);
		            } else {
		                sendString(request, "No actors found in the specified age range\n", 404);
		            }
		        } 
				catch (NumberFormatException e) {
		            sendString(request, "Invalid age format\n", 400);
		        }
			}
			else {
			        sendString(request, "Bad request\n", 400);
			    }
		}
		catch (Exception e){
			e.printStackTrace();
			sendString(request, "Server error\n", 500);
		}
		
	}

	private void handlePut(HttpExchange request) throws IOException {
		// TODO Auto-generated method stub
		
		URI uri = request.getRequestURI();
		String path = uri.getPath();
		System.out.println(path);
		if (path.contentEquals("/api/v1/addActor")) {
			String jsonString = Utils.getBody(request);
			// convert to jsonObject to manipulate
			try {
				JSONObject jsonobj = new JSONObject(jsonString);
				System.out.println("JSONObject = " + jsonobj.toString());
				// actorId & name are the keys
				// values are the actual data that we want to use
				String name = jsonobj.get("name").toString();
				String actorId = jsonobj.get("actorId").toString();
				System.out.println("name: " + name);
				System.out.println("actorId: " + actorId);
				if (actorExist(actorId)) {
					sendString(request, "Bad request\n", 400);
				}
				else {
					try {
						String age = jsonobj.get("age").toString();
						if (Integer.valueOf(age) > 0) {
							addActor(name, actorId, age);
							sendString(request, jsonString, 200);
						}
						else { 
							sendString(request, "Invalid age\n", 400);
						}
					}
					catch (Exception e) {
						addActor(name, actorId);
						sendString(request, jsonString, 200);
					}

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sendString(request, "Bad request\n", 400);
				return;
			}
			
		}
		else if (path.contentEquals("/api/v1/addMovie")) {
			String jsonString = Utils.getBody(request);
			// convert to jsonObject to manipulate
			try {
				JSONObject jsonobj = new JSONObject(jsonString);
				System.out.println("JSONObject = " + jsonobj.toString());
				// movieId & name are the keys
				// values are the actual data that we want to use
				String name = jsonobj.get("name").toString();
				String movieId = jsonobj.get("movieId").toString();
				System.out.println("name: " + name);
				System.out.println("actorId: " + movieId);
				if (movieExist(movieId)) {
					sendString(request, "Bad request\n", 400);
				}
				else {
					addMovie(name, movieId);
					sendString(request, jsonString, 200);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sendString(request, "Bad request\n", 400);
				return;
			}
		}
		else if (path.contentEquals("/api/v1/addRelationship")) {
			String jsonString = Utils.getBody(request);
			// convert to jsonObject to manipulate
			try {
				JSONObject jsonobj = new JSONObject(jsonString);
				System.out.println("JSONObject = " + jsonobj.toString());
				// movieId & name are the keys
				// values are the actual data that we want to use
				String actorId = jsonobj.get("actorId").toString();
				String movieId = jsonobj.get("movieId").toString();
				System.out.println("actorId: " + actorId);
				System.out.println("movieId: " + movieId);
				if (hasRelationship(actorId, movieId)) {
					sendString(request, "Bad request\n", 400);
				}
				else if (!actorExist(actorId) || !movieExist(movieId)) {
					sendString(request, "Bad request\n", 400);
				}
				else {
					addRelation(actorId, movieId);
					sendString(request, jsonString, 200);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sendString(request, "Bad request\n", 400);
				return;
			}
		}
		else { // wrong format/missing info
			sendString(request, "Bad request\n", 400);
		}
	}
	
	private void addActor(String actor, String actorId) {
		try (Session session = driver.session()) {
			session.writeTransaction(tx -> tx.run("MERGE (a:actor {name:$x, actorId:$y})",
					parameters("x", actor, "y", actorId) ));	
			session.close();
		} 
		
	}

	private void addRelation(String actorId, String movieId) {
		try (Session session = driver.session()) {
			session.writeTransaction(
					tx -> tx.run(
							"MATCH (a:actor {actorId:$x}), (m:movie {movieId:$y})\n" +
							"MERGE (a)-[r:ACTED_IN]->(m)\n" +
							"RETURN r",
							parameters("x", actorId, "y", movieId) 
							) 
					);	
			session.close();
		}		
	}

	private void addMovie(String movie, String movieID) {
		try (Session session = driver.session()) {
			session.writeTransaction(tx -> tx.run("MERGE (m:movie {name:$x, movieId:$y})",
					parameters("x", movie, "y", movieID) ));	
			session.close();
		} 
	}

	public void addActor(String actor, String actorID, String age) {
		try (Session session = driver.session()) {
			session.writeTransaction(tx -> tx.run("MERGE (a:actor {name:$x, actorId:$y, age:$z})",
					parameters("x", actor, "y", actorID, "z", age) ));	
			session.close();
		} 
	}
	
	private JSONObject getActorInfo (String actorId) {
		try (Session session = driver.session()) {
			try (Transaction tx = session.beginTransaction()) {
	            StatementResult result = tx.run("MATCH (a:actor {actorId: $x})\n" + "RETURN a.name as name, a.actorId as actorId, a.age as age", parameters("x", actorId));
	            if (result.hasNext()) {
	                Record record = result.next();
	                String name = record.get("name").asString();
	                String fetchedActorId = record.get("actorId").asString();
	                String age = record.get("age").asString();

	                JSONArray movies = getMoviesForActor(tx, actorId);


	                JSONObject actorInfo = new JSONObject();
	                actorInfo.put("actorId", fetchedActorId);
	                actorInfo.put("name", name);
	                actorInfo.put("age", age);
	                actorInfo.put("movies", movies);

	                return actorInfo;
	            }

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	private JSONArray getMoviesForActor(Transaction tx, String fetchedActorId) {
		// TODO Auto-generated method stub
		StatementResult result = tx.run("MATCH (a:actor {actorId: $x})-[r:ACTED_IN]->(m:movie)\n" + "RETURN m.movieId as movieId",
										parameters("x", fetchedActorId));
		JSONArray movies = new JSONArray();
		while (result.hasNext()) {
			Record record = result.next();
			movies.put(record.get("movieId").asString());

		}
		return movies;
	}

	public boolean actorExist(String actorId) {
		try (Session session = driver.session()) {
			try (Transaction tx = session.beginTransaction()){
				StatementResult result = tx.run("MATCH (a:actor {actorId: $x}) RETURN a.actorId as actorId",
							parameters("x", actorId));
				return result.hasNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private JSONObject getMovieInfo(String movieId) {
		// TODO Auto-generated method stub
		try (Session session = driver.session()){
			try (Transaction tx = session.beginTransaction()){
				StatementResult result = tx.run("MATCH (m:movie {movieId: $x})\n" + "RETURN m.name as name, m.movieId as movieId",
												parameters("x", movieId));
				if(result.hasNext()) {

					Record record = result.next();
					String name = record.get("name").asString();
					String fetchedMovieId = record.get(movieId).asString();
					
					JSONArray actors = getActorsInMovie(tx,movieId);
					
					JSONObject movieInfo = new JSONObject();
					movieInfo.put("movieId", movieId);
					movieInfo.put("name", name);
					movieInfo.put("actors", actors);
					
					return movieInfo;
				}
			}
		}
		 catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}
	
	private JSONArray getActorsInMovie(Transaction tx, String fetchedMovieId) {
		// TODO Auto-generated method stub

		StatementResult result = tx.run("MATCH (m:movie {movieId: $x})<-[r:ACTED_IN]-(a:actor)\n" +  "RETURN a.actorId as actorId",
										parameters("x",fetchedMovieId));
		JSONArray actors = new JSONArray();

		
		if (result.hasNext()){
			while (result.hasNext()) {

				Record record = result.next();
				actors.put(record.get("actorId").asString());

			}
			return actors;
		}
		return null;
	}
	
    private boolean hasRelationship(String actorId, String movieId) {
        // TODO Auto-generated method stub
        try (Session session = driver.session()){
            try (Transaction tx = session.beginTransaction()) {
                StatementResult result = tx.run("MATCH (a:actor {actorId: $actorId})-[r:ACTED_IN]->(m:movie {movieId: $movieId}) RETURN COUNT(r) as count",
                                                parameters("actorId", actorId, "movieId", movieId));
                if(result.hasNext()) {
                    Record record = result.next();
                    int count = record.get("count").asInt();
                    return count > 0;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
	
	private int computeBaconNumber(String actorId) {
		// TODO Auto-generated method stub
	    if (actorId.equals("nm0000102")) {
	        return 0; // Kevin Bacon has a BaconNumber of 0
	    }

	    try (Session session = driver.session()) {
	        try (Transaction tx = session.beginTransaction()) {
	            // Use a Cypher query to find the shortest path to Kevin Bacon (BaconNumber)
	            StatementResult result = tx.run("MATCH (a:actor {actorId: $x}), (kb:actor {actorId: 'nm0000102'}), " +
	                    "p=shortestPath((a)-[*]-(kb)) " +
	                    "WITH [n IN nodes(p) WHERE n:actor] AS actors " +
	                    "RETURN size(actors) - 1 as baconNumber",
	                    parameters("x", actorId));
	            if (result.hasNext()) {
	                Record record = result.next();
	                int baconNumber = record.get("baconNumber").asInt();
	                return baconNumber;
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return -1; // Actor not found or no path to Kevin Bacon
	}
	
	private List<String> computeBaconPath(String actorId) {
	    if (actorId.equals("nm0000102")) {
	        return Collections.singletonList("nm0000102"); // Kevin Bacon's baconPath is just his actorId
	    }
	    
	    try (Session session = driver.session()){
	    	try (Transaction tx = session.beginTransaction()) {
	    		StatementResult result = tx.run("MATCH p=shortestPath((a:actor {actorId: $x})-[*]-(kb:actor {actorId: 'nm0000102'})) " +
	    										"UNWIND nodes(p) as node " +
	    										"RETURN CASE WHEN node.actorId IS NOT NULL THEN node.actorId ELSE node.movieId END AS id",
	    										parameters("x", actorId));
	    		
	    		List<String> baconPath = new ArrayList<>();
	    		while (result.hasNext()) {
	    			Record record = result.next();
	    			String id = record.get("id").asString();
	    			baconPath.add(id);
	    		}
	    		
	    		if (baconPath.isEmpty()) {
	    			return null;
	    		}
	    		else {
	    			return baconPath;
	    		}
	    	}
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public boolean movieExist(String movieId) {
	    try (Session session = driver.session()) {
	        try (Transaction tx = session.beginTransaction()) {

	            StatementResult result = tx.run("MATCH (m:movie {movieId: $x}) RETURN COUNT(m) AS count",
	                    parameters("x", movieId));

	            if (result.hasNext()) {
	                Record record = result.next();
	                int count = record.get("count").asInt();
	                return count > 0; // Return true if at least one movie with the given movieId exists
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false; // An error occurred or no movie found
	}


    private List<String> getActorsInAgeRange(int minAge, int maxAge) {
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                // Use a Cypher query to retrieve actors within the specified age range
                StatementResult result = tx.run(
                    "MATCH (a:actor) WHERE toInteger(a.age) >= toInteger($minAge) AND toInteger(a.age) <= toInteger($maxAge) RETURN a.actorId AS actorId",
                    parameters("minAge", minAge, "maxAge", maxAge)
                );

                List<String> actors = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    String actorId = record.get("actorId").asString();
                    actors.add(actorId);
                }
                if (actors.isEmpty()) {
                	return null;
                }
                return actors;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // An error occurred or no actors found
    }
}


