package dao;

import lombok.SneakyThrows;
import org.checkerframework.checker.units.qual.A;
import org.junit.Assert;
import org.junit.Test;
import repository.dao.GroupDao;
import repository.dao.MemberDao;
import repository.dao.RoleDao;
import repository.domain.Member;
import repository.domain.Role;
import util.jdbcconnector.JdbcConnection;

import java.util.ArrayList;


public class GroupDaoTest {

    @Test(expected = Test.None.class)
    @SneakyThrows
    public void GroupTest(){
        JdbcConnection jdbcConnection = new JdbcConnection();

        Integer id_role = new RoleDao().addRole("admin", true,true,true,true, jdbcConnection);

        Integer id_group =new GroupDao().addGroup("1", jdbcConnection);
        Assert.assertNotNull(id_group);

        boolean result =new GroupDao().editGroup(id_group, "2", jdbcConnection);
        Assert.assertTrue(result);

        Integer id_member = 3;
        Integer id_member2 = 4;

        new MemberDao().deleteMember(id_member, jdbcConnection);
        new MemberDao().deleteMember(id_member2, jdbcConnection);

        new MemberDao().addMember(id_member,"1","1","1", jdbcConnection);
        result =new GroupDao().addMember(id_member,id_group, id_role, jdbcConnection);
        Assert.assertTrue(result);

        result =new GroupDao().editMember(id_member,id_group, -1, jdbcConnection);
        Assert.assertFalse(result);

        result =new GroupDao().editMember(id_member,id_group, id_role, jdbcConnection);
        Assert.assertTrue(result);

        result =new GroupDao().deleteMember(id_member,id_group, jdbcConnection);
        Assert.assertTrue(result);

        result =new GroupDao().editMember(id_member,id_group, id_role, jdbcConnection);
        Assert.assertFalse(result);

        result =new GroupDao().deleteMember(id_member,id_group, jdbcConnection);
        Assert.assertFalse(result);

        new MemberDao().deleteMember(id_member, jdbcConnection);
        result =new GroupDao().addMember(id_member,id_group, id_role, jdbcConnection);
        Assert.assertFalse(result);

        result =new GroupDao().deleteAllMembers(id_group, jdbcConnection);
        Assert.assertFalse(result);

        ArrayList <Role> rolesGroup =new GroupDao().getAllRole(jdbcConnection);

        result =new GroupDao().deleteGroup(id_group, jdbcConnection);
        Assert.assertTrue(result);

        id_group =new GroupDao().addGroup("1", jdbcConnection);
        new MemberDao().addMember(id_member,"1", "1", "1", jdbcConnection);
        new MemberDao().addMember(id_member2,"1", "1", "1", jdbcConnection);

      new GroupDao().addMember(id_member,id_group,id_role, jdbcConnection);
      new GroupDao().addMember(id_member2,id_group,id_role, jdbcConnection);

        result =new GroupDao().addWarning(id_member, id_group, id_member2, "1", 1, jdbcConnection);
        Assert.assertTrue(result);

        result =new GroupDao().addWarning(id_member, id_group, id_member2, "2", -1, jdbcConnection);
        Assert.assertTrue(result);

        result =new GroupDao().CheckWarning(jdbcConnection);
        Assert.assertTrue(result);

        Assert.assertSame(new GroupDao().getCountWarningsFromMemberInGroup(id_member,id_group, jdbcConnection), 1);

        result =new GroupDao().deleteAllWarnings(id_group,id_member, jdbcConnection);
        Assert.assertTrue(result);

        result =new GroupDao().addWarning(id_member, id_group, id_member2, "2", -1, jdbcConnection);
        Assert.assertTrue(result);

        result =new GroupDao().deleteAllWarningsInGroup(id_group, jdbcConnection);
        Assert.assertTrue(result);

        ArrayList <Member> members =new GroupDao().getAllMemberInGroup(id_group, jdbcConnection);
        Assert.assertSame(2,members.size());

        new MemberDao().deleteMember(id_member, jdbcConnection);
        new MemberDao().deleteMember(id_member2, jdbcConnection);
        new GroupDao().deleteGroup(id_group, jdbcConnection);
        result = new RoleDao().deleteRole(id_role, jdbcConnection);
        Assert.assertTrue(result);
    }


}
