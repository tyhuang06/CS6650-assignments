import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConnection {
  private static ConnectionFactory connectionFactory;
  private static Connection connection;
  private static String queueName = "album";
  private static Channel channel;
  private static String host = "localhost";
  private static int port = 5672;

  public RabbitMQConnection() {
    connectionFactory = new ConnectionFactory();
    connectionFactory.setHost(host);
    connectionFactory.setPort(port);

    try {
      connection = connectionFactory.newConnection();
      channel = connection.createChannel();
      channel.queueDeclare(queueName, false, false, false, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Channel getChannel() {
    return channel;
  }

  public Connection getConnection() {
    return connection;
  }

  public ConnectionFactory getConnectionFactory() {
    return connectionFactory;
  }

  public String getQueueName() {
    return queueName;
  }

  public Connection createConnection() {
    try {
      return connectionFactory.newConnection();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
