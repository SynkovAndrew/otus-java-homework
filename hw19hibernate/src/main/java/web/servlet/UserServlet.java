package web.servlet;

import domain.User;
import service.DbServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UserServlet extends HttpServlet {
    private final DbServiceImpl<User> dbService;

    public UserServlet(final DbServiceImpl<User> dbService) {
        this.dbService = dbService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final List<User> users = dbService.loadAll(User.class);

        resp.setContentType("");

    }
}
