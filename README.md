# 3311-course-project

Erik Huynh - SID: 215414923 - erikh99@my.yorku.ca
Vishek Lamba - SID: 218226811 - vishek12@my.yorku.ca
Vinh Quang Dang - SID: 218897843 -  qdang@my.yorku.ca 

New Feature:
for a new feature, I will add age for actors and an endpoint that generates all actors in a given age bracket (0-18, 18-60, 60-61).

the example URL for putting actors with their age is:
http://localhost:8080/api/v1/addActor
{
	"name": "Denzel Washington",
	"actorId": "nm1001213",
	“age”: “18”
}

the example URL for getting actors by age is 
http://localhost:8080/api/v1/getActorsByAge?minAge=18&maxAge=60

