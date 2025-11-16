package data;

public enum Language {
    RU("Русский", "Введите номер телефона"), EN("English", "Enter your phone number");

    public final String languageName;
    public final String title;

    Language(String languageName, String title) {
        this.languageName = languageName;
        this.title = title;
    }
}
