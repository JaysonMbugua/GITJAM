package com.conygre.spring.boot.rest;

import com.conygre.spring.boot.entities.CompactDisc;
import com.conygre.spring.boot.entities.Track;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CompactDiscApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getCatalogStartsEmpty() {
        ResponseEntity<CompactDisc[]> response = restTemplate.getForEntity("/api/compactdiscs", CompactDisc[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);
    }

    @Test
    void postGetAndDeleteByIdLifecycleWorks() {
        CompactDisc newDisc = createDisc("Parachutes", "Coldplay", 11.99, 10);

        ResponseEntity<Void> postResponse = restTemplate.postForEntity("/api/compactdiscs", newDisc, Void.class);
        ResponseEntity<CompactDisc[]> listResponse = restTemplate.getForEntity("/api/compactdiscs", CompactDisc[].class);

        assertEquals(HttpStatus.OK, postResponse.getStatusCode());
        assertNotNull(listResponse.getBody());
        assertEquals(1, listResponse.getBody().length);

        int generatedId = listResponse.getBody()[0].getId();
        ResponseEntity<CompactDisc> getResponse =
                restTemplate.getForEntity("/api/compactdiscs/" + generatedId, CompactDisc.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("Parachutes", getResponse.getBody().getTitle());
        assertEquals("Coldplay", getResponse.getBody().getArtist());

        restTemplate.delete("/api/compactdiscs/" + generatedId);

        ResponseEntity<CompactDisc> missingResponse =
                restTemplate.getForEntity("/api/compactdiscs/404/" + generatedId, CompactDisc.class);

        assertEquals(HttpStatus.NOT_FOUND, missingResponse.getStatusCode());
    }

    @Test
    void deleteByBodyRemovesPersistedDisc() {
        CompactDisc newDisc = createDisc("White Ladder", "David Gray", 9.99, 10);

        restTemplate.postForEntity("/api/compactdiscs", newDisc, Void.class);
        CompactDisc persistedDisc = getCatalog()[0];

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/compactdiscs",
                HttpMethod.DELETE,
                new HttpEntity<>(persistedDisc),
                Void.class);

        ResponseEntity<CompactDisc[]> listResponse = restTemplate.getForEntity("/api/compactdiscs", CompactDisc[].class);

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(listResponse.getBody());
        assertEquals(0, listResponse.getBody().length);
    }

    @Test
    void postWithTracksReturnsPersistedTracks() {
        CompactDisc newDisc = createDisc("Spice World", "Spice Girls", 4.99, 11);
        newDisc.setTrackTitles(List.of(new Track("Mama"), new Track("Wannabe")));

        ResponseEntity<Void> postResponse = restTemplate.postForEntity("/api/compactdiscs", newDisc, Void.class);
        CompactDisc persistedDisc = getCatalog()[0];
        ResponseEntity<CompactDisc> getResponse =
                restTemplate.getForEntity("/api/compactdiscs/" + persistedDisc.getId(), CompactDisc.class);

        assertEquals(HttpStatus.OK, postResponse.getStatusCode());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(2, getResponse.getBody().getTrackTitles().size());
        assertEquals(List.of("Mama", "Wannabe"),
                getResponse.getBody().getTrackTitles().stream().map(Track::getTitle).collect(Collectors.toList()));
    }

    private CompactDisc[] getCatalog() {
        ResponseEntity<CompactDisc[]> listResponse = restTemplate.getForEntity("/api/compactdiscs", CompactDisc[].class);
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertNotNull(listResponse.getBody());
        return listResponse.getBody();
    }

    private CompactDisc createDisc(String title, String artist, double price, int tracks) {
        CompactDisc disc = new CompactDisc();
        disc.setTitle(title);
        disc.setArtist(artist);
        disc.setPrice(price);
        disc.setTracks(tracks);
        return disc;
    }
}
