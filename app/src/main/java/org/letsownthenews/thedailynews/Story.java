package org.letsownthenews.thedailynews;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by benlister on 06/09/2014.
 */
public class Story {

    public int week;
    public String type;
    public String text;
    public String title;

    public int readership = 0;
    public int lies = 0;
    public int popularity = 0;

    public int conservative = 0;
    public int labour = 0;
    public int libdem = 0;
    public int ukip = 0;

    public Story(int week, String type, String text, String title, int readership, int lies, int popularity,
                 int ukip, int conservative, int labour, int libdem) {
        this.week = week;
        this.type = type;
        this.text = text;
        this.title = title;
        this.readership = readership;
        this.lies = lies;
        this.popularity = popularity;
        this.conservative = conservative;
        this.labour = labour;
        this.libdem = libdem;
        this.ukip = ukip;
    }

}
