package io.jenkins.plugins.analysis.warnings;

import java.io.Serial;

import org.kohsuke.stapler.DataBoundConstructor;
import org.jenkinsci.Symbol;
import hudson.Extension;

import io.jenkins.plugins.analysis.core.model.AnalysisModelParser;

/**
 * Provides a parser and customized messages for the PRQA QA-C Sourcecode Analyser.
 *
 * @author Ullrich Hafner
 */
public class QacSourceCodeAnalyser extends AnalysisModelParser {
    @Serial
    private static final long serialVersionUID = 3092674431567484628L;
    private static final String ID = "qac";

    /** Creates a new instance of {@link QacSourceCodeAnalyser}. */
    @DataBoundConstructor
    public QacSourceCodeAnalyser() {
        super();
        // empty constructor required for stapler
    }

    /** Descriptor for this static analysis tool. */
    @Symbol("qacSourceCodeAnalyser")
    @Extension
    public static class Descriptor extends AnalysisModelParserDescriptor {
        /** Creates the descriptor instance. */
        public Descriptor() {
            super(ID);
        }
    }
}
