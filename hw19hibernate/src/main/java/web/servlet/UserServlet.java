package web.servlet;

import com.google.gson.Gson;
import domain.User;
import service.DbServiceImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static java.util.Optional.ofNullable;

public class UserServlet extends HttpServlet {
    private final Gson gson;
    private final DbServiceImpl<User> dbService;

    public UserServlet(final DbServiceImpl<User> dbService) {
        this.dbService = dbService;
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final List<User> users = dbService.loadAll();
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        final PrintWriter writer = resp.getWriter();
        writer.print(gson.toJson(users));
        writer.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        final String name = req.getParameter("name");
        final Integer age = ofNullable(req.getParameter("age")).map(Integer::valueOf).orElse(null);
        dbService.create(
                User.builder()
                        .name(name)
                        .age(age)
                        .build()
        );
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
