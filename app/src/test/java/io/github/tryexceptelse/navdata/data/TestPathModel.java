package io.github.tryexceptelse.navdata.data;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

/**
 * Tests functionality of PathModel
 */
public class TestPathModel {
    private TemporaryFolder tmpFolder;
    private File pathDir;
    private PathModel model;

    @Before
    public void setUp() throws Exception {
        tmpFolder = new TemporaryFolder();
        tmpFolder.create();
        pathDir = tmpFolder.newFolder("pathDir");
    }

    @After
    public void tearDown() throws Exception {
        pathDir.delete();
        tmpFolder.delete();
    }

    @Test
    public void testPathModelCanBeInstantiated() throws Exception {
        new PathModel(pathDir);
    }

    @Test
    public void testPathModelIsEmptyWhenCreated() throws Exception {
        model = new PathModel(pathDir);
        Assert.assertTrue(model.isEmpty());
    }

    @Test
    public void testPathModelIsNotEmptyAfterPathIsAdded() throws Exception {
        model = new PathModel(pathDir);
        Path pathToAdd = new Path("Test Path");
        model.add(pathToAdd);
        Assert.assertFalse(model.isEmpty());
    }

    @Test
    public void testContainsReturnsTrueIfItemOfNameAdded() throws Exception {
        model = new PathModel(pathDir);
        final String pathName = "Test Path";
        Path pathToAdd = new Path(pathName);
        model.add(pathToAdd);
        Assert.assertTrue(model.contains(pathName));
    }

    @Test
    public void testContainsReturnsFalseWhenItemDoesNotExist() throws Exception {
        model = new PathModel(pathDir);
        final String pathNameA = "Test PathA";
        final String pathNameB = "Test PathB";
        Path pathToAdd = new Path(pathNameA);
        model.add(pathToAdd);
        Assert.assertFalse(model.contains(pathNameB));
    }

    @Test
    public void testAddingPathReturnsTrue() throws Exception {
        model = new PathModel(pathDir);
        final String pathName = "Test Path";
        Path pathToAdd = new Path(pathName);
        Assert.assertTrue(model.add(pathToAdd));
    }
}
