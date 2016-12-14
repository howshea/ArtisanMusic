package com.howshea.artisanmusic.event;

/**
 * PackageName: com.howshea.artisanmusic.event
 * FileName：   CreateListEvent
 * Created by haipo on 2016/12/9.
 */

public class CreateListEvent {
    private String mListTitle;

    public String getListTitle() {
        return mListTitle;
    }

    public CreateListEvent(String listTitle) {
        mListTitle = listTitle;
    }
}
