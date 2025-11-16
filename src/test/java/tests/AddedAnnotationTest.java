package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import data.Language;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

public class AddedAnnotationTest {

    @BeforeAll
    static void setupEnvironment() {
        Configuration.pageLoadStrategy = "eager";
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }


    @CsvSource(value = {"'', Обязательное поле", "+7000190101, Номер введен некорректно"})
    @ParameterizedTest(name = "Валидация номера телефона: {0} => {1}")
    void phoneNumberValidationTest(String phoneNumber, String errorMessage) {
        open("https://admin.netmonet.co/login");
        $("input[name='phone']").setValue(phoneNumber);
        $("button[type='submit']").click();
        SelenideElement error = $(".n-error").shouldBe(visible);
        assertEquals(errorMessage, error.getText(), "Некорректный текст ошибки");
    }

    @ValueSource(strings = {"Лампочка", "Мыло"})
    @ParameterizedTest(name = "При поиске по слову {0} на странице отображаются карточки товаров")
    @DisplayName("При поиске по слову на странице отображаются карточки товаров")
    void correctSearchTest(String searchData) {
        open("https://lemanaprof.ru/");
        $("input[type=text]").setValue(searchData).pressEnter();
        String resultHeader = $("h1").getText();
        assertEquals(searchData, resultHeader, "Заголовок результата не совпадает с поисковым запросом");
        int cardsCount = $$((".row > a")).size();
        assertTrue(cardsCount > 0, "На странице нет карточек по запросу: " + searchData);
    }

    @EnumSource(Language.class)
    @ParameterizedTest(name = "При переключении языка на странице меняется заголовок")
    void changeLanguageTest(Language language) {
        open("https://netmonet.co/auth/sign-in");
        $("button[type=button]").click();
        $(".select-language-modal-body").$(byText(language.languageName)).click();
        $(".close-wrapper").click();
        String phoneNumberTitle = $(".phone-number-form__title").getText();
        assertEquals(phoneNumberTitle, language.title);
    }


    static Stream<Arguments> documentsLanguageSwitchTest() {
        return Stream.of(Arguments.of(Language.RU, List.of("Лицензионное соглашение. Гость", "Правила использования профиля гостя", "Политика конфиденциальности и обработки персональных данных", "Согласие на получение информационных рассылок")), Arguments.of(Language.EN, List.of("License Agreement. Guest", "Guest Terms of Use", "Privacy Policy & Data Processing", "Consent to Receive Newsletters")));
    }

    @ParameterizedTest(name = "Документы на странице должны соответствовать выбранному языку: {0}")
    @MethodSource
    void documentsLanguageSwitchTest(Language language, List<String> expectedDocuments) {
        open("https://netmonet.co/auth/sign-in");
        $("button[type=button]").click();
        $(".select-language-modal-body").$(byText(language.languageName)).click(); // переключаем язык
        $(".close-wrapper").click();
        $(".agreement-description").click(); //кликаем на ссылку
        switchTo().window(1); //переходим в соседнюю вкладку
        $$(".document__text").shouldBe(sizeGreaterThan(0)); // проверяем, что тексты подгрузились
        List<String> actualTexts = $$(".document__text").texts(); // получаем список документов
        Assertions.assertEquals(expectedDocuments, actualTexts, "Список документов не совпадает с ожидаемым"); // проверяем, что список соответствует ожидаемому
    }
}
