package telegram.domain;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import kotlin.Pair;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

@Getter
@Setter
//@ToString
public class StateMachine
{
    //Класс - обертка для создания машины состояний - но не является ей((
    private State defaultState;
    private State currentState;
    
    public StateMachine(String name, String description){
        defaultState = new State(name,description);
        currentState = defaultState;
    }

    public StateMachine next(String nameState){
        currentState = currentState.next(nameState);
        return this;
    }
    public StateMachine previous(){
        currentState = currentState.previous();
        return this;
    }

    public StateMachine deletePath(State state){
        currentState.getPaths().remove(state);
        return this;
    }

    public StateMachine addPath(String name, String description, String nameButton){
        currentState.addPath(new State(name,description,nameButton,currentState));
        return this;
    }

    public StateMachine addPathProcessesMessages(Function<State.Data, List<BaseRequest>> operator,
                                                 String name, String description, String nameButton){
        currentState.addPath(new State(name, description,nameButton, currentState));
        next(name);
        currentState.setOperatorWhoProcessesMessages(operator);
        previous();
        return this;
    }

    public StateMachine addPathRunAtStartup(Function<State.Data, List<BaseRequest>> operator,
                                            String name, String nameButton){
        currentState.addPath(new State(name,null,nameButton, currentState));
        next(name);
        currentState.setOperatorWhichRunsAtStartup(operator);
        previous();
        return this;
    }

    public StateMachine addPathGenerateKeyboard(String nameState, String descriptionState, String nameButtonState,
                                                Function<State.Data,  List<Pair<String, String>>> operator,
                                                MemberData.TypeReceivedInformation type,
                                                String nameNewState, String description){
        currentState.addPath(new State(nameState,descriptionState,nameButtonState,currentState));
        next(nameState);
        currentState.addGenerateKeyboard(operator, type, new State(nameNewState,description,null, currentState));
        previous();
        return this;
    }

    public StateMachine relocationPathInPathGenerateKeyboard(String nameState, String descriptionState, String nameButtonState,
                                                             Function<State.Data,  List<Pair<String, String>>> operator,
                                                             MemberData.TypeReceivedInformation type,
                                                             String relocationNameState){
        State relocationState = currentState.next(relocationNameState);
        currentState.addPath(new State(nameState,descriptionState,nameButtonState,currentState));
        next(nameState);
        currentState.addGenerateKeyboard(operator, type, relocationState);
        previous();
        deletePath(relocationState);
        return this;
    }
}

//    public StateMachine addPath(String name, String description, String nameButton, State stateReturn){
//        currentState.addPath(new State(name,description,nameButton,stateReturn));
//        return this;
//    }
//    public StateMachine addPathRunAtStartup(BinaryOperator<MemberVault> operator, String name, String nameButton, State stateReturn){
//        currentState.addPath(new State(name,null,nameButton, stateReturn));
//        next(name);
//        currentState.setOperatorWhichRunsAtStartup(operator);
//        previous();
//        return this;
//    }
//    public StateMachine addPathProcessesMessages(BinaryOperator<MemberVault> operator, String name, String description, String nameButton, State stateReturn){
//        currentState.addPath(new State(name, description,nameButton, stateReturn));
//        next(name);
//        currentState.setOperatorWhoProcessesMessages(operator);
//        previous();
//        return this;
//    }