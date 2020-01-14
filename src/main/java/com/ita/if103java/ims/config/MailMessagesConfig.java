package com.ita.if103java.ims.config;

public class MailMessagesConfig {

    public final static String STYLE = """
                 div {
                     margin-top: 10px;
                     font-size: 16px;
                     font-family: Georgia, 'Times New Roman', Times, serif;
                 }
                 img {
                     width: 40px;
                     height: 40px;
                     margin-right: 10px;
                 }
                 hr {
                    color: #3a3b45;
                 }
                 a {
                    margin-top: 15px;
                    margin-bottom: 15px;
                    font-size: 16px !important;
                    text-align: center;
                    text-decoration: none;
                    border-radius: 35px !important;
                    background-color: dodgerblue;
                    color: white !important;
                    padding: 15px 15px;
                    width: max-content;
                    opacity: 0.93;
                    display: block;
                    }
                 .row {
                    display: flex;
                 }
                 .container {
                    width: 640px;
                    height: auto;
                    margin-left: 200px;
                 }
        """;

    public final static String HEADER = """
              <head>
              <style type="text/css">
        """
        + STYLE + """
              </style>
              </head>
              <body>
              <div class="container">
              <div class="row">
              <img src="https://image.flaticon.com/icons/png/512/1568/1568851.png">
              <h3>Inventory Management System</h3>
              </div>
              <hr>
         """;

    public final static String ACTIVATE_USER = """
            <div>
            Hello, we are happy to see you in our Inventory Management System.<br>
            Please, activate your account!
            </div>
        """;

    public final static String FOOTER = """
            <div>
            Please, ignore this message if you aren't interested in it.<br>
            Best wishes, IF-103.Java
            </div>
            </div>
            </body>
        """;

    public final static String RESET_PASSWORD = """
            <div>
            A password change was requested for the IMS account associated with this email address.<br>
            You can also change your password immediately.
            </div>
        """;

}
