# recipes
Spring Boot application using Spring JPA, REST and Security

Aplicatie ce permite stocarea de retete cu via REST. Sunt folosite tehnologiile Spring JPA, Spring Web(REST), Spring Security si sgbd H2. 

Endpoints:

POST /api/register 
Primeste un obiect JSON cu doua campuri: username (string) - min. 3 caractere, si  password (string) - min. 8 caractere. 
Daca userul nu exista, acesta este salvat in baza de date iar raspunsul este 200 (Ok). 
Daca userul exista deja, raspunsul este 400 (Bad Request).

Accesarea endpointurilor urmatoare necesita logarea(autentificarea) cu user si parola stabilite in pasul anterior, prin metoda Basic Auth. 

POST /api/new
Primeste un obiect JSON cu 5 campuri: name, category, description - toate string cu valori non-empty,
si ingredients, directions array-uri tip string cu cel putin un element non-empty.
Returneaza un obiect JSON cu cheie id si valoarea id-ului autogenerat pentru reteta nou inregistrata
sau 400 (bad request) daca nu sunt valide campurile.

GET /api/recipe/{id}
Returneaza un obiect JSON aferent retetei cu id-ul corespunzator sau 404 (not found) daca id-ul nu corespunde unei retete existente. 

DELETE /api/recipe/{id}
Poate fi apelat doar de catre userii autentificati, altfel returneaza 403 (forbidden)
Sterge reteta aferenta id-ului si returneaza 204 (no content). 
Daca id-ul nu corespunde unei retete existente, returneaza 404. 

PUT /api/recipe/{id}
Poate fi apelat doar de catre userii autentificati, altfel returneaza 403 (forbidden)
Updateaza reteta aferenta id-ului si returneaza 204 (no content). Trebuie trimise toate campurile, in aceleasi conditii ca si /api/new
Daca id-ul nu corespunde unei retete existente, returneaza 404. 

GET /api/recipe/search/
Cauta retete dupa campurile name sau category prin request parameters. 
utilizare:
/api/recipe/search/?name=cocktail (va returna retetele cu cuvantul cocktail in campul nume)
/api/recipe/search/?name=beverage (va returna retetele cu cuvantul beverage in campul category)
Daca sunt folositi ca paramentrii alte campuri sau denumiri decat name sau category, va returna 400 (bad request)
