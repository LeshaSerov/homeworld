package telegram.domain;

import repository.domain.Role;

public class Access {

    public enum Levels{
        NoNe,
        RightRead,
        RightPing,
        RightEdit,
        RightAdmin,
        RightCreator
    }

    public static boolean checkAccess(Role role, Levels level)
    {
        if (level == Levels.NoNe)
            return true;
        if (role == null)
            return false;
        return (level == Levels.RightRead && role.getRight_to_view())
                || (level == Levels.RightPing && role.getRight_ping())
                || (level == Levels.RightEdit && role.getRight_edit())
                || (level == Levels.RightAdmin && role.getRight_admin())
                || (level == Levels.RightCreator && role.getRight_creator());
    }

    public static boolean checkAccess(Role roleThisMember, Role roleOtherMember){
        if (roleOtherMember.getRight_creator())
            return false;
        if (roleThisMember.getRight_creator())
            return true;
        if (roleThisMember == roleOtherMember)
            return false;
        if (!roleThisMember.getRight_admin())
            return false;
        return !roleOtherMember.getRight_edit() || roleThisMember.getId() > roleOtherMember.getId();
    }
}
