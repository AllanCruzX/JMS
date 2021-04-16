package br.com.caelum.jms;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
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

import br.com.caelum.modelo.Pedido;

public class TesteConsumidorTopicoComercial {

	public static void main(String[] args) {

		try {

			InitialContext context = new InitialContext();
			// ConnectionFactory -> vem do MOM (ActiveMQ)
			ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

			Connection connection = factory.createConnection();

			// setClientID ->identificar a conex�o
			connection.setClientID("comercial");

			connection.start();

			// session -> para criar um consumer n�o podemos usar diretamente a conex�o
			// precisamos de um objeto session esse obeto ssesion cria o cusumer . (A
			// ssesion abstari a parte do trabalho trasacional e a parte de confirma��o de
			// recebimento da menssagem)
			//AUTO_ACKNOWLEDGE -> controle program�tico 
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Destination -> carrega a fila criada no ActiveMQ (� o lugar concreto onde a
			// mensagem ser� salvo dentro do MOM.)
			Topic topico = (Topic) context.lookup("loja");

			// consumer -> fica escutando a fila / createDurableSubscriber -> cria uma
			// assinatura duravel para identificar o consumidor
			MessageConsumer consumer = session.createDurableSubscriber(topico, "assinatura");

			// consumer.setMessageListener ->ele fique escutando o tempo todo por mensagens,
			// e para isso , devemos cadastrar um novo objeto no nosso consumer, com o
			// responsabilidade de tratar as mensagens que recebemos.
			consumer.setMessageListener(new MessageListener() {

				@Override
				public void onMessage(Message message) {
					//TextMessage textMessage = (TextMessage) message;
					ObjectMessage objectMessage = (ObjectMessage)message;

					try {
						Pedido pedido = (Pedido) objectMessage.getObject();
				        System.out.println(pedido.getCodigo());
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
