package com.conygre.spring.boot.repos;

import com.conygre.spring.boot.entities.CompactDisc;
import com.conygre.spring.boot.entities.Track;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class CompactDiscRepositoryTest {

    @Autowired
    private CompactDiscRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByArtistReturnsOnlyMatchingDiscs() {
        repository.save(createDisc("Parachutes", "Coldplay", 11.99, 10));
        repository.save(createDisc("White Ladder", "David Gray", 9.99, 10));
        repository.save(createDisc("A Rush of Blood to the Head", "Coldplay", 12.99, 11));

        List<CompactDisc> matches = (List<CompactDisc>) repository.findByArtist("Coldplay");

        assertEquals(2, matches.size());
        assertTrue(matches.stream().allMatch(disc -> "Coldplay".equals(disc.getArtist())));
    }

    @Test
    void findByArtistReturnsEmptyCollectionWhenNoMatchesExist() {
        repository.save(createDisc("Mezzanine", "Massive Attack", 12.99, 11));

        List<CompactDisc> matches = (List<CompactDisc>) repository.findByArtist("Blur");

        assertTrue(matches.isEmpty());
    }

    @Test
    void savingDiscPersistsTrackRelationship() {
        CompactDisc disc = createDisc("Spice World", "Spice Girls", 4.99, 11);
        disc.setTrackTitles(List.of(new Track("Mama"), new Track("Wannabe")));

        CompactDisc savedDisc = repository.save(disc);
        entityManager.flush();
        entityManager.clear();
        CompactDisc reloadedDisc = repository.findById(savedDisc.getId()).orElseThrow();

        assertEquals(2, reloadedDisc.getTrackTitles().size());
        assertEquals(List.of("Mama", "Wannabe"),
                reloadedDisc.getTrackTitles().stream().map(Track::getTitle).collect(Collectors.toList()));
    }

    @Test
    void saveAndReloadPreservesScalarFields() {
        CompactDisc savedDisc = repository.save(createDisc("Echo Park", "Feeder", 13.99, 12));
        entityManager.flush();
        entityManager.clear();

        CompactDisc reloadedDisc = repository.findById(savedDisc.getId()).orElseThrow();

        assertEquals("Echo Park", reloadedDisc.getTitle());
        assertEquals("Feeder", reloadedDisc.getArtist());
        assertEquals(Double.valueOf(13.99), reloadedDisc.getPrice());
        assertEquals(Integer.valueOf(12), reloadedDisc.getTracks());
    }

    @Test
    void saveWithoutTracksLeavesEmptyTrackCollection() {
        CompactDisc savedDisc = repository.save(createDisc("White Ladder", "David Gray", 9.99, 10));
        entityManager.flush();
        entityManager.clear();

        CompactDisc reloadedDisc = repository.findById(savedDisc.getId()).orElseThrow();

        assertTrue(reloadedDisc.getTrackTitles().isEmpty());
    }

    @Test
    void saveExistingDiscUpdatesScalarFields() {
        CompactDisc savedDisc = repository.save(createDisc("Greatest Hits", "Penelope", 14.99, 14));
        savedDisc.setPrice(15.99);
        savedDisc.setTracks(15);
        repository.save(savedDisc);
        entityManager.flush();
        entityManager.clear();

        CompactDisc reloadedDisc = repository.findById(savedDisc.getId()).orElseThrow();

        assertEquals(Double.valueOf(15.99), reloadedDisc.getPrice());
        assertEquals(Integer.valueOf(15), reloadedDisc.getTracks());
    }

    @Test
    void deleteByIdRemovesPersistedDisc() {
        CompactDisc savedDisc = repository.save(createDisc("Parachutes", "Coldplay", 11.99, 10));

        repository.deleteById(savedDisc.getId());
        entityManager.flush();
        entityManager.clear();

        assertFalse(repository.findById(savedDisc.getId()).isPresent());
    }

    @Test
    void namedQueryReturnsOnlyDiscsAbovePriceThreshold() {
        repository.save(createDisc("Cheap Album", "Artist A", 8.99, 8));
        repository.save(createDisc("Premium Album", "Artist B", 18.99, 12));
        repository.save(createDisc("Mid Album", "Artist C", 11.99, 10));
        entityManager.flush();
        entityManager.clear();

        List<CompactDisc> matches = entityManager
                .createNamedQuery("compactdisc.getAll", CompactDisc.class)
                .setParameter("price", 10.00)
                .getResultList();

        assertEquals(2, matches.size());
        assertTrue(matches.stream().allMatch(disc -> disc.getPrice() > 10.00));
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
