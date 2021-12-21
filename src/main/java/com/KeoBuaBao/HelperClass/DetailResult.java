package com.KeoBuaBao.HelperClass;

import lombok.Getter;
import lombok.Setter;

public class DetailResult {
    @Getter @Setter private Long win;
    @Getter @Setter private Long draw;
    @Getter @Setter private Long lose;

    public DetailResult() {
        this.win = 0L;
        this.draw = 0L;
        this.lose = 0L;
    }

    public DetailResult(Long win, Long draw, Long lose) {
        this.win = win;
        this.draw = draw;
        this.lose = lose;
    }
}
