package com.KeoBuaBao.Utility;


public class Test {
    public static void main(String[] args) {
        String username = "uyennguyen";
        String password = "123";
        long dateTime = 0;
        System.out.println(dateTime);
        String token = SecurityUtils.generateToken(username, SecurityUtils.hashPassword(password), dateTime);
        System.out.println(token);
    }
}

// Token Thuan: b695a052241955d4e8320bff0b1be61a69bdb1add0306b5c2368d24276279046
// Token Trang: 5858c8215425f6198869a9d6af31db77e1af5fde2f66209015904b20148eda53
// Token Sieu: 8d192a7d8bd65c1978c6181ba23546fa8b1640a62647651579b4aba70e539055
// Token Long: 5d6936c6b07314c898cee10ff4185bc7162ec59ec03c47904a30ee36a018a6a2
// Token Uyen: f62084e94a0d661175fa2bdf7bd9f7cd7dd3251cd18c7daf66d07a9bddbdd923
