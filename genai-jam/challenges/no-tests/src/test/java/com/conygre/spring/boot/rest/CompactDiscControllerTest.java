package com.conygre.spring.boot.rest;

import com.conygre.spring.boot.entities.CompactDisc;
import com.conygre.spring.boot.entities.Track;
import com.conygre.spring.boot.services.CompactDiscService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompactDiscController.class)
@ActiveProfiles("test")
class CompactDiscControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompactDiscService service;

    @Test
    void findAllReturnsSerializedCatalog() throws Exception {
        CompactDisc disc = createDisc(9, "Is This It", "The Strokes", 13.99, 11);
        disc.setTrackTitles(List.of(new Track(1, "Someday", 9)));
        when(service.getCatalog()).thenReturn(List.of(disc));

        mockMvc.perform(get("/api/compactdiscs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(9))
                .andExpect(jsonPath("$[0].title").value("Is This It"))
                .andExpect(jsonPath("$[0].artist").value("The Strokes"))
                .andExpect(jsonPath("$[0].trackTitles[0].title").value("Someday"));
    }

    @Test
    void findAllReturnsEmptyArrayWhenCatalogEmpty() throws Exception {
        when(service.getCatalog()).thenReturn(List.of());

        mockMvc.perform(get("/api/compactdiscs"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getCdByIdReturnsSerializedDisc() throws Exception {
        CompactDisc disc = createDisc(12, "White Ladder", "David Gray", 9.99, 10);
        when(service.getCompactDiscById(12)).thenReturn(disc);

        mockMvc.perform(get("/api/compactdiscs/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.title").value("White Ladder"))
                .andExpect(jsonPath("$.artist").value("David Gray"))
                .andExpect(jsonPath("$.tracks").value(10));
    }

    @Test
    void getCdByIdReturnsEmptyBodyWhenDiscMissing() throws Exception {
        when(service.getCompactDiscById(321)).thenReturn(null);

        mockMvc.perform(get("/api/compactdiscs/321"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void getByIdWith404ReturnsNotFoundWhenServiceReturnsNull() throws Exception {
        when(service.getCompactDiscById(404)).thenReturn(null);

        mockMvc.perform(get("/api/compactdiscs/404/404"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByIdWith404ReturnsDiscWhenFound() throws Exception {
        CompactDisc disc = createDisc(16, "Spice World", "Spice Girls", 4.99, 11);
        when(service.getCompactDiscById(16)).thenReturn(disc);

        mockMvc.perform(get("/api/compactdiscs/404/16"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(16))
                .andExpect(jsonPath("$.artist").value("Spice Girls"));
    }

    @Test
    void getCdByIdRejectsNonNumericId() throws Exception {
        mockMvc.perform(get("/api/compactdiscs/not-a-number"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addCdDelegatesToServiceWithRequestBody() throws Exception {
        CompactDisc disc = createDisc(99, "Parachutes", "Coldplay", 11.99, 10);

        mockMvc.perform(post("/api/compactdiscs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(disc)))
                .andExpect(status().isOk());

        ArgumentCaptor<CompactDisc> captor = ArgumentCaptor.forClass(CompactDisc.class);
        verify(service).addNewCompactDisc(captor.capture());
        CompactDisc capturedDisc = captor.getValue();
        assertEquals("Parachutes", capturedDisc.getTitle());
        assertEquals("Coldplay", capturedDisc.getArtist());
        assertEquals(10, capturedDisc.getTracks());
    }

    @Test
    void addCdBindsNestedTracksFromRequestBody() throws Exception {
        CompactDisc disc = createDisc(16, "Spice World", "Spice Girls", 4.99, 11);
        disc.setTrackTitles(List.of(new Track("Mama"), new Track("Wannabe")));

        mockMvc.perform(post("/api/compactdiscs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(disc)))
                .andExpect(status().isOk());

        ArgumentCaptor<CompactDisc> captor = ArgumentCaptor.forClass(CompactDisc.class);
        verify(service).addNewCompactDisc(captor.capture());
        CompactDisc capturedDisc = captor.getValue();
        assertEquals(2, capturedDisc.getTrackTitles().size());
        assertEquals("Mama", capturedDisc.getTrackTitles().get(0).getTitle());
        assertEquals("Wannabe", capturedDisc.getTrackTitles().get(1).getTitle());
    }

    @Test
    void addCdReturnsBadRequestForMalformedJson() throws Exception {
        mockMvc.perform(post("/api/compactdiscs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCdByIdDelegatesToService() throws Exception {
        mockMvc.perform(delete("/api/compactdiscs/14"))
                .andExpect(status().isOk());

        verify(service).deleteCompactDisc(14);
    }

    @Test
    void deleteCdByBodyDelegatesToService() throws Exception {
        CompactDisc disc = createDisc(14, "Echo Park", "Feeder", 13.99, 12);

        mockMvc.perform(delete("/api/compactdiscs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(disc)))
                .andExpect(status().isOk());

        ArgumentCaptor<CompactDisc> captor = ArgumentCaptor.forClass(CompactDisc.class);
        verify(service).deleteCompactDisc(captor.capture());
        CompactDisc capturedDisc = captor.getValue();
        assertNotNull(capturedDisc);
        assertEquals(14, capturedDisc.getId());
        assertEquals("Echo Park", capturedDisc.getTitle());
    }

    @Test
    void deleteCdByBodyReturnsBadRequestForMalformedJson() throws Exception {
        mockMvc.perform(delete("/api/compactdiscs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":"))
                .andExpect(status().isBadRequest());
    }

    private CompactDisc createDisc(int id, String title, String artist, double price, int tracks) {
        CompactDisc disc = new CompactDisc();
        disc.setId(id);
        disc.setTitle(title);
        disc.setArtist(artist);
        disc.setPrice(price);
        disc.setTracks(tracks);
        return disc;
    }
}
