package com.inschos.cloud.trading.assist.kit;

/**
 * 创建日期：2018/3/22 on 15:00
 * 描述：
 * 作者：zhangyunhe
 */
public class CardCodeKit {

    private static int[] coefficient = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static String[] base = new String[]{"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};

    public static final int CARD_TYPE_ID_CARD = 1;
    public static final int CARD_TYPE_PASSPORT = 2;
    public static final int CARD_TYPE_MILITARY_CERTIFICATE = 3;

    public static boolean isLegal(int cardType, String cardCode) {

        if (cardType == CARD_TYPE_ID_CARD && !StringKit.isEmpty(cardCode)) {

            if (cardCode.length() != 18) {
                return false;
            }

            long result = 0L;
            boolean flag = false;
            for (int i = 0; i < 18; i++) {
                char c = cardCode.charAt(i);
                if (i < 17 && c >= '1' && c <= '9') {
                    int num = (int) c;
                    result += (num * coefficient[i]);
                } else if (i == 17 && (c >= '1' && c <= '9') || c == 'x' || c == 'X') {
                    long l = result % 11;
                    String s = String.valueOf(c);
                    flag =  s.equalsIgnoreCase(base[(int) l]);
                } else {
                    flag = false;
                    break;
                }
            }

            return flag;

        } else if (cardType == CARD_TYPE_ID_CARD && !StringKit.isEmpty(cardCode)) {
            return true;
        } else if (cardType == CARD_TYPE_ID_CARD && !StringKit.isEmpty(cardCode)) {
            return true;
        } else {
            return false;
        }

    }

}
