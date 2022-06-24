package telegram.handlers.mechanicsStates;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import telegram.domain.MemberVault;
import telegram.domain.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HandlerState {

    public Boolean checkBotChat(Message message) {
        return Objects.equals(message.chat().id(), message.from().id());
    }

    public BaseRequest processing(MemberVault memberVault, Message message, CallbackQuery callbackQuery) {

        BaseRequest request = null;

        Long chat_id = null;
        Integer id_message = null;

        if (message != null){
            chat_id = message.chat().id();
            if (memberVault.getState().hasMessageHandler())
            {
                //handlerMessage(memberVault.getState().getHandlerMessage());
            }
        }
        else if(callbackQuery != null) {
            id_message = callbackQuery.message().messageId();
            chat_id = callbackQuery.message().chat().id();

            if(Objects.equals(callbackQuery.data(), "Назад"))
                memberVault.setState(memberVault.getState().previous());
            else {
                State stateNext = memberVault.getState().getPaths()
                        .stream().filter(element ->
                                Objects.equals(element.getName(), callbackQuery.data()))
                        .findFirst().orElse(null);
                memberVault.setState(stateNext);
                if (memberVault.getState().hasActivator()) {
                    System.out.println();
                    //Activator()
                }
            }
        }

        String description = memberVault.getState().getDescription();
        InlineKeyboardMarkup inlineKeyboardMarkup = GenerationKeyboard(memberVault.getState());

        //Обработка Клавиатуры
        if (id_message != null)
            request = new EditMessageText(chat_id, id_message, description).replyMarkup(inlineKeyboardMarkup);
        else
            request = new SendMessage(chat_id, description).replyMarkup(inlineKeyboardMarkup);

        return request;
    }

    private InlineKeyboardMarkup GenerationKeyboard(State state){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        if (state.hasGenerateButtons())
        {
            //GenerationButton
        }
        else
        for (State element:
             state.getPaths()) {
            inlineKeyboardMarkup.addRow(new InlineKeyboardButton(element.getNameButton()).callbackData(element.getName()));
        }
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("Назад").callbackData("Назад"));
        return inlineKeyboardMarkup;
    }
}

//
//        List <InlineKeyboardButton> a =
//                state.getPaths().stream().map(
//                        element -> new InlineKeyboardButton(element.getNameButton())
//                                .callbackData(element.getName())).toList();
//
//        inlineKeyboardMarkup.addRow(a);

