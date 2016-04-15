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

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public enum Verbosity
{
	None,
	Verbose,
	VeryVerbose,
	Everything,
	;

	public boolean isAtLeastVerbose()
	{
		return compareTo(None) > 0;
	}

	@NotNull
	public <T> T isAtLeastVerbose(@NotNull final Supplier<T> ifTrue, @NotNull final Supplier<T> ifFalse)
	{
		final Supplier<T> supplier = isAtLeastVerbose() ? ifTrue : ifFalse;
		return supplier.get();
	}

	@NotNull
	public <T> T isAtLeastVerbose(@NotNull final T original, @NotNull final Function<T, T> ifTrue, @NotNull final Function<T, T> ifFalse)
	{
		final Function<T, T> function = isAtLeastVerbose() ? ifTrue : ifFalse;
		return function.apply(original);
	}
}
