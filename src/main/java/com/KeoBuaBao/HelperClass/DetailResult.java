package com.KeoBuaBao.HelperClass;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class DetailResult {
    private Long win;
    private Long draw;
    private Long lose;

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
