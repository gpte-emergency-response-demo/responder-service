package com.redhat.cajun.navy.responder;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/responders")
public class RespondersController {

    @RequestMapping("/stats")
    public RespondersStats stats() {

        RespondersStats respondersStats = new RespondersStats();

        respondersStats.setTotal(100);
        respondersStats.setActive(50);

        return respondersStats;
    }

}
