package dao;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import repository.dao.FileInGroupDao;
import repository.dao.GroupDao;
import repository.domain.Category;
import repository.domain.File;
import util.ConnectionPool.ConnectionPool;

import java.util.ArrayList;

public class FileInGroupDaoTest {

    @Test(expected = Test.None.class)
    @SneakyThrows
    public void  FileInGroupTest()
    {
        /*ConnectionPool connector = new ConnectionPool();

        Integer id_group =new GroupDao().addGroup("12", connector);
        Integer id_category = new FileInGroupDao().addCategory(id_group,"a", connector);
        Assert.assertTrue(id_category>0);

        boolean result = new FileInGroupDao().deleteCategory(id_category, connector);
        Assert.assertTrue(result);

        result = new FileInGroupDao().deleteAllCategories(id_group, connector);
        Assert.assertFalse(result);

        new FileInGroupDao().addCategory(id_group,"23", connector);
        result = new FileInGroupDao().deleteAllCategories(id_group, connector);
        Assert.assertTrue(result);

        id_category = new FileInGroupDao().addCategory(id_group, "123", connector);
        result = new FileInGroupDao().editCategory(id_category,"543", connector);
        Assert.assertTrue(result);

        ArrayList<Category> categories = new FileInGroupDao().getAllCategories(id_group, connector);
        Assert.assertEquals(1, categories.size());

        result = new FileInGroupDao().addFile(1,id_category,"123", connector);
        Assert.assertTrue(result);
        result = new FileInGroupDao().deleteFile(1, connector);
        Assert.assertTrue(result);

        new FileInGroupDao().addFile(1,id_category,"123", connector);
        result = new FileInGroupDao().editFile(1,id_category,"title", connector);
        Assert.assertTrue(result);

        result = new FileInGroupDao().deleteAllFiles(id_category, connector);
        Assert.assertTrue(result);

        ArrayList<File> files = new FileInGroupDao().getAllFiles(id_group, connector);
        Assert.assertEquals(0, files.size());

        files = new FileInGroupDao().getAllFilesInCategory(id_category, connector);
        Assert.assertEquals(0, files.size());

      new GroupDao().deleteGroup(id_group, connector);*/

    }
}
