package io.jenkins.plugins.analysis.core.model;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import hudson.model.Action;
import hudson.model.Api;
import hudson.model.Run;
import jenkins.model.RunAction2;
import jenkins.tasks.SimpleBuildStep.LastBuildAction;

import io.jenkins.plugins.analysis.core.restapi.AggregationApi;
import io.jenkins.plugins.analysis.core.restapi.ToolApi;

/**
 * Aggregates the results of all analysis results. Provides an entry point for the remote API. Currently, the aggregated
 * results are only visualized in the associated trend chart but not in a detail view.
 *
 * @author Ullrich Hafner
 * @see AggregatedTrendAction
 */
@SuppressFBWarnings(value = "UWF", justification = "transient field owner ist restored using a Jenkins callback")
public class AggregationAction implements RunAction2, LastBuildAction {
    private transient Run<?, ?> owner;

    @CheckForNull
    @Override
    public String getIconFileName() {
        return null; // No UI representation up to now
    }

    @CheckForNull
    @Override
    public String getDisplayName() {
        return Messages.Aggregation_Name();
    }

    @CheckForNull
    @Override
    public String getUrlName() {
        return "warnings-ng";
    }

    /**
     * Gets the remote API for this action. Depending on the path, a different result is selected.
     *
     * @return the remote API
     */
    public Api getApi() {
        return new Api(new AggregationApi(findActions()));
    }

    public List<ToolApi> getTools() {
        return findActions();
    }

    private List<ToolApi> findActions() {
        return owner.getActions(ResultAction.class).stream().map(this::createToolApi).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends Action> getProjectActions() {
        return Set.of(new AggregatedTrendAction(owner.getParent()));
    }

    private ToolApi createToolApi(final ResultAction result) {
        return new ToolApi(result.getId(), result.getDisplayName(),
                result.getAbsoluteUrl(), result.getResult().getTotalSize(), result.getResult().getSizePerSeverity());
    }

    @Override
    public void onAttached(final Run<?, ?> r) {
        owner = r;
    }

    @Override
    public void onLoad(final Run<?, ?> r) {
        owner = r;
    }
}
