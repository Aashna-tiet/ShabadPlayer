package org.asofat.shabadplayer;
/**
 * A dummy item representing a piece of content.
 */


public class SongData {
    public final String id;
    public final String url;
    public final String details;
    private static final String SEP = "~~";

    public SongData(String id, String url, String details) {
        this.id = id;
        this.url = url;
        this.details = details;

    }

    public String toString() {
        return id+SEP+url+SEP+details;
    }

    public static SongData parseFromString(String str) {
        String[] parts = str.split(SEP);
        if (parts.length != 3) return null;
        return new SongData(parts[0], parts[1], parts[2]);
    }


}
