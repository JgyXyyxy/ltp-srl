package com.sdu.j.ltpsrl.entity;

import lombok.Data;

@Data
public class Arg {

    private int id;
    private String type;
    private int begin;
    private int end;

    public Arg(int id, String type, int begin, int end) {
        this.id = id;
        this.type = type;
        this.begin = begin;
        this.end = end;
    }

    public Arg() {

    }

}
