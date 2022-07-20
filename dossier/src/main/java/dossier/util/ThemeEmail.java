package dossier.util;

public enum ThemeEmail {
    FINISH_REGISTRATION("finish-registration"),
    CREATE_DOCUMENT("create-documents"),
    SEND_DOCUMENT("send-documents"),
    SEND_SES("send-ses"),
    CREDIT_ISSUED("credit-issued"),
    APPLICATION_DENIED("application-denied");

    private String title;

    ThemeEmail(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
}
