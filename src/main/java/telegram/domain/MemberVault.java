package telegram.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberVault {
    private State state;
    private Integer id_message = null;
    private Integer id_group = null;
    private Integer id_category = null;
    private Long id_member = null;
    private Integer id_role = null;

    public MemberVault(State state) {
        this.state = state;
    }

    //Переходы Между Состояниями
    public void next(String nameButton) {
        state = state.next(nameButton);
    }

    public void previous() {
        state = state.previous();
    }

}
