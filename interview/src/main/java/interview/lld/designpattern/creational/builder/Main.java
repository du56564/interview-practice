package interview.lld.designpattern.creational.builder;

public class Main {
    static void main() {
        HttpRequest request = new HttpRequest.Builder("")
                .method("POST")
                .body("{\"key\":\"value\"}")
                .timeout(15000)
                .build();
        System.out.println(request.toString());
    }
}
