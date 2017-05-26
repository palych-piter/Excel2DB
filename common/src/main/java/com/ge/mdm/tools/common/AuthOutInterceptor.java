package com.ge.mdm.tools.common;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AuthOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger logger = LoggerFactory.getLogger(AuthOutInterceptor.class);

    private static final String DEFAULT_USERNAME_HEADER_NAME = "USERNAME";
    private static final String DEFAULT_PASSWORD_HEADER_NAME = "PASSWORD";

    private String username;
    private String password;
    private String usernameHeaderName = DEFAULT_USERNAME_HEADER_NAME;
    private String passwordHeaderName = DEFAULT_PASSWORD_HEADER_NAME;


    public AuthOutInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(usernameHeaderName, Collections.singletonList(username));
        headers.put(passwordHeaderName, Collections.singletonList(password));
        message.put(Message.PROTOCOL_HEADERS, headers);
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsernameHeaderName() {
        return usernameHeaderName;
    }

    public void setUsernameHeaderName(String usernameHeaderName) {
        this.usernameHeaderName = usernameHeaderName;
    }

    public String getPasswordHeaderName() {
        return passwordHeaderName;
    }

    public void setPasswordHeaderName(String passwordHeaderName) {
        this.passwordHeaderName = passwordHeaderName;
    }
}

