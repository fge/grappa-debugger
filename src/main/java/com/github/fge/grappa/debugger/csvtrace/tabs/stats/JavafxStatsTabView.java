package com.github.fge.grappa.debugger.csvtrace.tabs.stats;

import com.github.fge.grappa.debugger.common.JavafxView;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseInfo;
import com.github.fge.grappa.debugger.javafx.JavafxUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class JavafxStatsTabView
    extends JavafxView<StatsTabPresenter, StatsTabDisplay>
    implements StatsTabView
{
    public JavafxStatsTabView()
        throws IOException
    {
        super("/tabs/statsTab.fxml");
    }

    @Override
    public void showParseInfo(final ParseInfo info)
    {
        Objects.requireNonNull(info);

        display.parseDate.setText(info.getTime().toString());

        double ratio;

        final int nrInvocations = info.getNrInvocations();
        display.nrRules.setText(String.valueOf(nrInvocations));

        ratio = (double) nrInvocations / info.getNrLines();
        display.invPerLine.setText(String.format("%.2f", ratio));

        ratio = (double) nrInvocations / info.getNrChars();
        display.invPerChar.setText(String.format("%.2f", ratio));


        display.treeDepth.setText(String.valueOf(info.getTreeDepth()));
    }

    @Override
    public void displayTotalParseTime(final long totalParseTime)
    {
        display.totalParseTime.setText(
            JavafxUtils.nanosToString(totalParseTime));
    }
}
