package com.zeshanaslam.headsup;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WordHandler {

    private List<String> words = new ArrayList<>();

    public WordHandler(Activity activity) {
        Collections.addAll(words, activity.getResources().getStringArray(R.array.wordslist_array));
    }

    public boolean hasNext() {
        return words.size() > 0;
    }

    public String getNext() {
        String word = words.get(new Random().nextInt(words.size()));
        words.remove(word);

        return word;
    }
}
