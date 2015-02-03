package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.MainWindowFactory;
import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.common.db.DbLoadStatus;
import com.github.fge.grappa.debugger.common.db.DbLoader;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.csvtrace.CsvTracePresenter;
import com.github.fge.grappa.debugger.csvtrace.dbmodel.DbCsvTraceModel;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseInfo;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.jooq.DSLContext;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class MainWindowPresenter
    extends BasePresenter<MainWindowView>
{
    private static final Map<String, ?> ZIPFS_ENV
        = Collections.singletonMap("readonly", "true");

    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private final Pattern SEMICOLON = Pattern.compile(";");

    private static final String INFO_PATH = "/info.csv";

    private static final ThreadFactory THREAD_FACTORY
        = new ThreadFactoryBuilder().setDaemon(true)
        .setNameFormat("db-loader-%d").build();

    private final ExecutorService executor
        = Executors.newSingleThreadExecutor(THREAD_FACTORY);

    private final BackgroundTaskRunner taskRunner;

    private final MainWindowFactory windowFactory;

    @VisibleForTesting
    CsvTracePresenter tracePresenter;

    public MainWindowPresenter(final MainWindowFactory windowFactory,
        final BackgroundTaskRunner taskRunner)
    {
        this.windowFactory = Objects.requireNonNull(windowFactory);
        this.taskRunner = Objects.requireNonNull(taskRunner);
    }

    public void handleCloseWindow()
    {
        if (tracePresenter != null)
            tracePresenter.dispose();
        windowFactory.close(this);
    }

    public void handleNewWindow()
    {
        windowFactory.createWindow();
    }

    public void handleLoadFile()
    {
        final Path path = view.chooseFile();

        if (path == null)
            return;

        MainWindowPresenter window = this;

        if (tracePresenter != null) {
            window = windowFactory.createWindow();
            if (window == null)
                return;
        }

        window.loadTab(path);
    }

    void loadTab(final Path path)
    {
        try {
            final CsvTraceModel model = getModel(path);
            tracePresenter = new CsvTracePresenter(view, taskRunner, model);
        } catch (IOException | SQLException e) {
            handleLoadFileError(e);
            return;
        }

        view.attachTrace(tracePresenter);
        tracePresenter.loadTrace();
    }

    CsvTraceModel getModel(final Path path)
        throws IOException, SQLException
    {
        final URI uri = URI.create("jar:" + path.toUri());
        final FileSystem zipfs = FileSystems.newFileSystem(uri, ZIPFS_ENV);
        final ParseInfo info = readInfo(zipfs);
        final DbLoadStatus status = new DbLoadStatus(info.getNrMatchers(),
            info.getNrInvocations());
        final DbLoader loader = new DbLoader(zipfs, status);
        final DSLContext jooq = loader.getJooq();
        final Callable<DSLContext> callable = loader::loadAll;
        final Future<DSLContext> future = executor.submit(callable);
        return new DbCsvTraceModel(zipfs, jooq, info, future);
    }

    private void handleLoadFileError(final Throwable throwable)
    {
        view.showError("Trace file error", "Unable to load trace file",
            throwable);
        view.setLabelText("Please load a trace file (File -> Load file)");
    }

    private ParseInfo readInfo(final FileSystem zipfs)
        throws IOException
    {
        final Path path = zipfs.getPath(INFO_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            final String[] elements = SEMICOLON.split(reader.readLine());

            final long epoch = Long.parseLong(elements[0]);
            final Instant instant = Instant.ofEpochMilli(epoch);
            final ZoneId zone = ZoneId.systemDefault();
            final LocalDateTime time = LocalDateTime.ofInstant(instant, zone);

            final int treeDepth = Integer.parseInt(elements[1]);
            final int nrMatchers = Integer.parseInt(elements[2]);
            final int nrLines = Integer.parseInt(elements[3]);
            final int nrChars = Integer.parseInt(elements[4]);
            final int nrCodePoints = Integer.parseInt(elements[5]);
            final int nrInvocations = Integer.parseInt(elements[6]);

            return new ParseInfo(time, treeDepth, nrMatchers, nrLines, nrChars,
                nrCodePoints, nrInvocations);
        }
    }

}
