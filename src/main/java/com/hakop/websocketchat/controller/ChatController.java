package com.hakop.websocketchat.controller;

import com.hakop.websocketchat.cache.SocketCache;
import com.hakop.websocketchat.model.Greeting;
import com.hakop.websocketchat.model.HelloMessage;
import com.hakop.websocketchat.model.SocketSessionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import org.webjars.NotFoundException;

import java.security.Principal;
import java.util.Collections;

@RestController("/")
public class ChatController {
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private SocketCache socketCache;

    @MessageMapping("/hello")
//	@SendTo("/topic/greetings")
    public void greeting(@Payload HelloMessage message, @Header("simpSessionId") String sessionId, Principal principal) {
        System.out.println(principal);
        socketCache.add(message.getName(),
                principal == null ? null : principal.getName(),
                sessionId);
    }

    @PostMapping("/action")
    public Greeting doAction(@RequestParam("user_name") final String userName) {

        SocketSessionInfo socketUser = socketCache.getUserSession(userName);
        if (socketUser == null) {
            throw new NotFoundException(String.format("User with name '%s' not exist.", userName));
        }

        Greeting greeting = new Greeting("Hello, " + HtmlUtils.htmlEscape(userName) + "!");

        messagingTemplate.convertAndSendToUser(
//                UUID.randomUUID().toString(), if set random user id, then nobody receipts message
				socketUser.getUserId(),
                "/topic/greetings", greeting,
                Collections.singletonMap("simpSessionId", socketUser.getSessionId()));
        return greeting;
    }

    // Обработчик ошибок
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
