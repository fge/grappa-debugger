package com.github.fge.grappa.debugger.model;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.internal.NonFinalForTesting;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@NonFinalForTesting
@ParametersAreNonnullByDefault
public class InputText
{
    private final int nrLines;
    private final int nrChars;
    private final int nrCodePoints;
    private final InputBuffer inputBuffer;

    public InputText(final int nrLines, final int nrChars,
        final int nrCodePoints, final InputBuffer inputBuffer)
    {
        this.nrLines = nrLines;
        this.nrChars = nrChars;
        this.nrCodePoints = nrCodePoints;
        this.inputBuffer = Objects.requireNonNull(inputBuffer);
    }

    public int getNrLines()
    {
        return nrLines;
    }

    public int getNrChars()
    {
        return nrChars;
    }

    public int getNrCodePoints()
    {
        return nrCodePoints;
    }

    @Nonnull
    public InputBuffer getInputBuffer()
    {
        return inputBuffer;
    }
}
