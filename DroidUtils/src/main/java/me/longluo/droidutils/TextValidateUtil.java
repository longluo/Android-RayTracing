package me.longluo.droidutils;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextValidateUtil {

    // 用户名：必须是字母开头，4 - 16位（数字、字母）
    private static final String REGEX_USER_NAME = "^[a-zA-Z][a-zA-Z0-9]{3,15}$";

    private static final String REGEX_NICK_NAME = "[[\\u4e00-\\u9fa5]|[a-zA-Z]|\\d]*";

    private static final String REGEX_MOBILE = "^1(3\\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$";

    private static final String REGEX_EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    public static final String REGEX_ID_CARD_NUMBER = "^((1[1-5])|(2[1-3])|(3[1-7])|(4[1-6])|(5[0-4])|(6[1-5])|[7-9]1)\\d{4}(19|20|21)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";

    public static final String REGEX_MONEY = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";

    public static boolean checkAccountUserNameOrPhoneOrEmail(String account) {
        boolean isPhone = checkMobilePhone(account);
        boolean isEmail = checkEmail(account);
        boolean isUserName = checkUserName(account);

        return isPhone || isEmail || isUserName;
    }

    public static boolean checkAccountPhoneOrEmail(String account) {
        boolean isPhone = checkMobilePhone(account);
        boolean isEmail = checkEmail(account);

        return isPhone || isEmail;
    }

    public static boolean checkUserName(String name) {
        if (TextUtils.isEmpty(name) || name.length() < 4 || name.length() > 16) {
            return false;
        }

        Pattern pattern = Pattern.compile(REGEX_USER_NAME);
        return pattern.matcher(name).matches();
    }

    public static boolean checkNickName(String name) {
        if (TextUtils.isEmpty(name) || name.length() < 4 || name.length() > 16) {
            return false;
        }

        Pattern pattern = Pattern.compile(REGEX_NICK_NAME);
        return pattern.matcher(name).matches();
    }

    public static boolean checkMobilePhone(String number) {
        if (TextUtils.isEmpty(number) || number.length() < 11) {
            return false;
        }

        Pattern pattern = Pattern.compile(REGEX_MOBILE);
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    public static boolean checkEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }

        Pattern pattern = Pattern.compile(REGEX_EMAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean checkIDCardNumber(String number) {
        if (TextUtils.isEmpty(number) || number.length() != 18) {
            return false;
        }

        Pattern pattern = Pattern.compile(REGEX_ID_CARD_NUMBER);
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    public static boolean checkMoney(String money) {
        if (TextUtils.isEmpty(money)) {
            return false;
        }

        Pattern pattern = Pattern.compile(REGEX_MONEY);
        Matcher matcher = pattern.matcher(money);
        return matcher.matches();
    }

    public static boolean passwordChecker(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 8 || password.length() > 16) {
            return false;
        }

        int kindOfCharacter = 0;

        for (char ch : password.toCharArray()) {
            if (Character.isLetter(ch)) {
                kindOfCharacter |= 1;
            } else if (Character.isDigit(ch)) {
                kindOfCharacter |= 2;
            } else {
                kindOfCharacter |= 4;
            }
        }

        return kindOfCharacter == 3 || kindOfCharacter >= 5;
    }

    public static boolean comparePassword(String passwdA, String passwdB) {
        if (passwdA.equals(passwdB)) {
            return true;
        }

        return false;
    }

    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     *
     * @param mobile 移动、联通、电信运营商的号码段
     *               <p>移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）
     *               、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）</p>
     *               <p>联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）</p>
     *               <p>电信的号段：133、153、180（未启用）、189</p>
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkMobile(String mobile) {
        if (mobile.length() < 11) {
            return false;
        }
//        String regex = "(\\+\\d+)?1[3458]\\d{9}$";
//        return Pattern.matches(regex, mobile);
        return true;
    }

    /**
     * “1111 1111 1111”格式化银行卡号
     *
     * @param editText
     */
    public static void cardNumberFormat(EditText editText, Editable string) {
        boolean isContinue = true;
        if (editText.getTag() instanceof Boolean) {
            isContinue = (boolean) editText.getTag();
        }
        if (isContinue) {
            String content = string.toString();
            int count = 4;
            while (count < content.length()) {
                String sample = content.substring(count, count + 1);
                if (!sample.equals(" ")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(content.substring(0, count));
                    sb.append(" ");
                    sb.append(content.substring(count, content.length()));
                    count += 1;
                    content = sb.toString();
                }
                count += 5;
            }

            editText.setTag(false);
            editText.setText(content);
            editText.setSelection(content.length());
        } else {
            editText.setTag(true);
        }
    }

    public static String getCardNumberFormat(String string) {
        String content = string.toString();
        int count = 4;
        while (count < content.length()) {
            String sample = content.substring(count, count + 1);
            if (!sample.equals(" ")) {
                StringBuilder sb = new StringBuilder();
                sb.append(content.substring(0, count));
                sb.append(" ");
                sb.append(content.substring(count, content.length()));
                content = sb.toString();
            }
            count += 5;
        }

        return content;
    }

}
