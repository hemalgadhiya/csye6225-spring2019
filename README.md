# CSYE 6225 - Spring 2019

## Team Information

| Name | NEU ID | Email Address |
| --- | --- | --- |
| Satish Kumar Anbalagan| 001351994| anbalagan.s@husky.neu.edu|
| Paavan Gopala Reddy| 01813403| gopalareddy.p@husky.neu.edu|
| HemalKumar Gadhiya|001460577 |gadhiya.h@husky.neu.edu|
| Akshay Murgod|001635872 |murgod.a@husky.neu.edu |


## Technology Stack
Springboot
Maven
MySQL
GitHub Account
Apache Tomcat
AWS - Cloud Formation
AWS - VPC

## Build Instructions
WebApp --> Import the project using the existing maven project, and find the class having the main method to run the SpringBoot Application. And also before running the SpringBoot application, make sure the MySQL server is running.

Added Note folder inside webapp. Designed and implemented the following files:
a) Note.java
b) NoteDao.java
c) NoteRepository.java

Note is the model class which is basically a POJO file. It consists of notes format.

NoteDao is the service file which has all the method to create, update and delete the notes.

NoteRepository is an Interface where we extend the JPA Repository

AWS -->

a) Navigate to cloud formation folder and run the following scripts ./csye6225-aws-cf-create-stack.sh and ./csye6225-aws-cf-terminate-stack.sh to create and teardown the stack respectively.

b) Navigate to scripts folder and run the following scripts ./csye6225-aws-networking-setup.sh and ./csye6225-aws-networking-teardown.sh to create and teardown the VPC respectively.


## Deploy Instructions


## Running Tests
Check for valid email ID.
Check for stacks creation and deletion

## CI/CD
