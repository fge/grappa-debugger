package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import com.github.fge.grappa.debugger.stats.classdetails.MatcherClassDetails;
import javafx.fxml.FXML;
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

    public void setPresenter(final ClassDetailsStatsPresenter presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
        init();
    }

    private void init()
    {
        classNames.setCellFactory(
            param -> new ListCell<MatcherClassDetails>()
            {
                @Override
                protected void updateItem(final MatcherClassDetails item,
                    final boolean empty)
                {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.getClassName());
                }
            }
        );
    }
}
