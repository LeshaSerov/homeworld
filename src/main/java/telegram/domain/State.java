package telegram.domain;

import lombok.*;

import java.util.Objects;
import java.util.Vector;

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
    private final State stateReturn;
    //Обработка Нажатия
    private String handlerMessage = null;
    //Обработка Нажатия
    private String handlerActivator = null;
    //Обработка Кнопок
    private String handlerButtonGenerating = null;
    private State stateNext = null;
    //Список Состояний
    private Vector<State> paths = new Vector<>();

    //Для Дефолтного Состояния
    public State(String name, String description) {
        this.name = name;
        this.description = description;
        this.nameButton = null;
        this.stateReturn = this;
    }

    public Boolean hasActivator(){
        return handlerActivator != null;
    }

    public Boolean hasMessageHandler(){
        return handlerMessage != null;
    }

    public Boolean hasGenerateButtons(){
        return  handlerButtonGenerating != null;
    }

    //Генерация Кнопок
    public void addButtonGenerating(String handlerButtonGenerating, State stateNext) {
        this.handlerButtonGenerating = handlerButtonGenerating;
        this.stateNext = stateNext;
    }

    //Редактирование Списка Состояний
    public void addPath(State state) {
        paths.add(state);
    }

    //Переходы Между Состояниями
    public State next(String name) {
        return paths.stream().filter(return_state -> Objects.equals(return_state.getName(), name)).findFirst().orElse(stateNext);
    }

    public State previous() {
        return stateReturn != null ? stateReturn : this;
    }
}
