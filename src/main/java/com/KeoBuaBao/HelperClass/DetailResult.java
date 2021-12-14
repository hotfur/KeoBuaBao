package com.KeoBuaBao.HelperClass;

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

    public Long getWin() {
        return win;
    }

    public void setWin(Long win) {
        this.win = win;
    }

    public Long getDraw() {
        return draw;
    }

    public void setDraw(Long draw) {
        this.draw = draw;
    }

    public Long getLose() {
        return lose;
    }

    public void setLose(Long lose) {
        this.lose = lose;
    }
}
