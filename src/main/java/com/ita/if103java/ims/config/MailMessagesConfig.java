package com.ita.if103java.ims.config;

public class MailMessagesConfig {

    public final static String ACTIVATE_USER = """
        Hello, we are happy to see you in our Inventory Management System.
        Please, follow link bellow to activate your account:
    """;

    public final static String FOOTER = """
        Please, ignore this message if you aren't interested in it.
        Best wishes, IF-103.Java
    """;

    public final static String RESET_PASSWORD = """
        A password change was requested for the IMS account associated with this email address.
        Please, follow this link if you would like to change your password:
    """;

}
