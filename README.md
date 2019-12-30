# Spring Security Registration and Login with JPA, MySQL

A REST API for registering new users to a mySQL database, and using database-side validation for logging in and accessing secured endpoints.

A front-end can react accordingly to HTML responses, and will not be able to retrieve any data or access secured endpoints without proper validation.

We BCrypt to store passwords, and simple password validation to ensure a minimal password strength.
We use JPA to make reading and writing to the database incredibly easy.
When uncommented, new users/forgot password tokens are sent by email.

### Prerequisites
For this project I used:

[Maven] 3.6.2
Spring Tool Suite 4.4.1
MySQL 8.0.17
Spring Boot 1.5.3
JDK 8
Gmail SMTP
Postman (for testing)

### Installing

Clone from git, or download manually.

In Spring Tool Suite, select File -> Open Projects From File System

Click Directory. Navigate to and select spring_security_registration_login_JPA

In /src/main/resources/application.properties :
We use Environment variables to store our SMTP credentials, and MySQL credentials. We use the database schema demo_db
You must change these to match your credentials and database schema. (The user table is automatically generated on running)

Right click the project in Project Explorer and select Run As -> Spring Boot App
    (If this is not available as an option, perhaps try running a maven clean install)

## Testing

Using Postman we can make HTML Requests to each corresponding mapping.

If we make a POST request to http://localhost:8080/register with body:
{ "email" : "brett.m.coding@gmail.com",
  "userName" : "b" }
We will get a 200 response, and the database will be updated with a new user containing a confirmation token.
If the JSON object does not contain an email and userName, we will get a 400 Bad Request response.
If the database already contains the email address, we will get a 409 Conflict response.

When the code in RequestController is uncommented, an email will be sent to the email address containing the token.

For now, copy the token manually from the database and add it to our next request.

Make a POST request to http://localhost:8080/confirm?token=your_token_here with body:
{ "p1" : "Aabcca123",
  "p2" : "Aabcca123" }
We will get a 200 response, the token will be removed from the database, the enabled column will be set to 1, and the provided password will be stored with BCrypt.
If the token is invalid, we will get a 406 Not Acceptable response.
If the passwords (p1/p2) do not match, we will get a 406 Not Acceptable response.
If the password is not strong enough, We will get a 406 Not Acceptable response.
The current password rules as defined in our PasswordValidator class are: 
at least one uppercase letter, one lowercase letter, one number, and be at least 8 characters

Make a POST request to http://localhost:8080/forgot with body:
{ "email" : "brett.m.coding@gmail.com" }
We will get a 200 response, and if a user is found with a matching email address, they will get a new confirmation token.
If the email address does not exist in the database, we will get a 406 Not Acceptable response.

When the code in ForgotPasswordController is uncommented, an email will be sent to the email address containing the token.

We can use http://localhost:8080/confirm?token=your_token_here exactly the same as above to set the new password.


We can now make a GET request to http://localhost:8080/auth with Authorization:
Basic Auth -> username: b password: Aabcca123
We will get a 200 response.
If no credentials are entered, we will get a 401 Unauthorized.
If we enter credentials that don't match a user in our user table, we will get a 401 Unauthorized.

Now we have a cookie that will allow us to make GET requests to http://localhost:8080/secured/test
If we are not logged in, we will get a 401 Unauthorized.

## Contributing

As a junior developer, any contributions to this are appreciated. Feel free to submit pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Thanks to:
* in28minutes https://in28minutes.com/
* Baeldung https://www.baeldung.com/
* TechPrimers http://techprimers.com/
* Code By Amir https://www.codebyamir.com/
* Telusko http://www.telusko.com/

Incredible learning resources and I couldn't have made this without learning from all of them.
