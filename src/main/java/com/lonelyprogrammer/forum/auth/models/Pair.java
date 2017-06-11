package com.lonelyprogrammer.forum.auth.models;

/**
 * Created by nikita on 11.06.17.
 */
public class Pair<X, Y> {
    public final X x;
    public final Y y;
    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}
