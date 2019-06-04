package io.chucknorris.api.lib.event;

import java.io.Serializable;

public interface Event extends Serializable {
    String getName();

    Object getPayload();
}
