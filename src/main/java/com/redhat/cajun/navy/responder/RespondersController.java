package com.redhat.cajun.navy.responder;

import com.redhat.cajun.navy.responder.model.Responder;
import com.redhat.cajun.navy.responder.service.ResponderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/responders")
public class RespondersController {

    @Autowired
    private ResponderService responderService;

    @RequestMapping(value = "/stats", method = RequestMethod.GET, produces = "application/json")
    public ResponderStats stats() {

        ResponderStats responderStats = responderService.getResponderStats();
        return responderStats;
    }

    @RequestMapping(value = "/responder/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Responder> responder(@PathVariable long id) {
        Responder responder = responderService.getResponder(id);
        if (responder == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(responder, HttpStatus.OK);
        }
    }

}
