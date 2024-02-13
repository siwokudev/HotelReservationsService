

# Bussines requirements

Create a simple backend application for hotel to work with reservations
in the application it should be possible to

* Create a new reservation
* Read all the reservations
* Change some values of one reservation by id, wich already exists

### Database definition

|    Field name    |       Type        |
|:----------------:|:-----------------:|
|        id        |      Integer      |
|  clientFullName  |      String       |
|    roomNumber    |      Integer      |
| reservationDates | List\<LocalDate\> |

## Repository notes

This is a REST service, it exposes the following endpoints

### Endpoints

* GET /reservation
    * fetch all reservations
* POST /reservation
  * saves a new reservation
  * BODY
    * ```yaml
      {
      "clientFullName": "John Smith",
      "roomNumber": 501,
      "startDate" : "2024-02-16",
      "endDate": "2024-02-17"
      }
* PUT /reservation/{id}
    * updates an existing reservation
    * BODY
      * ```yaml
        {
        "clientFullName": "John Smith",
        "roomNumber": 501,
        "startDate" : "2024-02-16",
        "endDate": "2024-02-17"
        }
* DELETE /reservation/{id}
  * deletes an existing reservation

### Database
* Database is an H2 that stores information in a file

### Testing
a postman collection is included for testing (*Coherent-HotelReservations.postman_collection.json*)
and all endpoints and services have their own unit tests using Junit and Mockito

### Features
it uses:
* Java 17
* Lombok 
* Mapstruct
* H2 database
* ObjectMother pattern along with Builder for easy testing data creation