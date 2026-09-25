package com.gianteyes.gaarigar.config;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.gianteyes.gaarigar.security.AccessPolicy;
import com.gianteyes.gaarigar.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.config.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.*;
import org.springframework.web.socket.config.annotation.*;
@Configuration @EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
 private final AccessPolicy access;private final UserRepository users;private final String secret;
 public WebsocketConfig(AccessPolicy access,UserRepository users,@Value("${jwt.secret}")String secret){this.access=access;this.users=users;this.secret=secret;}
 @Override public void configureMessageBroker(MessageBrokerRegistry c){c.setApplicationDestinationPrefixes("/app");c.enableSimpleBroker("/channel");}
 @Override public void registerStompEndpoints(StompEndpointRegistry r){r.addEndpoint("/chat");}
 @Override public void configureWebSocketTransport(WebSocketTransportRegistration r){r.setMessageSizeLimit(8192).setSendBufferSizeLimit(65536).setSendTimeLimit(10000);}
 @Override public void configureClientInboundChannel(ChannelRegistration registration){registration.interceptors(new ChannelInterceptor(){
  @Override public Message<?> preSend(Message<?> message,MessageChannel channel){
   StompHeaderAccessor a=MessageHeaderAccessor.getAccessor(message,StompHeaderAccessor.class);if(a==null)return message;
   if(a.getCommand()==StompCommand.CONNECT){
    try{String raw=a.getFirstNativeHeader("X-Authorization");var token=JWT.require(Algorithm.HMAC256(secret)).withIssuer("auth0").build().verify(raw);
     var u=users.findByPhone(token.getSubject()).orElseThrow();if(!Boolean.TRUE.equals(u.getIsActive())||token.getExpiresAt()==null||token.getClaim("role").asString()==null)throw new IllegalArgumentException();a.setUser(u::getPhone);
    }catch(Exception e){throw new org.springframework.security.access.AccessDeniedException("Invalid access token");}
   }
   if(a.getCommand()==StompCommand.SEND||a.getCommand()==StompCommand.SUBSCRIBE){
    String destination=a.getDestination();String pattern=a.getCommand()==StompCommand.SEND?"/app/chat/(\\d+)/(?:send|addSubscriber)":"/channel/(\\d+)";
    var match=java.util.regex.Pattern.compile(pattern).matcher(destination==null?"":destination);
    if(a.getUser()==null||!match.matches()||!access.chat(Long.valueOf(match.group(1)),a.getUser().getName()))throw new org.springframework.security.access.AccessDeniedException("Not a participant in this order");
   }return message;
  }
 });}
}
