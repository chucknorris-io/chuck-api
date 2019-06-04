package io.chucknorris.api.lib.event;

import java.io.Serializable;

public class BaseEvent implements Event, Serializable {
    private String name;
    private Object payload;

    public BaseEvent(String name, Object payload) {
        this.name = name;
        this.payload = payload;
    }

    public String getName() {
        return name;
    }

    public Object getPayload() {
        return payload;
    }
}
