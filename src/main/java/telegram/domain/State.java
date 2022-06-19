package telegram.domain;
import lombok.*;

import java.util.Objects;
import java.util.Vector;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class State {
    private final String name;
    private final String nameButton;
    private final State stateReturn;

    //Обработка Данных
    private String handlerRun = null;

    //Обработка Кнопок
    private String handlerButtonGenerating = null;
    private State stateNext = null;

    //Список Состояний
    private Vector<State> paths = new Vector<>();

    //Генерация Кнопок
    public void addButtonGenerating(String handlerButtonGenerating, State stateNext){
        this.handlerButtonGenerating = handlerButtonGenerating;
        this.stateNext = stateNext;
    }

    //Редактирование Списка Состояний
    public void addPath(State state){
        paths.add(state);
    }

    public void deletePath(State state){
        paths.remove(state);
    }

    //Переходы Между Состояниями
    public State next(String name){
        return paths.stream().filter(return_state-> Objects.equals(return_state.getName(), name)).findFirst().orElse(stateNext);
    }

    public State previous(){
        return stateReturn != null ? stateReturn : this;
    }
}
