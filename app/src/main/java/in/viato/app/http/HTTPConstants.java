package in.viato.app.http;

/**
 * Created by ranadeep on 11/09/15.
 */
public class HTTPConstants {

    private enum Server {

        LOCAL("http://local:8080"),

        DEV("https://dev:8000"),

        PRODUCTION("https://prod:80");

        public final String mUrl;

        Server(final String url) {
            mUrl = url;
        }
    }

    private static Server SERVER = Server.PRODUCTION;
}
