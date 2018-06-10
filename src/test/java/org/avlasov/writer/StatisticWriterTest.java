package org.avlasov.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.avlasov.PowerMockTestCase;
import org.avlasov.entity.statistic.ChampionshipStatistic;
import org.avlasov.entity.statistic.MatchStatistic;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.File;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created By artemvlasov on 09/06/2018
 **/
@PrepareForTest(StatisticWriter.class)
public class StatisticWriterTest extends PowerMockTestCase {

    @Mock
    private File file;
    @Mock
    private ObjectMapper objectMapper;
    private StatisticWriter statisticWriter;

    @Before
    public void setUp() throws Exception {
        statisticWriter = new StatisticWriter("test", objectMapper);
        doNothing().when(objectMapper).writeValue(any(File.class), any());
        whenNew(File.class).withAnyArguments().thenReturn(file);
        when(file.exists()).thenReturn(true);
    }

    @Test
    public void writeChampionshipStatistic_WithValidData() throws Exception {
        statisticWriter.writeChampionshipStatistic(ChampionshipStatistic.builder().build());
    }

    @Test
    public void writeDrawGroupPlayerStatistic_WithValidData() throws Exception {
        statisticWriter.writeDrawGroupPlayerStatistic(Collections.emptyMap());
    }

    @Test
    public void writeVehicleStatistics_WithValidData() throws Exception {
        statisticWriter.writeVehicleStatistics(Collections.emptyList());
    }

    @Test
    public void writePlatoonStatistics_WithValidData() throws Exception {
        statisticWriter.writePlatoonStatistics(Collections.emptyList());
    }

    @Test
    public void writePlayerStatistics_WithValidData() throws Exception {
        statisticWriter.writePlayerStatistics(Collections.emptyList());
    }

    @Test
    public void writeMatchStatistics_WithValidData() throws Exception {
        statisticWriter.writeMatchStatistics(MatchStatistic.builder().build());
    }

    @Test
    public void writeData_WithValidData() throws Exception {
        statisticWriter.writeData("test", "test");
    }

    @Test
    public void writeData_WithEmptyFilename() throws Exception {
        statisticWriter.writeData("", "test");
    }

    @Test
    public void writeData_WithNullFilename() throws Exception {
        statisticWriter.writeData(null, "test");
    }

    @Test
    public void writeData_WithNullStatistic() throws Exception {
        statisticWriter.writeData("test", null);
    }

    @Test
    public void writeData_WithNotExistingFolder() throws Exception {
        File folderMock = mock(File.class);
        whenNew(File.class).withArguments("test").thenReturn(folderMock);
        when(folderMock.exists()).thenReturn(false);
        statisticWriter.writeData("test", "test");
    }

    @Test
    public void writeData_WithNotExistingFile() throws Exception {
        File folderMock = mock(File.class);
        whenNew(File.class).withArguments("test").thenReturn(folderMock);
        when(file.exists()).thenReturn(false);
        when(folderMock.exists()).thenReturn(false);
        statisticWriter.writeData("test", "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeData_WithNullDataFolderPath_ThrowsIllegalArgumentException() throws Exception {
        StatisticWriter statisticWriter = new StatisticWriter(null, objectMapper);
        statisticWriter.writeData("test", "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeData_WithEmptyDataFolderPath_ThrowsIllegalArgumentException() throws Exception {
        StatisticWriter statisticWriter = new StatisticWriter("", objectMapper);
        statisticWriter.writeData("test", "test");
    }

}