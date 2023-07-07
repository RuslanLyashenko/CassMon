package ua.ubki.cassmon.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TestService {
    private static final Logger logger = LoggerFactory.getLogger(TestService.class);

    @Autowired
    private CassandraService cassandraService;

    @Autowired
    private NodeService nodeService;

    @EventListener(ApplicationReadyEvent.class)
    public void test() {

    }

}
