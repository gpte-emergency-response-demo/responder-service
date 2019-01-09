package com.redhat.cajun.navy.responder;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/responders")
public class RespondersController {

    @RequestMapping("/stats")
    public ResponderStats stats() {

        ResponderStats responderStats = new ResponderStats();

        responderStats.setTotal(100);
        responderStats.setActive(50);

        return responderStats;
    }

}
