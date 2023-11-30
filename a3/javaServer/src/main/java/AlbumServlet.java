import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(name = "AlbumServlet", value = "/albums/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,    // 10 MB
        maxFileSize = 1024 * 1024 * 50,        // 50 MB
        maxRequestSize = 1024 * 1024 * 100)    // 100 MB
public class AlbumServlet extends HttpServlet {
  private Gson gson = new Gson();
  private DbService dbService = new DbService();

  @Override
  public void init() {
    this.dbService = new DbService();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("missing parameters");
      return;
    }

    // validate url path and return the response status code
    if (!isUrlValid(urlPath, "GET")) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("key not found");
      return;
    }

    res.setStatus(HttpServletResponse.SC_OK);
    // get album by id
    String json = this.dbService.getAlbumById(urlPath.substring(1));
    res.getWriter().write(json);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    res.setContentType("application/json");

    String urlPath = req.getPathInfo();

    // and now validate url path and return the response status code
    if (!isUrlValid(urlPath, "POST")) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      res.getWriter().write("invalid request");
      return;
    }

    // Check we have a valid image part
    Part image = req.getPart("image");
    if (image == null || !image.getContentType().startsWith("image/")) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      res.getWriter().write("invalid image");
      return;
    }

    res.setStatus(HttpServletResponse.SC_OK);

    // Create new album object
    Album album = new Album("Sex Pistols", "Never Mind The Bollocks!", 1977);

    // Get image information
    try (InputStream stream = image.getInputStream()) {
      int size = stream.available();
      byte[] array = new byte[size];

      // update image
      stream.read(array);

      // post album to db
      String id = this.dbService.postAlbum(array, album);

      res.getWriter().write(id);
    } catch (IOException e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      res.getWriter().write("internal server error");
    }
  }

  // Another servlet endpoint to do like/dislike requests
  

  private boolean isUrlValid(String urlPath, String method) {
    return true;
  }
}
