Everything you need is at http://nextrtc.org/ ;)

# What is NextRTC?

NextRTC is a rich java library providing [WebRTC signaling](https://www.html5rocks.com/en/tutorials/webrtc/infrastructure/#what-is-signaling) server. You can use it as [standalone](https://github.com/mslosarz/nextrtc-example-wo-spring) web application, or add it as a [tenant](https://github.com/mslosarz/nextrtc-example-videochat) to your existing Spring application. It uses WebSocket (JSR 356) to communicate with clients. Front end client to NextRTC is available [here](https://github.com/mslosarz/nextrtc-js-client).

### What is needed to build this library on your own

NextRTC project use Lombok, so please be aware that you have to install Lombok plugin to you IDE.

# How to add NextRTC to your project?

NextRTC can be used in two modes **Standalone** or as a **Spring module**. Details about frontend client can be found [here](https://github.com/mslosarz/nextrtc-js-client). Both mode setup is described below.

## Code examples
There are 5 project that can interest you:
- Web chat using Spring (Spring WebSocket)
  - [Example1](https://github.com/mslosarz/nextrtc-example-videochat)
- Web chat using Tomcat (via Cargo plugin) without Spring but with websocket JSR-356 
  - [Example2](https://github.com/mslosarz/nextrtc-example-wo-spring)
- Web chat using RatPack without Spring with Netty WebSocket implementation
  - [Example3](https://github.com/mslosarz/nextrtc-example-ratpack)
- Web chat with Spring + SpringWebSocket + Spring Security
  - [Example4](https://github.com/mslosarz/nextrtc-example-spring-auth)
- [NextRTC JS Client](https://github.com/mslosarz/nextrtc-js-client)
- Sample js application that is used in the all examples 
  - [JS Sample Webapp](https://github.com/mslosarz/nextrtc-sample-webapp)

## Spring module mode
If you want to use NextRTC as a module of existing Spring based solution you have to add to you pom.xml file following entry:
```xml
<dependencies>
    <dependency>
        <groupId>org.nextrtc.signalingserver</groupId>
        <artifactId>nextrtc-signaling-server</artifactId>
        <version>${current-version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-websocket</artifactId>
    </dependency>
</dependencies>
```
Latest version of NextRTC can be found [here](https://mvnrepository.com/artifact/org.nextrtc.signalingserver/nextrtc-signaling-server).

Then you have to create a config where your endpoint will be defined
```java
@Configuration
@Import(NextRTCConfig.class)
public class MyWebSocketConfigurator implements WebSocketConfigurer {
    @Autowired
    private NextRTCServer server;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customEndpoint(), "/signaling").setAllowedOrigins("*");
    }

    @Bean
    public MyEndpoint customEndpoint() {
        return new MyEndpoint(server);
    }
}
```
And implement Spring WebSocket handler   
```java
public class MyEndpoint extends TextWebSocketHandler {
    private static class SessionWrapper implements Connection {

        private final WebSocketSession session;

        public SessionWrapper(WebSocketSession session) {
            this.session = session;
        }

        @Override
        public String getId() {
            return session.getId();
        }

        @Override
        public boolean isOpen() {
            return session.isOpen();
        }

        @Override
        public void sendObject(Object object) {
            try {
                session.sendMessage(new TextMessage(NextRTCServer.MessageEncoder.encode(object)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final NextRTCServer server;

    @Inject
    MyEndpoint(NextRTCServer server) {
        this.server = server;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        server.register(new SessionWrapper(session));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        server.handle(NextRTCServer.MessageDecoder.decode(message.getPayload()), new SessionWrapper(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        server.unregister(new SessionWrapper(session), status.getReason());
    }
}
```

That is all what you need to make NextRTC up and running (from the back-end point of view).
You can find working example [here](https://github.com/mslosarz/nextrtc-example-videochat).

### How to register own signal

In configuration class what you have to do is autowire SignalResolver and MessageSender (this one is required to send message to member).
```java
@Configuration
class Config {
    @Autowired
    private SignalResolver resolver;
    @Autowired
    private MessageSender sender;
}
```
Then in the same Config file you have to add bean which will add your handler to signal resolver. Handler has to implement interface `SignalHandler`. This interface has only one method with one parameter. This parameter has type `InternalMessage`. In parameter of this method you will always have `from` field with member that sent message. If your client provides in request destination member, field `to` will be filled with appropriate member.

That interface has only one method, so I this example I'll inline it to lambda expression:
```java
@Configuration
class Config {
    @Bean
    public Signal addCustomNextRTCHandlers(){
        Signal upperCase = Signal.fromString("upperCase");
        resolver.addCustomHandler(upperCase, (msg)-> 
            sender.send(InternalMessage.create()
                    .to(msg.getFrom())
                    .content(msg.getContent().toUpperCase())
                    .signal(upperCase)
                    .build())
        );
        return upperCase;
    }
}
```
 
In this example new handler (upperCase) will take a content of incoming message and resend the content to sender with uppercase letters.

### How to react on events
If you want to handle event that comes from server you have to create class that implements interface implements `NextRTCHandler`, and give annotation `@NextRTCEventListener(<event>)`. Class that implements interface and has EventListener annotation has to be registered in Spring container. Here is example with basic handler.
```java
@Component
@NextRTCEventListener(UNEXPECTED_SITUATION)
public class ExceptionHandler implements NextRTCHandler {
    private static final Logger log = Logger.getLogger(ExceptionHandler.class);
    @Override
    public void handleEvent(NextRTCEvent nextRTCEvent) {
        log.error(nextRTCEvent);
    }
}
```


## Standalone mode
If you want to use NextRTC in standalone mode you have to add it as a Maven dependency
```xml
<dependencies>
    <dependency>
        <groupId>org.nextrtc.signalingserver</groupId>
        <artifactId>nextrtc-signaling-server</artifactId>
        <version>${current-version}</version>
        <exclusions>
            <exclusion>
                <groupId>org.springframework</groupId>
                <artifactId>spring-context</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
</dependencies>
```
Latest version of NextRTC can be found [here](https://mvnrepository.com/artifact/org.nextrtc.signalingserver/nextrtc-signaling-server). Then you have to create NextRTCServer instance. And use it inside your WebSocket handler.
Below there are examples of usage that can be found [here (ratpack)](https://github.com/mslosarz/nextrtc-example-ratpack) and [here (standalone tomcat)](https://github.com/mslosarz/nextrtc-example-wo-spring).
```java
// RatPack implementation
public class RatPackWebSocketHandler implements WebSocketHandler<String> {
    private final NextRTCServer server;
    private RatPackConnection connection;

    private static class RatPackConnection implements Connection {
        private static final AtomicLong nextId = new AtomicLong(1);
        private final WebSocket socket;
        private String id;

        private RatPackConnection(WebSocket socket) {
            this.id = "0xx" + nextId.getAndIncrement();
            this.socket = socket;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public boolean isOpen() {
            return socket.isOpen();
        }

        @Override
        public void sendObject(Object object) {
            socket.send(NextRTCServer.MessageEncoder.encode(object));
        }
    }

    public RatPackWebSocketHandler(NextRTCServer server) {
        this.server = server;
    }

    @Override
    public String onOpen(WebSocket webSocket) throws Exception {
        connection = new RatPackConnection(webSocket);
        server.register(connection);
        return null;
    }

    @Override
    public void onClose(WebSocketClose<String> webSocketClose) throws Exception {
        server.unregister(connection, webSocketClose.getOpenResult());
    }

    @Override
    public void onMessage(WebSocketMessage<String> webSocketMessage) throws Exception {
        server.handle(webSocketMessage.getText(), connection);
    }
}
```
When you're using tomcat as a servlet container you'll need to add two dependencies (websocket-api and servlet-api)
```xml
<dependencies>
    <dependency>
        <groupId>javax.websocket</groupId>
        <artifactId>javax.websocket-api</artifactId>
        <version>1.1</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
        <version>3.1.0</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.nextrtc.signalingserver</groupId>
        <artifactId>nextrtc-signaling-server</artifactId>
        <version>${current-version}</version>
        <exclusions>
            <exclusion>
                <groupId>org.springframework</groupId>
                <artifactId>spring-context</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
</dependencies>
```
In standalone mode you probably have to add to your project directory `webapp/WEB-INF/web.xml` with content is similar to this provided below:
```xml
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
		 http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
</web-app>
```
And implement @ServerEndpoint as it was presented below
```java
@ServerEndpoint(value = "/signaling")
public class MyEndpoint {

    private static final Logger log = LoggerFactory.getLogger(MyEndpoint.class);
    private static final NextRTCServer server = NextRTCServer.create(configuration -> {
        configuration.nextRTCProperties().setPingPeriod(1);

        configuration.signalResolver().addCustomSignal(Signal.fromString("upperCase"), (msg) ->
                configuration.messageSender().send(InternalMessage.create()
                        .to(msg.getFrom())
                        .signal(Signal.fromString("upperCase"))
                        .content(msg.getContent() == null ? "" : msg.getContent().toUpperCase())
                        .build()));

        configuration.eventDispatcher().addListener(new CustomHandler());
        return configuration;
    });

    private static class SessionWrapper implements Connection {

        private final Session session;

        public SessionWrapper(Session session) {
            this.session = session;
        }

        @Override
        public String getId() {
            return session.getId();
        }

        @Override
        public boolean isOpen() {
            return session.isOpen();
        }

        @Override
        public void sendObject(Object object) {
            try {
                session.getBasicRemote().sendText(NextRTCServer.MessageEncoder.encode(object));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        server.register(new SessionWrapper(session));
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        server.handle(NextRTCServer.MessageDecoder.decode(message), new SessionWrapper(session));
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        server.unregister(new SessionWrapper(session), reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable exception) {
        server.handleError(new SessionWrapper(session), exception);
    }
}
```
Without `web.xml` servlet container sometimes doesn't scan classes and your Endpoint can be omitted during class loading.
You can find working example [here](https://github.com/mslosarz/nextrtc-example-wo-spring).

### How to register own signal

In manualConfiguration method if you want to provide your own signal you have to register it during server creation.
```java
NextRTCServer server = NextRTCServer.create(configuration -> {
    configuration.signalResolver().addCustomSignal(/* custom signal is going here */);
    return configuration;
});
```
Configuration object provides you ability to modify properties `configuration.nextRTCProperties()`, and it also provides you `configuration.signalResolver()`. To add your own signal you have to just add to signal resolver your own signal
 ```java
private static final NextRTCServer server = NextRTCServer.create(configuration -> {
    configuration.nextRTCProperties().setPingPeriod(1);

    configuration.signalResolver().addCustomSignal(Signal.fromString("upperCase"), (msg) ->
            configuration.messageSender().send(InternalMessage.create()
                    .to(msg.getFrom())
                    .signal(Signal.fromString("upperCase"))
                    .content(msg.getContent() == null ? "" : msg.getContent().toUpperCase())
                    .build()));

    configuration.eventDispatcher().addListener(new CustomHandler());
    return configuration;
});
}
```
In this example new handler (upperCase) will take a content of incoming message and resend the content to sender with uppercase letters.

### How to react on events
If you want to handle event that comes from server you have to create class that implements interface implements `NextRTCHandler`, and give annotation `@NextRTCEventListener(<event>)`. Class that implements interface and has EventListener annotation has to be registered during manual configuration. Here is example with basic handler.
```java
@NextRTCEventListener(UNEXPECTED_SITUATION)
public class ExceptionHandler implements NextRTCHandler {
    private static final Logger log = Logger.getLogger(ExceptionHandler.class);
    @Override
    public void handleEvent(NextRTCEvent nextRTCEvent) {
        log.error(nextRTCEvent);
    }
}
...
private static final NextRTCServer server = NextRTCServer.create(configuration -> {
    configuration.eventDispatcher().addListener(new ExceptionHandler());
    return configuration;
});
}
```

## How to send messages to client

When you will write your own signal, you probably will send a result of processing to some member of conversation. To do so you just have to do two things.
Firstly you have to find member to whom you want to send message. You can do it by autowiring `MemberRepository`. Bean that implement this interface will return member to whom you can easily send message.
```java
class SomeClass {
    @Autowired
    private MemberRepository members;
    
    public void fetchById(String id){
        Optional<Member> memberOptional = members.findBy(id);
    }
}
```
To be able to send a message to member you have to autowire `MessageSender`, then create an `InternalMessage` and send it.
```java
class SendMessage {
    @Autowired
    private MessageSender sender;
    
    public void sendTo(Member to){
        sender.send(InternalMessage.create()
            .to(to)
            .content("whatever you like")
            .signal(Signal.fromString("my_signal"))
            .build()); // method send will send message synchronously
    }   
}
```
In standalone mode you have to do the same. You can find MessageSender in EndpointConfiguration bean.
# Architecture of NextRTC - Overview

NextRTC uses event bus to communicate with other components. Internally NextRTC is taking request object then based on signal field is looking for appropriate handler. When handler exists it code is executed. During execution NextRTC can send messages to client via websocket and can produce predefined events on what you can react in your application.

## When your code can be run?

You can react on following events:
- SESSION_OPENED 
    * Is posted when client opens websocket channel
- SESSION_CLOSED
    * Is posted when client close websocket channel
- CONVERSATION_CREATED
    * Is posted when conversation has been created
- CONVERSATION_DESTROYED
    * Is posted when conversation has been destroyed
- UNEXPECTED_SITUATION
    * Is posted on every unexpected situation - broken pipe, forbidden action
- MEMBER_JOINED
    * Is posted when member has been added to conversation
- MEMBER_LEFT
    * Is posted when member has left the conversation
- MEDIA_LOCAL_STREAM_REQUESTED
    * Is posted after offerRequest / answerRequest signals are sent to clients
- MEDIA_LOCAL_STREAM_CREATED
    * Is posted when signal offerResponse / answerResponse arrives to server
- MEDIA_STREAMING
    * Is posted when clients exchanged all required signals to connect themselves by WebRTC
- TEXT
    * Is posted on each text message coming from client

 To be able to react on event you have to write java class which implements `NextRTCHandler` and has annotation `@NextRTCEventListener`. You can customize on what certain event your handler will react by giving it name to annotation `@NextRTCEventListener(UNEXPECTED_SITUATION)`. If you are using NextRTC with Spring then your handler should be also annotated with one of Spring stereotype annotations (@Service, @Component ...)

## Signal structure
Each request coming in and out of server have structure like this:
```json
{
    "from": "",
    "to": "",
    "signal": "",
    "content": "",
    "custom" : {}
}
```
custom can contains only string properties, deeper nesting will produce error. Valid request/response can look like this:
```json
{
    "from": "John",
    "to": "Alice",
    "signal": "TEXT",
    "content": "Hello Alice!",
    "custom" : {
        "myCustomHeader": "my custom value",
        "otherHeader": "value"
    }
}
```
Custom is used to determine version of conversation. When you are creating new conversation you can add to `custom` field `type` with value `MESH` or `BROADCAST`. Depending on value in type field signaling server will create different conversation type.

### Field `signal`
In signal field you can pass one of default defined signals:
- create
- join
- offerResponse
- answerResponse
- finalize
- candidate
- ping
- left
- text

Or give your own custom signal - how to define custom signals you can read [here]().
Server can send to messages with default predefined signals or with custom defined by you. Default group consists of:
- created
- joined
- newJoined
- text
- offerRequest
- answerRequest
- candidate
- ping
- error
- end

# Changes
If you want to check what has been done you can check [CHANGELOG](https://github.com/mslosarz/nextrtc-signaling-server/blob/master/CHANGELOG)