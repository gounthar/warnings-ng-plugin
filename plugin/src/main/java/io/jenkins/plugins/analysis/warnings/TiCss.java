package io.jenkins.plugins.analysis.warnings;

import java.io.Serial;

import org.kohsuke.stapler.DataBoundConstructor;
import org.jenkinsci.Symbol;
import hudson.Extension;

import io.jenkins.plugins.analysis.core.model.AnalysisModelParser;

/**
 * Provides a parser and customized messages for the Texas Instruments Code Composer Studio compiler.
 *
 * @author Ullrich Hafner
 */
public class TiCss extends AnalysisModelParser {
    @Serial
    private static final long serialVersionUID = -7982604310730169143L;
    private static final String ID = "code-composer";

    /** Creates a new instance of {@link TiCss}. */
    @DataBoundConstructor
    public TiCss() {
        super();
        // empty constructor required for stapler
    }

    /** Descriptor for this static analysis tool. */
    @Symbol("tiCss")
    @Extension
    public static class Descriptor extends AnalysisModelParserDescriptor {
        /** Creates the descriptor instance. */
        public Descriptor() {
            super(ID);
        }
    }
}
