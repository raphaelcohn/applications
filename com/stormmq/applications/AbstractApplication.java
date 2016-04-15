// The MIT License (MIT)
//
// Copyright © 2016, Raphael Cohn <raphael.cohn@stormmq.com>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.stormmq.applications;

import com.stormmq.applications.uncaughtExceptionHandlers.*;
import com.stormmq.logs.Log;
import org.jetbrains.annotations.*;

import java.io.IOError;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.atomic.AtomicReference;

import static com.stormmq.applications.ExitCode.Software;
import static com.stormmq.applications.ExitCode.Success;
import static java.lang.Thread.currentThread;

public abstract class AbstractApplication implements Application
{
	@SuppressWarnings("WeakerAccess") @NotNull protected final UncaughtExceptionHandler uncaughtExceptionHandler;
	@NotNull protected final AtomicReference<ExitCode> exitCode;
	@NotNull protected final ExitCodeSettingUncaughtExceptionHandler exitCodeSettingUncaughtExceptionHandler;

	protected AbstractApplication(@NotNull final Log log)
	{
		uncaughtExceptionHandler = new LogUncaughtExceptionHandler(log);
		exitCode = new AtomicReference<>(Success);
		exitCodeSettingUncaughtExceptionHandler = new ExitCodeSettingUncaughtExceptionHandler(uncaughtExceptionHandler, exitCode);
	}

	@Override
	@NotNull
	public final ExitCode execute()
	{
		try
		{
			executeInternally();
		}
		catch (final MustExitBecauseOfFailureException e)
		{
			uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
		}
		return exitCode.get();
	}

	@Override
	public void run(@NotNull final AutoCloseable... toBeClosedBeforeExit)
	{
		ExitCode exitCode;
		try
		{
			exitCode = execute();
		}
		catch (@SuppressWarnings("ErrorNotRethrown") final IOError e)
		{
			exitCode = handle(e, ExitCode.IOError);
		}
		catch (final Throwable e)
		{
			exitCode = handle(e, Software);
		}

		for (final AutoCloseable autoCloseable : toBeClosedBeforeExit)
		{
			try
			{
				autoCloseable.close();
			}
			catch (final Throwable ignored)
			{
			}
		}

		if (exitCode != Success)
		{
			exitCode.exit();
		}
	}

	@NotNull
	private ExitCode handle(@NotNull final Throwable throwable, @NotNull final ExitCode exitCode)
	{
		uncaughtExceptionHandler.uncaughtException(currentThread(), throwable);
		return exitCode;
	}

	protected final boolean mustExitBecauseOfFailure()
	{
		return exitCode.get() != Success;
	}

	protected abstract void executeInternally() throws MustExitBecauseOfFailureException;
}
