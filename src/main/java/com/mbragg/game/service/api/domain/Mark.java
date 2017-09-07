package com.mbragg.game.service.api.domain;

/**
 * A 'Mark' that can be assigned to a Player and used in a Game.
 */
public enum Mark {
    CROSS('X'),
    CIRCLE('O'),
    INITIAL('-');

    private final Character name;

    private Mark(Character name) {
        this.name = name;
    }

    public Character getName() {
        return name;
    }
}
