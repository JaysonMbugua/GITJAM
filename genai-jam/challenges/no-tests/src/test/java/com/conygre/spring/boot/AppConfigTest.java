package com.conygre.spring.boot;

import com.conygre.spring.boot.rest.CompactDiscController;
import com.conygre.spring.boot.repos.CompactDiscRepository;
import com.conygre.spring.boot.services.CompactDiscService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import springfox.documentation.spring.web.plugins.Docket;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class AppConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CompactDiscService compactDiscService;

    @Autowired
    private CompactDiscRepository compactDiscRepository;

    @Autowired
    private CompactDiscController compactDiscController;

    @Test
    void contextLoadsWithCoreBeans() {
        assertNotNull(compactDiscService);
        assertNotNull(compactDiscRepository);
        assertNotNull(compactDiscController);
    }

    @Test
    void testProfileExcludesSwaggerDocketBean() {
        assertTrue(applicationContext.getBeansOfType(Docket.class).isEmpty());
    }
}
