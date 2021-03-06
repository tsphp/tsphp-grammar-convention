/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.test.integration.testutils;

import ch.tsphp.grammarconvention.AGrammarConventionCheck;
import ch.tsphp.grammarconvention.GrammarWalker;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.antlr.tool.GrammarAST;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.exceptions.base.MockitoAssertionError;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore
public abstract class AGrammarWalkerTest
{
    public static final String TEST_FILE = "test.g";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public static File createFile(TemporaryFolder folder, List<String> lines) throws IOException {
        File file = folder.newFile(TEST_FILE);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            for (String string : lines) {
                writer.write(string);
                writer.write("\n");
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return file;
    }

    public GrammarWalker createGrammarWalker(ModuleFactory moduleFactory) {
        GrammarWalker walker = new GrammarWalker();
        walker.setModuleFactory(moduleFactory);
        return walker;
    }

    protected void verifyVisitAndLeaveTokenNotCalled(AGrammarConventionCheck check) {
        try {
            ArgumentCaptor<GrammarAST> captor = ArgumentCaptor.forClass(GrammarAST.class);
            verify(check).visitToken(captor.capture());
            fail("visitToken was called " + captor.getAllValues().size() + " time(s)");
        } catch (MockitoAssertionError e) {
            //should get exception since visitToken should not have been called
        }
        try {
            ArgumentCaptor<GrammarAST> captor = ArgumentCaptor.forClass(GrammarAST.class);
            verify(check).leaveToken(captor.capture());
            fail("leaveToken was called " + captor.getAllValues().size() + " time(s)");
        } catch (MockitoAssertionError e) {
            //should get exception since leaveToken should not have been called
        }
    }

    protected Configuration createDummyChildConfiguration() {
        Configuration config = mock(Configuration.class);
        when(config.getAttributeNames()).thenReturn(new String[]{});
        when(config.getChildren()).thenReturn(new Configuration[]{});
        return config;
    }


    protected Configuration createChildConfiguration(String childCheckName, String[][] attributes) throws CheckstyleException {
        Configuration childConfig = mock(Configuration.class);
        when(childConfig.getName()).thenReturn(childCheckName);
        final int length = attributes.length;
        String[] attributeNames = new String[length];
        for (int i = 0; i < length; ++i) {
            String[] attribute = attributes[i];
            attributeNames[i] = attribute[0];
            when(childConfig.getAttribute(attribute[0])).thenReturn(attribute[1]);
        }
        when(childConfig.getAttributeNames()).thenReturn(attributeNames);
        when(childConfig.getChildren()).thenReturn(new Configuration[]{});
        return childConfig;
    }

    protected File createFile(String fileName, String[] lines) throws IOException {
        return FileHelper.createFile(folder, fileName, lines);
    }

    protected File createFile(String fileName, Collection<String> lines) throws IOException {
        return FileHelper.createFile(folder, fileName, lines);
    }
}
