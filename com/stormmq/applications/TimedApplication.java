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

import com.stormmq.applications.timedApplicationResultsUsers.LogTimedApplicationResultsUser;
import com.stormmq.applications.timedApplicationResultsUsers.TimedApplicationResultsUser;
import com.stormmq.logs.Log;
import org.jetbrains.annotations.*;

import static java.lang.System.currentTimeMillis;

public final class TimedApplication implements Application
{
	@NotNull
	public static Application loggingTimedApplication(@NotNull final Application application, @NotNull final Log log)
	{
		return new TimedApplication(application, new LogTimedApplicationResultsUser(log));
	}

	@NotNull private final Application application;
	@NotNull private final TimedApplicationResultsUser timedApplicationResultsUser;

	private TimedApplication(@NotNull final Application application, @NotNull final TimedApplicationResultsUser timedApplicationResultsUser)
	{
		this.application = application;
		this.timedApplicationResultsUser = timedApplicationResultsUser;
	}

	@NotNull
	@Override
	public ExitCode execute()
	{
		final long start = currentTimeMillis();
		final ExitCode exitCode = application.execute();
		final long end = currentTimeMillis();
		final long duration = end - start;
		timedApplicationResultsUser.use(duration);
		return exitCode;
	}

	@Override
	public void run(@NotNull final AutoCloseable... toBeClosedBeforeExit)
	{
		application.run(toBeClosedBeforeExit);
	}
}
