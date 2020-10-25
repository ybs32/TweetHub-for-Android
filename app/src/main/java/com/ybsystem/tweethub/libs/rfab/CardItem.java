package com.ybsystem.tweethub.libs.rfab;

import java.io.Serializable;

import lombok.Data;

@Data
public class CardItem implements Serializable{

    private String name;
    private int resId;

    public CardItem() {
    }

    public CardItem setName(String name) {
        this.name = name;
        return this;
    }

    public CardItem setResId(int resId) {
        this.resId = resId;
        return this;
    }

}
