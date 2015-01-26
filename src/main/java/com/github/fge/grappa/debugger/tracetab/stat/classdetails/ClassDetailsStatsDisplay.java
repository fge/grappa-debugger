package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import com.github.fge.grappa.debugger.stats.classdetails.MatcherClassDetails;
import com.google.common.annotations.VisibleForTesting;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class ClassDetailsStatsDisplay
{
    private ClassDetailsStatsPresenter presenter;

    @FXML
    ListView<MatcherClassDetails> classNames;

    @FXML
    Label matcherType;

    public void setPresenter(final ClassDetailsStatsPresenter presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
        init();
    }

    @VisibleForTesting
    void init()
    {
        classNames.setCellFactory(param -> new MatcherClassCell(this));
    }

    @VisibleForTesting
    void showClassDetailsEvent(final MatcherClassDetails details)
    {
        Objects.requireNonNull(details);
        presenter.handleShowClassDetails(details);
    }

    private static final class MatcherClassCell
        extends ListCell<MatcherClassDetails>
    {
        private MatcherClassCell(final ClassDetailsStatsDisplay display)
        {
            setEditable(false);
            selectedProperty().addListener(new ChangeListener<Boolean>()
            {
                @SuppressWarnings("AutoUnboxing")
                @Override
                public void changed(
                    final ObservableValue<? extends Boolean> observable,
                    final Boolean oldValue, final Boolean newValue)
                {
                    if (oldValue == newValue)
                        return;
                    if (!newValue)
                        return;
                    final MatcherClassDetails details = getItem();
                    display.showClassDetailsEvent(details);
                }
            });
        }

        @Override
        protected void updateItem(final MatcherClassDetails item,
            final boolean empty)
        {
            super.updateItem(item, empty);
            if (empty)
                return;
            setText(item == null ? null : item.getClassName());
        }
    }
}
