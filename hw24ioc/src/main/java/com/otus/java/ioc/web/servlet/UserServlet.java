package com.otus.java.ioc.web.servlet;

import com.google.gson.Gson;
import com.otus.java.ioc.service.UserService;
import domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static java.util.Optional.ofNullable;

@Component
public class UserServlet extends HttpServlet {
    private final Gson gson;
    private final UserService userService;

    @Autowired
    public UserServlet(final UserService userService, final Gson gson) {
        this.userService = userService;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final List<User> users = userService.loadAll();
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
        final User user = User.builder()
                .name(name)
                .age(age)
                .build();
        userService.create(user);
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        final PrintWriter writer = resp.getWriter();
        writer.print(gson.toJson(user));
        writer.flush();
    }
}
