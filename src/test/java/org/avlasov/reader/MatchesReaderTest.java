package org.avlasov.reader;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.avlasov.entity.match.Match;
import org.avlasov.test.TestEntitiesCreator;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Created By artemvlasov on 09/06/2018
 **/
@RunWith(PowerMockRunner.class)
@PrepareForTest(MatchesReader.class)
public class MatchesReaderTest {

    @Mock
    private File folder;
    @Mock
    private File file;
    @Mock
    private ObjectMapper objectMapper;
    private MatchesReader matchesReader;

    @Before
    public void setUp() throws Exception {
        matchesReader = new MatchesReader("test", "matches_data.json", objectMapper);
        whenNew(File.class).withAnyArguments().thenReturn(folder);
        when(objectMapper.readValue(any(File.class), any(JavaType.class))).thenReturn(Collections.singletonList(TestEntitiesCreator.getBestMatch()));
        when(folder.exists()).thenReturn(true);
        when(folder.listFiles()).thenReturn(new File[]{file});
        when(file.getName()).thenReturn("matches_data.json");
    }

    @Test
    public void readMatches_WithValidData_ReturnMatches() throws Exception {
        List<Match> matches = matchesReader.readMatches();
        assertNotNull(matches);
        assertThat(matches, IsCollectionWithSize.hasSize(1));
    }

    @Test
    public void readMatches_WithNotExistsFolder_ReturnEmptyCollection() throws Exception {
        when(folder.exists()).thenReturn(false);
        List<Match> matches = matchesReader.readMatches();
        assertNotNull(matches);
        assertThat(matches, IsEmptyCollection.empty());
    }

    @Test
    public void readMatches_WithEmptyFolder_ReturnEmptyCollection() throws Exception {
        when(folder.listFiles()).thenReturn(null);
        List<Match> matches = matchesReader.readMatches();
        assertNotNull(matches);
        assertThat(matches, IsEmptyCollection.empty());
    }

    @Test
    public void readMatches_WithoutMatchingFile_ReturnEmptyCollection() throws Exception {
        when(file.getName()).thenReturn("invalid.json");
        List<Match> matches = matchesReader.readMatches();
        assertNotNull(matches);
        assertThat(matches, IsEmptyCollection.empty());
    }

}