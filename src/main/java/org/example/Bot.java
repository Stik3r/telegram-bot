package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Vector;

public class Bot extends TelegramLongPollingBot {

    //BOT_DATA
    final String BOT_TOKEN = Bot_Item.BOT_TOKEN;
    final String BOT_NAME = Bot_Item.BOT_NAME;

    ReplyKeyboardMarkup replyKeyboardMarkup;


    String[] genres;
    Vector<ReplyKeyboardMarkup> keyBoardsGenres = new Vector<>();
    int currentPage = 0;
    Storage storage = new Storage();

    public Bot() {
        String buf = "Аниме Биографии Боевики Военные Детективы Детские Документальные Драмы Исторические" +
                " Комедии Мелодраммы Мультфильмы Мюзиклы Приключения Семейные Спортивные Триллеры Ужасы " +
                "Фантастика Фэнтези";
        genres = buf.split(" ");
        initKetBoardGenres();
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
            if(update.hasMessage() && update.getMessage().hasText())
            {
                Message inMess = update.getMessage();
                String chatId = inMess.getChatId().toString();
                String response = parseMessage(inMess.getText());
                SendMessage outMess = new SendMessage();

                outMess.setChatId(chatId);
                outMess.setText(response);
                outMess.setReplyMarkup(replyKeyboardMarkup);


                execute(outMess);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public String parseMessage(String textMsg) {
        String response;

        if(textMsg.equals("/start")){
            response = "Приветствую, бот посылает случайные фильмы и если ты не знаешь что посмотреть то" +
                    " можешь испытать удачу, вдруг заинтересует. Для начала необходимо выбрать жанр:";
            replyKeyboardMarkup = keyBoardsGenres.get(0);
        }
        else if(textMsg.equals("Пред. страница")){
            if(currentPage == 0)
                response = "Это первая страница.";
            else{
                currentPage--;
                replyKeyboardMarkup = keyBoardsGenres.get(currentPage);
                response = "Клавиатура обнавлена \nСтраница " + (currentPage + 1);
            }
        }
        else if(textMsg.equals("След. страница"))
            if(currentPage == keyBoardsGenres.size() - 1)
                response = "Пока что это все жанры, что у меня имеются :(";
            else {
                currentPage++;
                replyKeyboardMarkup = keyBoardsGenres.get(currentPage);
                response = "Клавиатура обнавлена \nСтраница " + (currentPage + 1);
            }
        else
            response = "Сообщение не распознано";

        return response;
    }

    void initKetBoardGenres(){
        int genresCount = 0;
        while (genresCount < genres.length){
            ReplyKeyboardMarkup genresKeyboard = new ReplyKeyboardMarkup();
            genresKeyboard.setResizeKeyboard(true);
            ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
            keyboardRows.add(CreateRow(genresCount, 3));
            genresCount += 3;
            keyboardRows.add(CreateRow(genresCount, 2));
            genresCount += 2;
            KeyboardRow pages = new KeyboardRow();
            pages.add(new KeyboardButton("Пред. страница"));
            pages.add(new KeyboardButton("След. страница"));
            keyboardRows.add(pages);
            genresKeyboard.setKeyboard(keyboardRows);
            keyBoardsGenres.add(genresKeyboard);
        }
    }

    KeyboardRow CreateRow(int indx, int count){
        KeyboardRow result = new KeyboardRow();
        for(int i = 0; i < count; i++){
            result.add(new KeyboardButton(genres[indx + i]));
        }
        return result;
    }
}
