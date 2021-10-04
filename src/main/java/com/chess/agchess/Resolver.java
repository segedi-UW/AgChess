package com.chess.agchess;

import java.net.URL;

public class Resolver {

    public static String toURL(String resource) {
        URL url = Resolver.class.getResource(resource);
        if (url == null) throw new NullPointerException(resource + " was not found");
        return url.toExternalForm();
    }
}
