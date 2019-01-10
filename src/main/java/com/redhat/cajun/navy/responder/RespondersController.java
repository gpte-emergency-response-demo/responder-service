package com.redhat.cajun.navy.responder;

import com.redhat.cajun.navy.responder.service.ResponderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/responders")
public class RespondersController {

    @Autowired
    private ResponderService responderService;

    @RequestMapping("/stats")
    public ResponderStats stats() {

        ResponderStats responderStats = responderService.getResponderStats();
        return responderStats;
    }

}
