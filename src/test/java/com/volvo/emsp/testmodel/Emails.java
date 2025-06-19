package com.volvo.emsp.testmodel;

@SuppressWarnings("unused")
public class Emails {
    public static final String EMAIL1 = "test1@example.com";
    public static final String EMAIL2 = "test2@example.com";
    public static final String EMAIL3 = "test3@example.com";
    public static final String EMAIL4 = "test4@example.com";
    public static final String EMAIL5 = "test5@example.com";

    public static final String TOO_LONG_EMAIL =
            "test@example" +
                    ".aaaaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaa" +
                    ".aaaaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaa" +
                    ".aaaaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaa" +
                    ".aaaaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaa" +
                    ".com";
    public static final String INVALID_EMAIL = "test1@example";
    public static final String INVALID_EMAIL2 = "test1@example.";
    public static final String INVALID_EMAIL3 = "test1@example..com";
    public static final String INVALID_EMAIL4 = "test1@example.com.";
    public static final String INVALID_EMAIL5 = "test1@example.com..";
    public static final String INVALID_EMAIL6 = "@";                      // only @ symbol
    public static final String INVALID_EMAIL7 = "user@";                  // no domain
    public static final String INVALID_EMAIL8 = "@domain.com";            // no local part
    public static final String INVALID_EMAIL9 = "user@domain";            // no top level domain
    public static final String INVALID_EMAIL10 = "user.@domain.com";      // dot at end of local part
    public static final String INVALID_EMAIL11 = ".user@domain.com";      // dot at start of local part
    public static final String INVALID_EMAIL12 = "user@domain..com";      // consecutive dots
    public static final String INVALID_EMAIL13 = "user space@domain.com"; // space in local part
    public static final String INVALID_EMAIL14 = "user@domain space.com"; // space in domain
    public static final String INVALID_EMAIL15 =
            "verylonglocalparthavingmorethan64characters0123456789abcdefghijklmnopqrstuvwxyz@domain.com";  // local part > 64 chars
    public static final String INVALID_EMAIL16 = "leoabby@outlook.ocm";

    public static void main(String[] args) {
        System.out.println(TOO_LONG_EMAIL);
        System.out.println(TOO_LONG_EMAIL.length());
    }

}
