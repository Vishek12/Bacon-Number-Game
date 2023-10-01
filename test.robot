*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      20 seconds

Suite Setup    Create Session    localhost    http://localhost:8080

*** Keywords ***
Should Contain Same Elements
    [Arguments]    ${list1}    ${list2}
    ${result}=    Evaluate    set(${list1}) == set(${list2})
    Should Be True    ${result}

*** Test Cases ***
addActorPass1
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Denzel		actorId=nm1001231
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    	json=${params}    headers=${headers}    expected_status=200
    
addActorPass2
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Rob			actorId=nm1001232
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    	json=${params}    headers=${headers}    expected_status=200
    
addActorPass3
	${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Kevin Bacon		actorId=nm0000102
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    	json=${params}    headers=${headers}    expected_status=200

addAuthorFailBadFormat
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Devin
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    	json=${params}    headers=${headers}    expected_status=400
    
addAuthorFailSameId
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Denzel		actorId=nm1001231
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    	json=${params}    headers=${headers}    expected_status=400

addMoviePass1
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Parasite	movieId=nm7001454
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    	json=${params}    headers=${headers}    expected_status=200
    
addMoviePass2
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=SpiderMan	movieId=nm7001455
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    	json=${params}    headers=${headers}    expected_status=200

addMovieFailBadFormat
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Animal Farm    
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie		json=${params}    headers=${headers}    expected_status=400
    
addMovieFailSameId
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Parasite	movieId=nm7001454
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie	    json=${params}    headers=${headers}    expected_status=400

addRelationshipPass1
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1001231	movieId=nm7001454
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    	json=${params}    headers=${headers}    expected_status=200
    
addRelationshipPass2
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1001232	movieId=nm7001454
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    	json=${params}    headers=${headers}    expected_status=200
    
addRelationshipPass3
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000102	movieId=nm7001455
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    	json=${params}    headers=${headers}    expected_status=200
    
addRelationshipPass4
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1001231	movieId=nm7001455
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    	json=${params}    headers=${headers}    expected_status=200    

addRelationshipFailBadFormat
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1001233
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship		json=${params}    headers=${headers}    expected_status=400
    
addRelationFailRelationExist
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1001231	movieId=nm7001454
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship	    json=${params}    headers=${headers}    expected_status=400
    
# Test cases for /api/v1/getActor endpoint
getActorPass
    ${actorId}=    Set Variable    nm1001231
    ${resp}=    GET On Session    localhost    url=/api/v1/getActor?actorId=${actorId}    expected_status=200
    ${resp_body}=    Set Variable    ${resp.json()}
	Should Contain    ${resp_body.keys()}    actorId
	Should Contain    ${resp_body.keys()}    name
	Should Contain    ${resp_body.keys()}    movies
    ${expected_actor_id}=    Set Variable    nm1001231
    ${expected_name}=    Set Variable    Denzel
    ${expected_movies}=    Create List    nm7001454    nm7001455
    Should Be Equal    ${resp_body['actorId']}    ${expected_actor_id}
    Should Be Equal    ${resp_body['name']}    ${expected_name}
    Should Contain Same Elements    ${resp_body['movies']}    ${expected_movies}

getActorFailBadRequest
    ${resp}=    GET On Session    localhost    url=/api/v1/getActor    expected_status=400

getActorFailNotFound
    ${actorId}=    Set Variable    non_existent_actor_id
    ${resp}=    GET On Session    localhost    url=/api/v1/getActor?actorId=${actorId}    expected_status=404

# Test cases for /api/v1/getMovie endpoint
getMoviePass
    ${movieId}=    Set Variable    nm7001454
    ${resp}=    GET On Session    localhost    url=/api/v1/getMovie?movieId=${movieId}    expected_status=200
    ${resp_body}=    Set Variable    ${resp.json()}
	Should Contain    ${resp_body.keys()}    movieId
	Should Contain    ${resp_body.keys()}    name
	Should Contain    ${resp_body.keys()}    actors
    ${expected_movie_id}=    Set Variable    nm7001454
    ${expected_name}=    Set Variable    Parasite
    ${expected_actors}=    Create List    nm1001232    nm1001231
    Should Be Equal    ${resp_body['movieId']}    ${expected_movie_id}
    Should Be Equal    ${resp_body['name']}    ${expected_name}
    Should Contain Same Elements    ${resp_body['actors']}    ${expected_actors}

getMovieFailBadRequest
    ${resp}=    GET On Session    localhost    url=/api/v1/getMovie    expected_status=400

getMovieFailNotFound
    ${movieId}=    Set Variable    non_existent_movie_id
    ${resp}=    GET On Session    localhost    url=/api/v1/getMovie?movieId=${movieId}    expected_status=404

# Test cases for /api/v1/hasRelationship endpoint
hasRelationshipPass
    ${actorId}=    Set Variable    nm1001231
    ${movieId}=    Set Variable    nm7001454
    ${resp}=    GET On Session    localhost    url=/api/v1/hasRelationship?actorId=${actorId}&movieId=${movieId}    expected_status=200
    ${resp_body}=    Set Variable    ${resp.json()}
	Should Contain    ${resp_body.keys()}    actorId
	Should Contain    ${resp_body.keys()}    movieId
	Should Contain    ${resp_body.keys()}    hasRelationship
    ${expected_actor_id}=    Set Variable    nm1001231
    ${expected_movie_id}=    Set Variable    nm7001454
    Should Be Equal    ${resp_body['actorId']}    ${expected_actor_id}
    Should Be Equal    ${resp_body['movieId']}    ${expected_movie_id}
    Should Be Equal As Strings    ${resp_body['hasRelationship']}    True

hasRelationshipFailBadRequest
    ${resp}=    GET On Session    localhost    url=/api/v1/hasRelationship    expected_status=400

hasRelationshipFailNotFound
    ${actorId}=    Set Variable    12
    ${movieId}=    Set Variable    34
    ${resp}=    GET On Session    localhost    url=/api/v1/hasRelationship?actorId=${actorId}&movieId=${movieId}    expected_status=404

# Test cases for /api/v1/computeBaconNumber endpoint
computeBaconNumberPass
    ${actorId}=    Set Variable    nm1001231
    ${resp}=    GET On Session    localhost    url=/api/v1/computeBaconNumber?actorId=${actorId}    expected_status=200
    ${resp_body}=    Set Variable    ${resp.json()}
	Should Contain    ${resp_body.keys()}    baconNumber
    ${expected_bacon_number}=    Set Variable    1
    Should Be Equal As Integers    ${resp_body['baconNumber']}    ${expected_bacon_number}

computeBaconNumberFailBadRequest
    ${resp}=    GET On Session    localhost    url=/api/v1/computeBaconNumber    expected_status=400

computeBaconNumberFailNotFound
    ${actorId}=    Set Variable    non_existent_actor_id
    ${resp}=    GET On Session    localhost    url=/api/v1/computeBaconNumber?actorId=${actorId}    expected_status=404

# Test cases for /api/v1/computeBaconPath endpoint
computeBaconPathPass
    ${actorId}=    Set Variable    nm1001231
    ${resp}=    GET On Session    localhost    url=/api/v1/computeBaconPath?actorId=${actorId}    expected_status=200
    ${resp_body}=    Set Variable    ${resp.json()}
	Should Contain    ${resp_body.keys()}    baconPath
    ${expected_bacon_path}=    Create List    nm1001231    nm7001455    nm0000102
    Should Contain Same Elements    ${resp_body['baconPath']}    ${expected_bacon_path}
    
computeBaconPathFailBadRequest
    ${resp}=    GET On Session    localhost    url=/api/v1/computeBaconPath    expected_status=400

computeBaconPathFailNotFound
    ${actorId}=    Set Variable    non_existent_actor_id
    ${resp}=    GET On Session    localhost    url=/api/v1/computeBaconPath?actorId=${actorId}    expected_status=404
    
addActorPass200
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Actor1		actorId=nm1		age=25
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    	json=${params}    headers=${headers}    expected_status=200
    
addActorFailed400
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Actor2		actorId=nm2		age=-1
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    	json=${params}    headers=${headers}    expected_status=400
    
getActorByAge200
    ${resp}    GET On Session    localhost    url=/api/v1/getActorsbyAge?minAge=20&maxAge=25    expected_status=200
    ${resp_body}    Set Variable    ${resp.json()}
    ${expected_valid_actors}    Create List    nm1
    Should Contain Same Elements    ${resp_body['actors']}    ${expected_valid_actors}

getActorByAge404
    # Test case for retrieving actors in an invalid age bracket (no actors in the age bracket)
    ${resp_invalid_age}=    GET On Session    localhost    url=/api/v1/getActorsbyAge?minAge=70&maxAge=80    expected_status=404
    Should Be Equal As Integers    ${resp_invalid_age.status_code}    404

GetActorBadRequest
    ${resp}=    GET On Session    localhost    url=/api/v1/getActorsbyAge    expected_status=400
    Should Contain    ${resp.text}    Invalid age format

