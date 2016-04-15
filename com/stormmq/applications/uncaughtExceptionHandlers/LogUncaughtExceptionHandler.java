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

package com.stormmq.applications.uncaughtExceptionHandlers;

import com.stormmq.logs.Log;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.stormmq.logs.LogLevel.Alert;
import static com.stormmq.string.Formatting.format;
import static java.lang.System.err;
import static java.util.regex.Pattern.LITERAL;
import static java.util.regex.Pattern.compile;

public final class LogUncaughtExceptionHandler implements UncaughtExceptionHandler
{
	@SuppressWarnings("HardcodedLineSeparator") private static final Pattern RegEx = compile("\n", LITERAL);

	@NotNull private final Log log;

	public LogUncaughtExceptionHandler(@NotNull final Log log)
	{
		this.log = log;
	}

	@Override
	public void uncaughtException(@NotNull final Thread thread, @NotNull final Throwable uncaughtThrowable)
	{
		if (uncaughtThrowable instanceof MustExitBecauseOfFailureException)
		{
			log.log(Alert, uncaughtThrowable.getMessage());
		}
		else
		{
			final StringWriter stringWriter = new StringWriter(4096);
			try(final PrintWriter printWriter = new PrintWriter(stringWriter))
			{
				uncaughtThrowable.printStackTrace(printWriter);
			}
			final String stackTrace = RegEx.matcher(stringWriter.toString()).replaceAll(Matcher.quoteReplacement(", "));
			log.log(Alert, format("Exception '%1$s' on thread '%2$s' is '%3$s'", uncaughtThrowable.getMessage(), thread.getName(), stackTrace));
		}
	}
}
