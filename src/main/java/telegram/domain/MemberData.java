package telegram.domain;

import com.pengrad.telegrambot.model.Update;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import repository.domain.Role;

@Getter
@Setter
@ToString
public class MemberData {
    private State state;

    private Integer NumberSublist = 0;
    private State previousStateGenerateKeyboard = null;

    private Integer idMessage = null;
    private Role role;
    private Integer idGroup = null;

    private Integer idChat = null;
    private Integer idCategory = null;
    private Long idOtherMember = null;
    private Integer idRoleOtherMember = null;
    private Integer idFile = null;
    private Integer idProject = null;
    private Integer idWarning = null;

    private String titleFile = null;

    private Boolean isStateWhichSendsMessages = false;
    private Boolean isReturn = false;

    public enum TypeReceivedInformation{
        IdGroup,

        IdChat,
        IdCategory,
        IdOtherMember,
        IdRoleOtherMember,
        IdFile,
        IdProject,
        idWarning

    }
    public void addInfo(TypeReceivedInformation type, String x){
        switch (type)
        {
            case IdGroup -> idGroup = Integer.valueOf(x);
            case IdChat -> idChat = Integer.valueOf(x);
            case IdCategory -> idCategory = Integer.valueOf(x);
            case IdOtherMember -> idOtherMember = Long.valueOf(x);
            case IdRoleOtherMember -> idRoleOtherMember = Integer.valueOf(x);
            case IdFile -> idFile = Integer.valueOf(x);
            case IdProject -> idProject = Integer.valueOf(x);
            case idWarning -> idWarning = Integer.valueOf(x);

        }
    }

    public MemberData(State state) {
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
