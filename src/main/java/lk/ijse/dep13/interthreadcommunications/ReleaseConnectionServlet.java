package lk.ijse.dep13.interthreadcommunications;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "release-connection-servlet", urlPatterns = "/connections/*")
public class ReleaseConnectionServlet extends HttpServlet {
    @Override
    protected void doDelete ( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
        super.doDelete ( req, resp );
    }
}
