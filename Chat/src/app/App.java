package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class App {

    private TopicConnectionFactory conFactory;
	private TopicConnection connection;
	private TopicSession pubSession;
	private TopicSession subSession;
	private Topic topicChatApp;
	private TopicPublisher pub;
	private TopicSubscriber sub;
	private String userName;
    private List<String> blockedUsers = new ArrayList<>();
    
    public App(String user){
		userName = user;
		try {
			initJndi();
			connect();
			createPublisher();
			createSubscriber(buildSelector());
		} catch (NamingException | JMSException e) {
			e.printStackTrace();
		}
    }

    private void createSubscriber(String selector) throws JMSException {
		// true argument means that published messages will be consumed by the publisher
		// itself and by other subscribers. But we have selector, that will accept messages
		// from everybody, except the publisher.
		subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		sub = subSession.createSubscriber(topicChatApp, selector, true);
		sub.setMessageListener(message -> {
			if (message instanceof TextMessage){
				try {
					long time = message.getJMSTimestamp();
					Date date = new Date(time);
					String text = ((TextMessage) message).getText();
					System.out.println(date.toString() + "  " + text);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
	}
    
    private void initJndi() throws NamingException {
		Context jndi = new InitialContext();
		conFactory = (TopicConnectionFactory)jndi.lookup("ConFactory");
		topicChatApp = (Topic)jndi.lookup("topic-chatapp");
    }
    
    private void connect() throws JMSException {
		connection = conFactory.createTopicConnection();
		connection.start();
    }
    
    private void createPublisher() throws JMSException {
		pubSession = connection.createTopicSession(true, Session.AUTO_ACKNOWLEDGE);
		pub = pubSession.createPublisher(topicChatApp);
    }
    
    private String buildSelector() {
		StringBuilder selector = new StringBuilder();
		selector.append("Name <> '").append(userName).append("'");
		for (String blockedUser : blockedUsers) {
			selector.append(" AND Name <> '").append(blockedUser).append("'");
		}
		return selector.toString();
	}

    public static void main(String[] args) {
		try {
			if (args.length != 1)
				System.out.println("Wrong number of arguments, please provide user name only.");
			else {
				App chatApp = new App(args[0]);

				BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
				while(true){
					String text = input.readLine();
					if (text.equalsIgnoreCase("exit")){
						chatApp.close();
						break;
					} else if (text.equalsIgnoreCase("stop")){
						chatApp.stop();
					} else if (text.equalsIgnoreCase("start")){
						chatApp.start();
					} else if (text.equalsIgnoreCase("commit")){
						chatApp.commit();
					} else if (text.equalsIgnoreCase("rollback")){
						chatApp.rollback();
					} else if (text.matches("block [a-zA-Z]+")) {
						String userToBlock = text.substring("block ".length());
						chatApp.blockUser(userToBlock);
					} else if (text.matches("join [a-zA-Z]+")) {
						String topicToJoin = text.substring("join ".length());
						chatApp.join(topicToJoin);
					}
					else {
						chatApp.writeMessage(text);
					}
				}
			}
		} catch (JMSException | IOException e) {
			e.printStackTrace();
		}
    }
    
    private void writeMessage(String text) throws JMSException {
		TextMessage tm = pubSession.createTextMessage();
		tm.setText(userName + ": " + text);
		tm.setStringProperty("Name", userName);
		pub.send(tm);
	}

	private void close() throws JMSException {
		writeMessage("Goodbye!");
		commit();
		connection.close();
	}

	private void stop() throws JMSException {
		writeMessage("I'm going offline!");
		commit();
		connection.stop();
	}

	private void start() throws JMSException {
		writeMessage("I'm here again!");
		commit();
		connection.start();
	}

	private void commit() throws JMSException {
		pubSession.commit();
	}

	private void rollback() throws JMSException {
		pubSession.rollback();
	}

	private void blockUser(String userToBlock) throws JMSException{
		blockedUsers.add(userToBlock);
		subSession.close();
		createSubscriber(buildSelector());
	}

	private void join(String topicToJoin) throws JMSException {
		pubSession.createTemporaryTopic();
	}
}