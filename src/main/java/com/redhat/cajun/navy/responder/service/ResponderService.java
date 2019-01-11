package com.redhat.cajun.navy.responder.service;

import com.redhat.cajun.navy.responder.model.ResponderStats;
import com.redhat.cajun.navy.responder.model.Responder;

public interface ResponderService {

    ResponderStats getResponderStats();

    Responder getResponder(long id);

}
