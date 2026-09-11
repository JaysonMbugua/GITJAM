package com.conygre.spring.boot.entities;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityModelTest {

    @Test
    void compactDiscDefaultConstructorStartsWithEmptyTrackList() {
        CompactDisc disc = new CompactDisc();

        assertNotNull(disc.getTrackTitles());
        assertTrue(disc.getTrackTitles().isEmpty());
    }

    @Test
    void compactDiscConvenienceConstructorSetsScalarFields() {
        CompactDisc disc = new CompactDisc("Parachutes", 11.99, "Coldplay", 10);

        assertEquals("Parachutes", disc.getTitle());
        assertEquals(Double.valueOf(11.99), disc.getPrice());
        assertEquals("Coldplay", disc.getArtist());
        assertEquals(Integer.valueOf(10), disc.getTracks());
    }

    @Test
    void compactDiscSettersUpdateFieldsAndTracks() {
        CompactDisc disc = new CompactDisc();
        Track mama = new Track("Mama");
        Track wannabe = new Track("Wannabe");

        disc.setId(16);
        disc.setTitle("Spice World");
        disc.setArtist("Spice Girls");
        disc.setPrice(4.99);
        disc.setTracks(11);
        disc.setTrackTitles(List.of(mama, wannabe));

        assertEquals(16, disc.getId());
        assertEquals("Spice World", disc.getTitle());
        assertEquals("Spice Girls", disc.getArtist());
        assertEquals(Double.valueOf(4.99), disc.getPrice());
        assertEquals(Integer.valueOf(11), disc.getTracks());
        assertEquals(List.of("Mama", "Wannabe"),
                disc.getTrackTitles().stream().map(Track::getTitle).collect(Collectors.toList()));
    }

    @Test
    void compactDiscCanBeSerializedWithTracks() throws Exception {
        CompactDisc disc = new CompactDisc("Mezzanine", 12.99, "Massive Attack", 11);
        disc.setId(15);
        disc.setTrackTitles(List.of(new Track(1, "Angel", 15), new Track(2, "Teardrop", 15)));

        CompactDisc copy = roundTrip(disc);

        assertEquals(15, copy.getId());
        assertEquals("Mezzanine", copy.getTitle());
        assertEquals("Massive Attack", copy.getArtist());
        assertEquals(2, copy.getTrackTitles().size());
        assertEquals(List.of("Angel", "Teardrop"),
                copy.getTrackTitles().stream().map(Track::getTitle).collect(Collectors.toList()));
    }

    @Test
    void trackDefaultConstructorAllowsSetters() {
        Track track = new Track();

        track.setId(3);
        track.setTitle("Spice up your life");
        track.setCdId(16);

        assertEquals(Integer.valueOf(3), track.getId());
        assertEquals("Spice up your life", track.getTitle());
        assertEquals(16, track.getCdId());
    }

    @Test
    void trackFullConstructorSetsAllFields() {
        Track track = new Track(2, "Wannabe", 16);

        assertEquals(Integer.valueOf(2), track.getId());
        assertEquals("Wannabe", track.getTitle());
        assertEquals(16, track.getCdId());
    }

    @Test
    void trackTitleOnlyConstructorSetsTitle() {
        Track track = new Track("Mama");

        assertEquals("Mama", track.getTitle());
        assertEquals(0, track.getCdId());
    }

    @Test
    void trackCanBeSerialized() throws Exception {
        Track track = new Track(1, "Someday", 9);

        Track copy = roundTrip(track);

        assertEquals(Integer.valueOf(1), copy.getId());
        assertEquals("Someday", copy.getTitle());
        assertEquals(9, copy.getCdId());
    }

    private <T> T roundTrip(T value) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(value);
        }

        try (ObjectInputStream objectInputStream =
                     new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
            @SuppressWarnings("unchecked")
            T copy = (T) objectInputStream.readObject();
            return copy;
        }
    }
}
