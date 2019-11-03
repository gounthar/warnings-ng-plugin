package io.jenkins.plugins.analysis.core.model;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.Report;

import io.jenkins.plugins.analysis.core.model.BlamesModel.BlamesRow;
import io.jenkins.plugins.forensics.blame.Blames;
import io.jenkins.plugins.forensics.blame.FileBlame;

import static io.jenkins.plugins.analysis.core.assertions.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link BlamesModel}.
 *
 * @author Colin Kaschel
 */
class BlamesModelTest extends AbstractDetailsModelTest {
    private static final String COMMIT = "commit";
    private static final String NAME = "name";
    private static final String EMAIL = "email";

    private static final int EXPECTED_COLUMNS_SIZE = 6;

    @Test
    void shouldConvertIssueToArrayWithAllColumnsAndRows() {
        Report report = new Report();
        report.add(createIssue(1));
        report.add(createIssue(2));
        Blames blames = mock(Blames.class);

        BlamesModel model = createModel(report, blames);

        assertThat(model.getHeaders()).hasSize(EXPECTED_COLUMNS_SIZE);
        assertThat(model.getWidths()).hasSize(EXPECTED_COLUMNS_SIZE);
        assertThat(model.getColumnsDefinition()).isEqualTo("["
                + "{\"data\": \"description\"},"
                + "{"
                + "  \"type\": \"string\","
                + "  \"data\": \"fileName\","
                + "  \"render\": {"
                + "     \"_\": \"display\","
                + "     \"sort\": \"sort\""
                + "  }"
                + "},"
                + "{\"data\": \"age\"},"
                + "{\"data\": \"author\"},"
                + "{\"data\": \"email\"},"
                + "{\"data\": \"commit\"}"
                + "]");
        assertThat(model.getRows()).hasSize(2);
    }

    @Test
    void shouldShowIssueWithBlames() {
        Report report = new Report();
        Issue issue = createIssue(1);
        report.add(issue);

        FileBlame blameRequest = mock(FileBlame.class);
        when(blameRequest.getCommit(issue.getLineStart())).thenReturn(COMMIT);
        when(blameRequest.getEmail(issue.getLineStart())).thenReturn(EMAIL);
        when(blameRequest.getName(issue.getLineStart())).thenReturn(NAME);

        Blames blames = mock(Blames.class);
        when(blames.contains(issue.getFileName())).thenReturn(true);
        when(blames.getBlame(issue.getFileName())).thenReturn(blameRequest);

        BlamesModel model = createModel(report, blames);

        BlamesRow actualRow = model.getRow(issue);
        assertThat(actualRow).hasDescription(EXPECTED_DESCRIPTION)
                .hasAge("1")
                .hasCommit(COMMIT)
                .hasAuthor(NAME)
                .hasEmail(EMAIL);
        assertThatDetailedColumnContains(actualRow.getFileName(),
                createExpectedFileName(issue), "/path/to/file-1:0000015");
    }

    @Test
    void shouldShowIssueWithoutBlames() {
        Report report = new Report();
        Issue issue = createIssue(1);
        report.add(issue);

        Blames blames = mock(Blames.class);

        BlamesModel model = createModel(report, blames);

        BlamesRow actualRow = model.getRow(issue);
        assertThat(actualRow).hasDescription(EXPECTED_DESCRIPTION)
                .hasAge("1")
                .hasCommit(BlamesModel.UNDEFINED)
                .hasAuthor(BlamesModel.UNDEFINED)
                .hasEmail(BlamesModel.UNDEFINED);

        assertThatDetailedColumnContains(actualRow.getFileName(),
                createExpectedFileName(issue), "/path/to/file-1:0000015");
    }

    private BlamesModel createModel(final Report report, final Blames blames) {
        return new BlamesModel(report, blames, createFileNameRenderer(), createAgeBuilder(), issue -> DESCRIPTION);
    }
}
