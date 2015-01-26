package com.github.fge.grappa.debugger.stats.classdetails;

import com.github.fge.grappa.debugger.stats.TraceEventProcessor;
import com.github.fge.grappa.matchers.MatcherType;
import com.github.fge.grappa.trace.TraceEvent;
import com.github.fge.grappa.trace.TraceEventType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MatcherClassDetailsProcessor
    implements TraceEventProcessor
{
    private final Map<String, MatcherClassDetails> classDetails
        = new HashMap<>();
    private final Map<String, RuleInvocationDetails> ruleDetails
        = new HashMap<>();
    private final Map<String, Integer> ruleStartIndices
        = new HashMap<>();


    @Override
    public void process(final TraceEvent event)
    {
        final String ruleName = event.getMatcher();
        final String matcherClass = event.getMatcherClass();
        final MatcherType matcherType = event.getMatcherType();
        final TraceEventType eventType = event.getType();

        final RuleInvocationDetails ruleDetail;

        if (eventType == TraceEventType.BEFORE_MATCH) {
            final MatcherClassDetails classDetail
                = classDetails.computeIfAbsent(matcherClass,
                    key -> new MatcherClassDetails(key, matcherType));
            ruleDetail = ruleDetails.computeIfAbsent(ruleName,
                RuleInvocationDetails::new);
            classDetail.addRuleInvocationDetails(ruleDetail);
            ruleStartIndices.put(ruleName, event.getIndex());
            return;
        }

        ruleDetail = ruleDetails.get(ruleName);

        if (eventType == TraceEventType.MATCH_FAILURE) {
            ruleDetail.failedMatches++;
            return;
        }

        @SuppressWarnings("AutoUnboxing")
        final int start = ruleStartIndices.get(ruleName);
        final int end = event.getIndex();

        if (start == end)
            ruleDetail.emptyMatches++;
        else
            ruleDetail.nonEmptyMatches++;
    }

    public Map<String, MatcherClassDetails> getClassDetails()
    {
        System.out.println(classDetails.size());
        return Collections.unmodifiableMap(classDetails);
    }
}
