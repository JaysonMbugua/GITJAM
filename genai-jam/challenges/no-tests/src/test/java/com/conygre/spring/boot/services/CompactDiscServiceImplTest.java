package com.conygre.spring.boot.services;

import com.conygre.spring.boot.entities.CompactDisc;
import com.conygre.spring.boot.repos.CompactDiscRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CompactDiscServiceImplTest {

    @Mock
    private CompactDiscRepository dao;

    @InjectMocks
    private CompactDiscServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getCatalogReturnsRepositoryResults() {
        List<CompactDisc> expectedCatalog = List.of(createDisc(1, "Parachutes", "Coldplay", 11.99, 10));
        when(dao.findAll()).thenReturn(expectedCatalog);

        Iterable<CompactDisc> actualCatalog = service.getCatalog();

        assertSame(expectedCatalog, actualCatalog);
        verify(dao).findAll();
    }

    @Test
    void getCatalogReturnsEmptyCollectionWhenRepositoryEmpty() {
        List<CompactDisc> expectedCatalog = List.of();
        when(dao.findAll()).thenReturn(expectedCatalog);

        Iterable<CompactDisc> actualCatalog = service.getCatalog();

        assertSame(expectedCatalog, actualCatalog);
        verify(dao).findAll();
    }

    @Test
    void getCompactDiscByIdReturnsDiscWhenPresent() {
        CompactDisc expectedDisc = createDisc(9, "Is This It", "The Strokes", 13.99, 11);
        when(dao.findById(9)).thenReturn(Optional.of(expectedDisc));

        CompactDisc actualDisc = service.getCompactDiscById(9);

        assertSame(expectedDisc, actualDisc);
        verify(dao).findById(9);
    }

    @Test
    void getCompactDiscByIdReturnsNullWhenMissing() {
        when(dao.findById(404)).thenReturn(Optional.empty());

        CompactDisc actualDisc = service.getCompactDiscById(404);

        assertNull(actualDisc);
        verify(dao).findById(404);
    }

    @Test
    void addNewCompactDiscResetsIdBeforeSaving() {
        CompactDisc discToSave = createDisc(27, "White Ladder", "David Gray", 9.99, 10);
        when(dao.save(any(CompactDisc.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompactDisc savedDisc = service.addNewCompactDisc(discToSave);

        assertSame(discToSave, savedDisc);
        assertEquals(0, savedDisc.getId());
        verify(dao).save(discToSave);
    }

    @Test
    void addNewCompactDiscReturnsRepositorySavedEntity() {
        CompactDisc discToSave = createDisc(88, "Just Enough Education to Perform", "Stereophonics", 10.99, 11);
        CompactDisc persistedDisc = createDisc(5, "Just Enough Education to Perform", "Stereophonics", 10.99, 11);
        when(dao.save(discToSave)).thenReturn(persistedDisc);

        CompactDisc savedDisc = service.addNewCompactDisc(discToSave);

        assertSame(persistedDisc, savedDisc);
        assertEquals(0, discToSave.getId());
        verify(dao).save(discToSave);
    }

    @Test
    void updateCompactDiscSavesProvidedDisc() {
        CompactDisc discToUpdate = createDisc(12, "Echo Park", "Feeder", 13.99, 12);
        when(dao.save(discToUpdate)).thenReturn(discToUpdate);

        CompactDisc updatedDisc = service.updateCompactDisc(discToUpdate);

        assertSame(discToUpdate, updatedDisc);
        verify(dao).save(discToUpdate);
    }

    @Test
    void deleteCompactDiscByIdLoadsAndDeletesDisc() {
        CompactDisc discToDelete = createDisc(15, "Mezzanine", "Massive Attack", 12.99, 11);
        when(dao.findById(15)).thenReturn(Optional.of(discToDelete));

        service.deleteCompactDisc(15);

        verify(dao).findById(15);
        verify(dao).delete(discToDelete);
    }

    @Test
    void deleteCompactDiscByIdThrowsWhenDiscMissing() {
        when(dao.findById(123)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.deleteCompactDisc(123));
        verify(dao).findById(123);
        verify(dao, never()).delete(any(CompactDisc.class));
    }

    @Test
    void deleteCompactDiscDeletesProvidedDisc() {
        CompactDisc discToDelete = createDisc(16, "Spice World", "Spice Girls", 4.99, 11);

        service.deleteCompactDisc(discToDelete);

        verify(dao).delete(discToDelete);
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
