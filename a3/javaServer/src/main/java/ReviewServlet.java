import com.google.gson.Gson;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ReviewServlet", value = "/reviews/*")
public class ReviewServlet extends HttpServlet {
  private Gson gson = new Gson();
  private DbService dbService = new DbService();
  private RabbitMQConnection rabbitMQConnection;
  private int numThreads = 20;

  @Override
  public void init() throws ServletException {
    super.init();
    this.dbService = new DbService();
    this.rabbitMQConnection = new RabbitMQConnection();

    // setup rabbitmq runnable
    Runnable runnable = () -> {
      try {
        Channel channel = rabbitMQConnection.createConnection().createChannel();
        channel.queueDeclare(rabbitMQConnection.getQueueName(), false, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
          try {
            deliver(delivery);
          } catch (Exception e) {
            e.printStackTrace();
          }
        };

        channel.basicQos(50000);
        channel.basicConsume(rabbitMQConnection.getQueueName(), false, deliverCallback, consumerTag -> {
        });
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };

    for (int i = 0; i < numThreads; i++) {
      new Thread(runnable).start();
    }
  }

  private void deliver(Delivery delivery) {
    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
    String[] messageParts = message.split(",");
    String albumId = messageParts[0];
    int value = Integer.parseInt(messageParts[1]);

    this.dbService.postLike(albumId, value);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.setContentType("application/json");

    String urlPath = req.getPathInfo();

    // and now validate url path and return the response status code
//    if (!isUrlValid(urlPath, "POST")) {
//      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//      res.getWriter().write("key not found");
//      return;
//    }

    // post with rabbitmq
    Channel channel = rabbitMQConnection.getChannel();
    String likeOrDislike = urlPath.substring(1);
    String albumId = urlPath.substring(2);

    if (likeOrDislike.equals("like")) {
      channel.basicPublish("", rabbitMQConnection.getQueueName(), null, (albumId + ",1").getBytes());
    } else if (likeOrDislike.equals("dislike")) {
      channel.basicPublish("", rabbitMQConnection.getQueueName(), null, (albumId + ",-1").getBytes());
    }

    res.setStatus(HttpServletResponse.SC_OK);
    res.getWriter().write("success");
  }
}
