package com.inschos.cloud.trading.assist.kit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        if (StringKit.isEmpty(cardCode)) {
            return false;
        }

        if (cardType == CARD_TYPE_ID_CARD) {
            if (cardCode.length() != 18) {
                return false;
            }

            long result = 0L;
            boolean flag = false;
            for (int i = 0; i < 18; i++) {
                char c = cardCode.charAt(i);
                if (i < 17 && c >= '0' && c <= '9') {
                    int num = c - '0';
                    result += (num * coefficient[i]);
                } else if (i == 17 && (c >= '0' && c <= '9') || c == 'x' || c == 'X') {
                    long l = result % 11;
                    String s = String.valueOf(c);
                    flag = s.equalsIgnoreCase(base[(int) l]);
                } else {
                    flag = false;
                    break;
                }
            }

            return flag;

        } else if (cardType == CARD_TYPE_PASSPORT) {
            return true;
        } else if (cardType == CARD_TYPE_MILITARY_CERTIFICATE) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isLegal(String cardType, String cardCode) {
        if (!StringKit.isInteger(cardType)) {
            return false;
        }

        Integer integer = Integer.valueOf(cardType);

        return integer >= 1 && integer <= 3 && isLegal(integer, cardCode);

    }

    public static Date getBirthDayByCode(int cardType, String cardCode) {
        Date date = null;
        if (isLegal(cardType, cardCode)) {
            if (cardType == CARD_TYPE_ID_CARD) {
                String substring = cardCode.substring(6, 14);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                try {
                    date = sdf.parse(substring);
                } catch (ParseException e) {
                    date = null;
                }
            }
        }
        return date;
    }

    public static String getCardTypeText(String cardType) {
        String str = "";
        if (!StringKit.isInteger(cardType)) {
            return str;
        }
        int type = Integer.valueOf(cardType);
        return getCardTypeText(type);
    }


    public static String getCardTypeText(int cardType) {
        String cardTypeText = null;
        switch (cardType) {
            case CardCodeKit.CARD_TYPE_ID_CARD:
                cardTypeText = "身份证";
                break;
            case CardCodeKit.CARD_TYPE_PASSPORT:
                cardTypeText = "护照";
                break;
            case CardCodeKit.CARD_TYPE_MILITARY_CERTIFICATE:
                cardTypeText = "军官证";
                break;
        }
        return cardTypeText;
    }

}
