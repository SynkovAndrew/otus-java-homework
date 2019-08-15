package com.otus.java.ioc.web.handler;


import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.util.security.Constraint;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;

import static java.util.Optional.ofNullable;

@Component
public class ServerSecurityHandler extends ConstraintSecurityHandler {
    @PostConstruct
    public void init() {
        final var resource = ServerSecurityHandler.class.getClassLoader().getResource("realm.properties");
        ofNullable(resource)
                .ifPresentOrElse(res -> {
                    final var constraint = new Constraint("auth", "admin");
                    constraint.setAuthenticate(true);
                    final var constraintMapping = new ConstraintMapping();
                    constraintMapping.setPathSpec("/*");
                    constraintMapping.setConstraint(constraint);
                    setAuthenticator(new BasicAuthenticator());
                    setLoginService(new HashLoginService("MyRealm", res.getPath()));
                    setConstraintMappings(Collections.singletonList(constraintMapping));
                }, () -> {
                    throw new RuntimeException("Realm properties is not found!");
                });
    }
}
