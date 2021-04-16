package br.com.caelum.jms;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class TesteConsumidorTopicoEstoqueSelector {

	public static void main(String[] args) {

		try {

			InitialContext context = new InitialContext();
			// ConnectionFactory -> vem do MOM (ActiveMQ)
			ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

			Connection connection = factory.createConnection();

			// setClientID ->identificar a conexão
			connection.setClientID("estoque");

			connection.start();

			// session -> para criar um consumer não podemos usar diretamente a conexão
			// precisamos de um objeto session esse obeto ssesion cria o cusumer . (A
			// ssesion abstari a parte do trabalho trasacional e a parte de confirmação de
			// recebimento da menssagem)
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Destination -> carrega a fila criada no ActiveMQ (É o lugar concreto onde a
			// mensagem será salvo dentro do MOM.)
			Topic topico = (Topic) context.lookup("loja");

			// consumer -> fica escutando a fila / createDurableSubscriber -> cria uma
			// assinatura duravel para identificar o consumidor
			//"ebook= false" =>Consumo condicional de mensagens (Só permite identificar HEAD "Não consigo fazer condicional no corpo da mensagem")
			//
			MessageConsumer consumer = session.createDurableSubscriber(topico, "assinatura-selector" ,"ebook is null OR ebook= false", false);

			// consumer.setMessageListener ->ele fique escutando o tempo todo por mensagens,
			// e para isso , devemos cadastrar um novo objeto no nosso consumer, com o
			// responsabilidade de tratar as mensagens que recebemos.
			consumer.setMessageListener(new MessageListener() {

				@Override
				public void onMessage(Message message) {
					TextMessage textMessage = (TextMessage) message;
					try {
						System.out.println(textMessage.getText());
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}

			});

			new Scanner(System.in).nextLine(); // parar o programa para testar a conexao

			session.close();
			connection.close();
			context.close();

		} catch (JMSException | NamingException e) {
			e.printStackTrace();
		}

	}

}
