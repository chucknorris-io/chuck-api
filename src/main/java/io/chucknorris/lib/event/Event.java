package io.chucknorris.lib.event;

import java.io.Serializable;

public interface Event extends Serializable {

  String getName();

  Object getPayload();
}
