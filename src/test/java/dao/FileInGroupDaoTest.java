package dao;

import org.junit.Assert;
import org.junit.Test;
import repository.dao.FileInGroupDao;
import repository.dao.GroupDao;
import repository.domain.Category;
import repository.domain.File;

import javax.xml.transform.Result;
import java.util.ArrayList;

public class FileInGroupDaoTest {

    @Test
    public void  FileInGroupTest()
    {
        try {
            Integer id_group = GroupDao.addGroup("12");
            Integer id_category = FileInGroupDao.addCategory(id_group,"a");
            Assert.assertTrue(id_category>0);

            boolean result = FileInGroupDao.deleteCategory(id_category);
            Assert.assertTrue(result);

            result = FileInGroupDao.deleteAllCategories(id_group);
            Assert.assertFalse(result);

            FileInGroupDao.addCategory(id_group,"23");
            result = FileInGroupDao.deleteAllCategories(id_group);
            Assert.assertTrue(result);

            id_category = FileInGroupDao.addCategory(id_group, "123");
            result = FileInGroupDao.editCategory(id_category,"543");
            Assert.assertTrue(result);

            ArrayList<Category> categories = FileInGroupDao.getAllCategories(id_group);
            Assert.assertEquals(1, categories.size());

            result = FileInGroupDao.addFile(1,id_category,"123");
            Assert.assertTrue(result);
            result = FileInGroupDao.deleteFile(1);
            Assert.assertTrue(result);

            FileInGroupDao.addFile(1,id_category,"123");
            result = FileInGroupDao.editFile(1,id_category,"title");
            Assert.assertTrue(result);

            result = FileInGroupDao.deleteAllFiles(id_category);
            Assert.assertTrue(result);

            ArrayList<File> files = FileInGroupDao.getAllFiles(id_group);
            Assert.assertEquals(0, files.size());

            files = FileInGroupDao.getAllFilesInCategory(id_category);
            Assert.assertEquals(0, files.size());

            GroupDao.deleteGroup(id_group);

            //Assert.fail();

        }
            catch (Exception exception){

        }
    }
}
