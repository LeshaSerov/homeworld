package telegram.domain;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import kotlin.Pair;
import lombok.*;
import telegram.Bot;
import util.ConnectionPool.ConnectionPool;

import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.function.Function;

@Builder
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class State {

    private final String name;
    private final String description;
    private final String nameButton;

    @ToString.Exclude
    @NonNull
    private State stateReturn;

    private final Access.Levels levelAccess;

    private State stateNext = null;

    public State(String name, String description, Access.Levels levels) {
        this.name = name;
        this.description = description;
        this.nameButton = null;
        this.levelAccess = levels;
        this.stateReturn = this;
    }

   /* public State(State state)
    {
        this.name = state.name;
        this.description = state.description;
        this.nameButton = state.nameButton;
        this.levelAccess = state.levelAccess;
        this.stateReturn = state.stateReturn;
        this.stateNext = state.stateNext;
        this.paths = state.paths;
        this.operatorWhoGeneratesDescription = state.operatorWhoGeneratesDescription;
        this.operatorWhichGeneratesKeyboard = state.operatorWhichGeneratesKeyboard;
        this.operatorWhichRunsAtStartup = state.operatorWhichRunsAtStartup;
        this.operatorWhoProcessesMessages = state.operatorWhoProcessesMessages;
    }*/

    //Механизм, обеспечивающий возможность подключать свои функции для обработки состояний
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Data{
        Long idThisMember;
        MemberData data;
        Update update;
        ConnectionPool connector;
        TelegramBot bot;
    }
    private Function<Data, List<BaseRequest>> operatorWhichRunsAtStartup = null;
    private Function<Data, List<BaseRequest>> operatorWhoProcessesMessages = null;
    private Function<Data,  List<Pair<String, String>>> operatorWhichGeneratesKeyboard = null;
    private MemberData.TypeReceivedInformation type = null;
    private Function<Data, String> operatorWhoGeneratesDescription = null;
    public void addGenerateKeyboard(Function<Data,  List<Pair<String, String>>> operator, MemberData.TypeReceivedInformation type, State stateNext) {
        this.operatorWhichGeneratesKeyboard = operator;
        this.type = type;
        this.stateNext = stateNext;
    }

    //Список Путей-Состояний по которым генерируется клавиатура
    private Vector<State> paths = new Vector<>();

    public void addPath(State state) {
        paths.add(state);
    }

    public State next(String name) {
        if (stateNext != null) return stateNext;
        return paths.stream().filter(state -> Objects.equals(state.getName(), name)).findFirst().orElseThrow();
    }
    public State previous() {
        return stateReturn != null ? stateReturn : this;
    }

}
