package org.revature.revado.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController// bean name will be healthController
public class HealthController {

    /*public ReturnType methodName() {
    }*/
    @GetMapping("/health")
    public String getHealthOfApplication() {
        return "Revado Application Is UP";
    }


    //Alternative with @RequestMapping:
    /*@RequestMapping(value = "/health", method = RequestMethod.GET)
    public String getHealthOfApplication1() {
        return "UP";
    }*/

}
