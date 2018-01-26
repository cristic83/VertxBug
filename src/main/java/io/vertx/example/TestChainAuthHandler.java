package io.vertx.example;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.shiro.ShiroAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BasicAuthHandler;
import io.vertx.ext.web.handler.ChainAuthHandler;
import org.apache.shiro.realm.SimpleAccountRealm;

public class TestChainAuthHandler {

    public static void main(String[] args) {
        // Create an HTTP server which simply returns "Hello World!" to each request.
        System.out.println("Starting oauth client ");
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);

        ChainAuthHandler chainAuthHandler = ChainAuthHandler.create();

        SimpleAccountRealm systemRealm = createRealm("system", "system-pass");
        systemRealm.addRole("system");

        SimpleAccountRealm userRealm = createRealm("user", "user-pass");
        userRealm.addRole("user");

        chainAuthHandler.append(BasicAuthHandler.create(ShiroAuth.create(vertx, userRealm)));
        chainAuthHandler.append(BasicAuthHandler.create(ShiroAuth.create(vertx, systemRealm)));

        chainAuthHandler.addAuthority("system");
        router.route("/")
                .handler(chainAuthHandler)
                .handler(rc -> rc.response().end("Only system users should see it"));

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(8282);
    }


    private static SimpleAccountRealm createRealm(String username, String password) {

        SimpleAccountRealm realm = new SimpleAccountRealm();
        realm.addAccount(username, password);

        return realm;
    }

}
