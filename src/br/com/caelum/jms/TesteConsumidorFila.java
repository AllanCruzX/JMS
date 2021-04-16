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
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class TesteConsumidorFila {
	
	public static void main(String[] args) {
		
		 try {
			 
		InitialContext context = new InitialContext(); 
		//ConnectionFactory -> vem do MOM (ActiveMQ)
        ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
        
        
        Connection connection = factory.createConnection();
       	connection.start();       	
       	
      
       	//session -> para criar um consumer não podemos usar diretamente a conexão  precisamos de um objeto session esse obeto ssesion cria o cusumer . (A ssesion abstari a parte do trabalho trasacional e a parte de confirmação de recebimento da menssagem) 
      //AUTO_ACKNOWLEDGE -> controle programático 
       	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
       	//Destination -> carrega a fila criada no ActiveMQ (É o lugar concreto onde a mensagem será salvo dentro do MOM.)
       	Destination fila = (Destination) context.lookup("financeiro");
       	//consumer -> fica escutando a fila 
       	MessageConsumer consumer = session.createConsumer(fila);
       	
       	// consumer.setMessageListener ->ele fique escutando o tempo todo por mensagens, e para isso , devemos cadastrar um novo objeto no nosso consumer, com o responsabilidade de tratar as mensagens que recebemos.
       	consumer.setMessageListener(new MessageListener(){

       	    @Override
       	    public void onMessage(Message message){
       	     TextMessage textMessage  = (TextMessage)message;
             try{
                 System.out.println(textMessage.getText());
             } catch(JMSException e){
                 e.printStackTrace();
             }
       	    }

       	});
 

       new Scanner(System.in).nextLine(); //parar o programa para testar a conexao

        connection.close();
        context.close();
        
        } catch (JMSException | NamingException e) {
			e.printStackTrace();
		}
		
	}
	
	   private void ComsumidorEspecificoParaFila() {
		   try {
	    	
	    	 InitialContext ctx = new InitialContext();
	         QueueConnectionFactory cf = (QueueConnectionFactory)ctx.lookup("ConnectionFactory");
	         QueueConnection conexao = cf.createQueueConnection();
	         conexao.start();

	         QueueSession sessao = conexao.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
	         Queue fila = (Queue) ctx.lookup("financeiro");
	         QueueReceiver receiver = (QueueReceiver) sessao.createReceiver(fila );

	         Message message;
			
				message = receiver.receive();
		
	         System.out.println(message);

	         new Scanner(System.in).nextLine();

	         sessao.close();
	         conexao.close();    
	         ctx.close();
			} catch (JMSException | NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

}
