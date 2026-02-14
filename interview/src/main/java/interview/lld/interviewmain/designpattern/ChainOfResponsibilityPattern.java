package interview.lld.interviewmain.designpattern;

/*
The Chain of Responsibility Design Pattern is a behavioral pattern that lets you pass requests along a chain of handlers,
allowing each handler to decide whether to process the request or pass it to the next handler in the chain.

Request -> Handler1 -> Handler2 -> Handler3 -> Business Logic
skip if-else/switcg
For example:
    - A logging system might write to the console, file, or remote server depending on configuration,
    - HTTP request might need to go through validation, authentication, and rate-limiting steps.

Core Entity:
    - Client
    - Handler (Interface)
    - AuthHandler, RateLimitHandler

    Handler
        - next: Handler
        + setNext(h : Handler)
        + handle(request)

 */

class Request {
    private String user;
    private String role;
    private int requestCount;
    private String payload;
    public Request(String user, String role, int requestCount, String payload) {
        this.user = user;
        this.role = role;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}

interface RequestHandler {
    void setNext(RequestHandler next);
    void handle(Request request);
}

abstract class BaseHandler implements RequestHandler {
    protected RequestHandler next;

    @Override
    public void setNext(RequestHandler next) {
        this.next = next;
    }

    protected void forward(Request request) {
        if (next!=null) {
            next.handle(request);
        }
    }
}

class AuthHandler extends BaseHandler {
    @Override
    public void handle(Request request) {
        if (request.getUser() == null) {
            System.out.println("AuthHandler: User not authenticated.");
            return; // Stop the chain
        }
        System.out.println("AuthHandler: Authenticated.");
        //next.handle(request);
        forward(request);
    }
}

class AuthorizationHandler extends BaseHandler {

    @Override
    public void handle(Request request) {
        if (!"ADMIN".equals(request.getRole())) {
            System.out.println("AuthorizationHandler:Access denied.");
        }
        System.out.println("AuthorizationHandler: Authorized");
        forward(request);
    }
}

class RateLimitHandler extends BaseHandler {
    @Override
    public void handle(Request request) {
        if (request.getRequestCount() >= 100) {
            System.out.println("RateLimitHandler: Rate limit exceeded.");
            return;
        }
        System.out.println("RateLimitHandler: Within rate limit.");
        forward(request);
    }
}

class ValidationHandler extends BaseHandler {
    @Override
    public void handle(Request request) {
        if (request.getPayload() == null || request.getPayload().trim().isEmpty()) {
            System.out.println("ValidationHandler: Invalid payload.");
            return;
        }
        System.out.println("ValidationHandler: Payload valid.");
        forward(request);
    }
}

class BusinessLogicHandler extends BaseHandler {
    @Override
    public void handle(Request request) {
        System.out.println("BusinessLogicHandler: 🚀 Processing request...");
        // Core application logic goes here
    }
}

class ChainOfResponsibility {
    static void main() {
        // Create handlers
        RequestHandler auth = new AuthHandler();
        RequestHandler authorization = new AuthorizationHandler();
        RequestHandler rateLimit = new RateLimitHandler();
        RequestHandler validation = new ValidationHandler();
        RequestHandler businessLogic = new BusinessLogicHandler();

        // Build the chain
        auth.setNext(authorization);
        authorization.setNext(rateLimit);
        rateLimit.setNext(validation);
        validation.setNext(businessLogic);

        // Send a request through the chain
        Request request = new Request("john", "ADMIN", 10, "{ \"data\": \"valid\" }");
        auth.handle(request);

        System.out.println("\n--- Trying an invalid request ---");
        Request badRequest = new Request(null, "USER", 150, "");
        auth.handle(badRequest);

    }
}
