# Loose Coupling and Message-based Integration Winter Semester 19/20

## Chat Application Exercise

- Develop a command line chat application

- Support for pub/sub messaging

- Chat application reiveis the name of the user as an input parameter

- Each message is terminated by a carriage return

- The messages are published on a predefined *JMS Topic*

- Incoming messages are  pulled bu the very same JMS topic to which the chat application must be subscribed

- All incoming messages should be  displayed on the console in the form: **[name of originating user]: [message text]**

- Instead of using a fully-fledged JNDI server, all objects that should be  available using JNDI are configured in the *jndi.properties* file

### Additional tasks

- Modify your chat application to filter  out messages from specific users

- Add the possibility to change topic dinamically

- Modify your chat application to allow one-to-one communicaitons between users

### Implementation details

Operating system: Ubuntu 18.04

Editor: Visual Studio Code (used also for debugging)


## Dependencies

[ActiveMQ](Guides/activemq.md)