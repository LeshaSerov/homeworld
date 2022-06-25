package telegram.handlers.mechanicsStates;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.*;
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

    public List<BaseRequest> processing(MemberVault memberVault, Message message, CallbackQuery callbackQuery) {

        List<BaseRequest> request = new ArrayList<>();

        Long chat_id = null;
        Integer id_message = memberVault.getId_message();

        if (message != null) {
            chat_id = message.chat().id();
            if (memberVault.getState().hasMessageHandler()) {
                //handlerMessage(memberVault.getState().getHandlerMessage());
                request.add(new SendMessage(chat_id, "Обработан текст сообщения - методом( " + memberVault.getState().getHandlerMessage() + " )"));
                request.add(new DeleteMessage(chat_id, id_message));
                id_message = null;
                memberVault.setId_message(null);
                memberVault.setState(memberVault.getState().previous());
            }
        } else if (callbackQuery != null) {

            chat_id = callbackQuery.message().chat().id();

            if (id_message != null && !id_message.equals(callbackQuery.message().messageId())) {
                request.add(new DeleteMessage(chat_id, callbackQuery.message().messageId()));
            } else {
                id_message = callbackQuery.message().messageId();
                memberVault.setId_message(id_message);

                if (Objects.equals(callbackQuery.data(), "Назад"))
                    memberVault.setState(memberVault.getState().previous());
                else if (memberVault.getState().hasGenerateButtons()) {
                    //Хранение Информации
                    memberVault.setState(memberVault.getState().getStateNext());
                } else {
                    State stateNext = memberVault.getState().getPaths()
                            .stream().filter(element ->
                                    Objects.equals(element.getName(), callbackQuery.data()))
                            .findFirst().orElse(null);
                    memberVault.setState(stateNext);
                    if (memberVault.getState().hasMessageHandler()) {
                        request.add(new SendMessage(chat_id, "Введите информацию"));
                    }
                    if (memberVault.getState().hasActivator()) {
                        request.add(new SendMessage(chat_id, "Запустился Активатор(" + memberVault.getState().getHandlerActivator() + ")"));
                        //Activator()
                        memberVault.setState(memberVault.getState().previous());
                    }
                }
            }
        }

        String description = memberVault.getState().getDescription();
        InlineKeyboardMarkup inlineKeyboardMarkup = GenerationKeyboard(memberVault.getState());

        //Обработка Клавиатуры
        if (id_message != null)
            request.add(new EditMessageText(chat_id, id_message, description).replyMarkup(inlineKeyboardMarkup));
        else
            request.add(new SendMessage(chat_id, description).replyMarkup(inlineKeyboardMarkup));

        return request;
    }

    private InlineKeyboardMarkup GenerationKeyboard(State state) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        if (state.hasGenerateButtons()) {
            //GenerationButton
            for (int i = 1; i < 5; i++) {
                inlineKeyboardMarkup.addRow(new InlineKeyboardButton(state.getStateNext().getName() + Integer.toString(i)).callbackData(Integer.toString(i)));
            }
        } else
            for (State element :
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

