package com.redhat.cajun.navy.responder.service;

import java.util.List;

import com.redhat.cajun.navy.responder.model.Responder;
import com.redhat.cajun.navy.responder.model.ResponderStats;

public interface ResponderService {

    ResponderStats getResponderStats();

    Responder getResponder(long id);

    List<Responder> availableResponders();
}
