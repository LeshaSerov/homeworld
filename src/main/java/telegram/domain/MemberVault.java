package telegram.domain;

import lombok.*;
import repository.domain.Category;
import repository.domain.Group;
import repository.domain.Member;
import repository.domain.Role;

@Getter
@Setter
public class MemberVault{
    private State state;
//    private Group group = null;
//    private Category category = null;
//    private Member member = null;
//    private Role role = null;

    public MemberVault(State state){
        this.state = state;
    }

    //Переходы Между Состояниями
    public void next(String nameButton){
        state = state.next(nameButton);
    }

    public void previous(){
        state = state.previous();
    }

}
