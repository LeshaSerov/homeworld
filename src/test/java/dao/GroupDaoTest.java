package dao;

import org.checkerframework.checker.units.qual.A;
import org.junit.Assert;
import org.junit.Test;
import repository.dao.GroupDao;
import repository.dao.MemberDao;
import repository.dao.RoleDao;
import repository.domain.Group;
import repository.domain.Member;
import repository.domain.Role;
import repository.domain.Warning;

import java.util.ArrayList;


public class GroupDaoTest {

    @Test
    public void GroupTest(){
        try
        {
            Integer id_role = RoleDao.addRole("admin", true,true,true,true);

            Integer id_group = GroupDao.addGroup("1");
            Assert.assertNotNull(id_group);

            boolean result = GroupDao.editGroup(id_group, "2");
            Assert.assertTrue(result);

            Integer id_member = 3;
            Integer id_member2 = 4;

            MemberDao.deleteMember(id_member);
            MemberDao.deleteMember(id_member2);

            MemberDao.addMember(id_member,"1","1","1");
            result = GroupDao.addMember(id_member,id_group, id_role);
            Assert.assertTrue(result);

            result = GroupDao.editMember(id_member,id_group, -1);
            Assert.assertFalse(result);

            result = GroupDao.editMember(id_member,id_group, id_role);
            Assert.assertTrue(result);

            result = GroupDao.deleteMember(id_member,id_group);
            Assert.assertTrue(result);

            result = GroupDao.editMember(id_member,id_group, id_role);
            Assert.assertFalse(result);

            result = GroupDao.deleteMember(id_member,id_group);
            Assert.assertFalse(result);

            MemberDao.deleteMember(id_member);
            result = GroupDao.addMember(id_member,id_group, id_role);
            Assert.assertFalse(result);

            result = GroupDao.deleteAllMembers(id_group);
            Assert.assertFalse(result);

            ArrayList <Role> rolesGroup = GroupDao.getAllRole();

            result = GroupDao.deleteGroup(id_group);
            Assert.assertTrue(result);

            id_group = GroupDao.addGroup("1");
            MemberDao.addMember(id_member,"1", "1", "1");
            MemberDao.addMember(id_member2,"1", "1", "1");

            GroupDao.addMember(id_member,id_group,id_role);
            GroupDao.addMember(id_member2,id_group,id_role);

            result = GroupDao.addWarning(id_member, id_group, id_member2, "1", 1);
            Assert.assertTrue(result);

            result = GroupDao.addWarning(id_member, id_group, id_member2, "2", -1);
            Assert.assertTrue(result);

            result = GroupDao.CheckWarning();
            Assert.assertTrue(result);

            Assert.assertSame(GroupDao.getCountWarningsFromMemberInGroup(id_member,id_group), 1);

            result = GroupDao.deleteAllWarnings(id_group,id_member);
            Assert.assertTrue(result);

            result = GroupDao.addWarning(id_member, id_group, id_member2, "2", -1);
            Assert.assertTrue(result);

            result = GroupDao.deleteAllWarningsInGroup(id_group);
            Assert.assertTrue(result);

            ArrayList <Member> members = GroupDao.getAllMemberInGroup(id_group);
            Assert.assertSame(2,members.size());

            MemberDao.deleteMember(id_member);
            MemberDao.deleteMember(id_member2);
            GroupDao.deleteGroup(id_group);
            RoleDao.deleteRole(id_role);

            //Assert.fail();
        }
        catch (Exception ignored)
        {}
    }


}
