package com.rx.demo.model;

/**
 * Created by Nakhimovich on 2/21/15.
 */
public class Card {
    final int cardId;
    final int nameId;
    final int avatarID;
    private int closeId;

    public Card(int cardId, int nameId, int avatarID, int closeId) {
        this.cardId = cardId;
        this.nameId = nameId;
        this.avatarID = avatarID;
        this.closeId = closeId;
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

    public int getCloseId() {
        return closeId;
    }
}
