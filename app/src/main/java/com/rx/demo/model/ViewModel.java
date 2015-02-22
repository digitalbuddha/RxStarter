package com.rx.demo.model;

/**
 * Created by Nakhimovich on 2/21/15.
 */
public class ViewModel {
    final int nameId;
    final int avatarID;

    public ViewModel(int nameId, int avatarID) {
        this.nameId = nameId;
        this.avatarID = avatarID;
    }

    public int getNameId() {
        return nameId;
    }

    public int getAvatarID() {
        return avatarID;
    }
}
