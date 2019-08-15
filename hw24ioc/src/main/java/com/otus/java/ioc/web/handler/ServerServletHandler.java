package com.otus.java.ioc.web.handler;

import com.otus.java.ioc.web.servlet.UserServlet;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServerServletHandler extends ServletHandler {
    private final static String PATH = "/user";
    private final UserServlet userServlet;

    public ServerServletHandler(final @Autowired UserServlet userServlet) {
        this.userServlet = userServlet;
    }

    @PostConstruct
    public void init() {
        addServletWithMapping(new ServletHolder(userServlet), PATH);
    }
}
