package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class Bot extends TelegramLongPollingBot {

    //BOT_DATA
    final String BOT_TOKEN = Bot_Item.BOT_TOKEN;
    final String BOT_NAME = Bot_Item.BOT_NAME;

    boolean inline = false;

    Map<String, Parser> usersParsers = new HashMap<>();

    InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();

    String[] genres;

    public Bot() {
        String buf = "Аниме Биографии Боевики Военные Детективы Детские Документальные Драмы Исторические" +
                " Комедии Мелодраммы Мультфильмы Мюзиклы Приключения Семейные Спортивные Триллеры Ужасы " +
                "Фантастика Фэнтези";
        genres = buf.split(" ");
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try{
            if(update.hasMessage())
            {
                String chatId = update.getMessage().getChatId().toString();
                String response = parseMessage(update.getMessage());
                SendMessage outMess = new SendMessage();

                outMess.setChatId(chatId);
                outMess.setText(response);

                if(inline){
                    outMess.setReplyMarkup(inlineKeyboard);
                    inline = false;
                }

                execute(outMess);
            }
            else if (update.hasCallbackQuery()){
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
                SendMessage outMess = new SendMessage();
                outMess.setChatId(chatId);
                outMess.setText("Теперь необходимо написать год. Сообщение должно выглядить следующим образом" +
                        "\n\"год 2017\"");
                execute(outMess);

                Parser parser = new Parser();
                parser.genre = update.getCallbackQuery().getData();
                parser.year = "";
                usersParsers.put(chatId, parser);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public String parseMessage(Message msg) {
        String response;

        if(msg.getText().equals("/start")){
            response = "Приветствую, бот посылает случайные фильмы и если ты не знаешь что посмотреть то" +
                    " можешь испытать удачу, вдруг заинтересует. Для начала необходимо выбрать жанр:";
            CreateGenresKeyboard();
        } else if(msg.getText().toLowerCase().contains("год") &&
        usersParsers.containsKey(msg.getChatId().toString())){
            usersParsers.get(msg.getChatId().toString()).year = msg.getText().substring(
                    msg.getText().length() - 5);
            response =  
        }
        else
            response = "Сообщение не распознано";

        return response;
    }

    void CreateGenresKeyboard(){
        List<List<InlineKeyboardButton>> keyBoardRows = new Vector<>();
        for (String genre:
             genres) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(genre);
            button.setCallbackData(genre);
            List<InlineKeyboardButton> bufRow = new Vector<>();
            bufRow.add(button);
            keyBoardRows.add(bufRow);
        }
        inlineKeyboard.setKeyboard(keyBoardRows);
        inline = true;
    }

}
