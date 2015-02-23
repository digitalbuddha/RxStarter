package com.rx.demo.model;

/**
 * Created by Nakhimovich on 2/21/15.
 */
public class ViewModel {
    final int cardId;
    final int nameId;
    final int avatarID;

    public ViewModel(int cardId, int nameId, int avatarID) {
        this.cardId = cardId;
        this.nameId = nameId;
        this.avatarID = avatarID;
    }

    public int getCardId() {
        return cardId;
    }

    public int getNameId() {
        return nameId;
    }

    public int getAvatarID() {
        return avatarID;
    }
}
