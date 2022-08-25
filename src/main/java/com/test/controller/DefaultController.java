package com.test.controller;

import com.test.audit.Auditable;
import com.test.entities.Party;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DefaultController {

  @Value("${spring.application.name}")
  private String name;

  @Value("${spring.profiles.active:Unknown}")
  private String activeProfile;

  @GetMapping("/")
  @Auditable
  public String index() {
    log.info(" Default controller called");
    return name + " service is running. The current active environment is " + activeProfile;
  }

  @PostMapping("/greet")
  @Auditable( objectId = "#party.partyId")
  public ResponseEntity<String> greet(@RequestBody Party party) {
    log.info(" Party Invoked with Party Id {}", party.getPartyId());
    return new ResponseEntity<>("Party Invoked with Party Id " + party.getPartyId(), HttpStatus.OK);
  }

}
