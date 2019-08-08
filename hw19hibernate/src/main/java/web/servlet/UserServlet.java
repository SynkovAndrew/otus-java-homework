package web.servlet;

import domain.User;
import service.DbServiceImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class UserServlet extends HttpServlet {
    private final DbServiceImpl<User> dbService;

    public UserServlet(final DbServiceImpl<User> dbService) {
        this.dbService = dbService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final List<User> users = dbService.loadAll();
        final String content = "[" + users.stream().map(User::toString).collect(Collectors.joining(", ")) + "]";

        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        final PrintWriter writer = resp.getWriter();
        writer.print(content);
        writer.flush();
    }
}
