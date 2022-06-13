package dao;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import repository.dao.GroupDao;
import repository.dao.MemberDao;
import repository.dao.RoleDao;
import repository.domain.Member;
import repository.domain.Role;
import util.ConnectionPool.ConnectionPool;

import java.util.ArrayList;


public class GroupDaoTest {

    @Test(expected = Test.None.class)
    @SneakyThrows
    public void GroupTest(){
        ConnectionPool connector = new ConnectionPool();

        Integer id_role = new RoleDao().addRole("admin", true,true,true,true, connector);

        ArrayList <Role> roles = new RoleDao().allRole(connector);
        for (Role auto: roles) {
            System.out.println(auto.toString());
        }

        Integer id_group =new GroupDao().addGroup("1", connector);
        Assert.assertNotNull(id_group);

        boolean result =new GroupDao().editGroup(id_group, "2", connector);
        Assert.assertTrue(result);

        Integer id_member = 3;
        Integer id_member2 = 4;

        new MemberDao().deleteMember(id_member, connector);
        new MemberDao().deleteMember(id_member2, connector);

        new MemberDao().addMember(id_member,"1","1","1", connector);
        result =new GroupDao().addMember(id_member,id_group, id_role, connector);
        Assert.assertTrue(result);

        result =new GroupDao().editMember(id_member,id_group, -1, connector);
        Assert.assertFalse(result);

        result =new GroupDao().editMember(id_member,id_group, id_role, connector);
        Assert.assertTrue(result);

        result =new GroupDao().deleteMember(id_member,id_group, connector);
        Assert.assertTrue(result);

        result =new GroupDao().editMember(id_member,id_group, id_role, connector);
        Assert.assertFalse(result);

        result =new GroupDao().deleteMember(id_member,id_group, connector);
        Assert.assertFalse(result);

        new MemberDao().deleteMember(id_member, connector);
        result =new GroupDao().addMember(id_member,id_group, id_role, connector);
        Assert.assertFalse(result);

        result =new GroupDao().deleteAllMembers(id_group, connector);
        Assert.assertFalse(result);

        ArrayList <Role> rolesGroup =new GroupDao().getAllRole(connector);

        result =new GroupDao().deleteGroup(id_group, connector);
        Assert.assertTrue(result);

        id_group =new GroupDao().addGroup("1", connector);
        new MemberDao().addMember(id_member,"1", "1", "1", connector);
        new MemberDao().addMember(id_member2,"1", "1", "1", connector);

      new GroupDao().addMember(id_member,id_group,id_role, connector);
      new GroupDao().addMember(id_member2,id_group,id_role, connector);

        result =new GroupDao().addWarning(id_member, id_group, id_member2, "1", 1, connector);
        Assert.assertTrue(result);

        result =new GroupDao().addWarning(id_member, id_group, id_member2, "2", -1, connector);
        Assert.assertTrue(result);

        result =new GroupDao().CheckWarning(connector);
        Assert.assertTrue(result);

        Assert.assertSame(new GroupDao().getCountWarningsFromMemberInGroup(id_member,id_group, connector), 1);

        result =new GroupDao().deleteAllWarnings(id_group,id_member, connector);
        Assert.assertTrue(result);

        result =new GroupDao().addWarning(id_member, id_group, id_member2, "2", -1, connector);
        Assert.assertTrue(result);

        result =new GroupDao().deleteAllWarningsInGroup(id_group, connector);
        Assert.assertTrue(result);

        ArrayList <Member> members =new GroupDao().getAllMemberInGroup(id_group, connector);
        Assert.assertSame(2,members.size());

        new MemberDao().deleteMember(id_member, connector);
        new MemberDao().deleteMember(id_member2, connector);
        new GroupDao().deleteGroup(id_group, connector);
        result = new RoleDao().deleteRole(id_role, connector);
        Assert.assertTrue(result);
    }


}
