# NetComputing
Project for the course Net Computing during our Erasmus exchange programm in the Netherlands.
System with Server Client with use of Sockets,RMI,Message queues and REST(not implemented yet).
The client provides information about his CPU and memory usage to the server through message queuing and the server checks the usage levels and if they exceed a specific level sends an alert through socket to task manager that outputs an alert message.
Thought of extendind the project is to make task manager distributing the work from one client to many when an alert message occurs.
