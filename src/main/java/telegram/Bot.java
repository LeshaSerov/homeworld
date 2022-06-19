package telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import telegram.domain.MemberVault;
import telegram.domain.State;
import util.ConnectionPool.ConnectionPool;

import java.util.*;

public class Bot {
    private final TelegramBot bot = new TelegramBot("5417780715:AAFRy0huha6DSO0VNWVJF7ThjN6kpzZ6iWk");
    private final ConnectionPool connector = new ConnectionPool();
    private final Map<Integer, MemberVault> controllerStates = new HashMap<>();
    private final State stateDefault = new State(null, null, null);

    private void initial(){
        State local = stateDefault;
        local.addPath(new State("AddGroup", "Добавить Группу", stateDefault));
        local.addPath(new State("ListGroup", "Список Групп", stateDefault));

        local.next("AddGroup").setHandlerRun("AddGroup");

        local = local.next("ListGroup");
        local.addButtonGenerating("ListGroup", new State("Group", null, local));

        local = local.next("Group");

        local.addPath(new State("FileSystem", "", local));
        local.addPath(new State("AddMembers", "", local));
        local.addPath(new State("ListMembersGroup", "", local));

        local = local.next("ListMembersGroup");
        local.addButtonGenerating("ListMembersGroup", new State("Member", null, local));

        local = local.next("Member");
        local.addPath(new State("DeleteMember", "", local.previous())); //вертать к списку
        local.addPath(new State("ListRoles", "", local)); //

        local.next("DeleteMember").setHandlerRun("DeleteMEmber");

        local = local.next("ListRoles");
        local.addButtonGenerating("ListRoles", new State("ChangeRole", null, local));
        local.next("ChangeRole").setHandlerRun("ChangeRole");

        local = local.previous().previous();


    }

    public void serve() {
        initial();
        System.out.println();
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }


    private void process(Update update) {
//        Проверка Типа Апдейта
//        Проверка в чате или в личке с ботом
//        Обработка Апдейтов - состояние пользователя
//        1 есть handlerRun
//
    }




//        try {
//            connector.CreateConnect();
//            controllerStates.put(1, new MemberVault(stateDefault));
//        }catch (Exception ignored){
//
//        }
//    }
}

//
//        BaseRequest request = null;
//
//        if (update.callbackQuery() != null) {
//            request = getAnswerRequest(update);
//        } else if (update.message() != null) {
//            request = getRequest(update);
//        }
//
//        if (request != null) {
//            bot.execute(request);
//        }
//    }
//
//
//    private BaseRequest getAnswerRequest(Update update) {
//        CallbackQuery callbackQuery = update.callbackQuery();
//        String call = callbackQuery.message().text();
//        String call_answer = callbackQuery.message().replyMarkup().inlinenameButtonboard()[0][0].text();
//        return new DeleteMessage(update.callbackQuery().message().chat().id(), update.callbackQuery().message().messageId());
//    }
//
//    private BaseRequest getRequest(Update update) {
//        String text = update.message().text();
//        if (text.startsWith("/ping")) return Ping.CreatePing(update.message());
//
//        else if (text.startsWith("/"))
//            return new DeleteMessage(update.message().chat().id(), update.message().messageId());
//        else return new SendMessage(update.message().chat().id(), text);
//    }
//
//
//
//}
