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

    /**
     * Constructor without parameters: Set all fields to zero
     */
    public DetailResult() {
        this.win = 0L;
        this.draw = 0L;
        this.lose = 0L;
    }

    /**
     * Constructor with parameters: Set win, draw, and lose by the given number from the user's result
     * @param win number of win rounds
     * @param draw number of draw rounds
     * @param lose number of lose rounds
     */
    public DetailResult(Long win, Long draw, Long lose) {
        this.win = win;
        this.draw = draw;
        this.lose = lose;
    }
}
