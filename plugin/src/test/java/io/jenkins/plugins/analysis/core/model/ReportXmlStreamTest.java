package io.jenkins.plugins.analysis.core.model;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.Issue;

import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;
import edu.hm.hafner.util.ResourceTest;

import static edu.hm.hafner.analysis.assertions.Assertions.*;

/**
 * Tests the class {@link ReportXmlStream}.
 *
 * @author Ullrich Hafner
 */
class ReportXmlStreamTest extends ResourceTest {
    @Test @Issue("JENKINS-63659")
    void shouldMapOldSerializationFromAnalysisModel900AndWarnings841() {
        ReportXmlStream reportXmlStream = new ReportXmlStream();

        Object restored = reportXmlStream.read(getResourceAsFile("serialization-9.0.0.xml"));
        assertThat(restored).isInstanceOfSatisfying(Report.class,
                report -> {
                    assertThat(report).isEmpty(); // Fallback empty report
                });
    }

    @Test @Issue("JENKINS-61293")
    void shouldMapDescriptionsToCorrectType() {
        ReportXmlStream reportXmlStream = new ReportXmlStream();

        Object restored = reportXmlStream.read(getResourceAsFile("npe.xml"));
        assertThat(restored).isInstanceOfSatisfying(Report.class,
                report -> {
                    assertThat(report).hasSize(2); // Duplicate issues will be skipped
                });
    }

    @Test
    void shouldReadIssues() {
        ReportXmlStream reportXmlStream = new ReportXmlStream();

        Object restored = reportXmlStream.read(getResourceAsFile("java-report.xml"));

        Path saved = createTempFile();
        assertThat(restored).isInstanceOfSatisfying(Report.class,
                report -> {
                    assertThatReportIsCorrect(report);

                    reportXmlStream.write(saved, report);
                });

        Report newFormat = reportXmlStream.read(saved);
        assertThatReportIsCorrect(newFormat);
    }

    private void assertThatReportIsCorrect(final Report report) {
        assertThat(report).hasSize(9);

        assertThat(report.get(0))
                .hasCategory("ConstructorLeaksThis")
                .hasSeverity(Severity.WARNING_NORMAL)
                .hasLineStart(83)
                .hasColumnStart(44)
                .hasOrigin("java")
                .hasModuleName("Static Analysis Model and Parsers")
                .hasPackageName("edu.hm.hafner.analysis")
                .hasFileName("/var/data/workspace/pipeline-analysis-model/src/main/java/edu/hm/hafner/analysis/Report.java");
    }
}
