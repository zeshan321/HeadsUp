package com.zeshanaslam.headsup;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WordHandler {

    List<String> words = new ArrayList<>();
    Activity activity;

    public WordHandler(Activity activity) {
        this.activity = activity;

        Collections.addAll(words, activity.getResources().getStringArray(R.array.wordslist_array));
    }

    public boolean hasNext() {
        return words.size() > 0;
    }

    public String getNext() {
        int index = new Random().nextInt(words.size());
        String word = words.get(index);

        words.remove(index);

        return word;
    }
}
