package com.KeoBuaBao.HelperClass;

import lombok.Getter;
import lombok.Setter;

/**
 * A helper class to encapuslate the result of a match consisting of all the rounds
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
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
