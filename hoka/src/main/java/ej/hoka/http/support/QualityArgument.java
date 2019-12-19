/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.support;

/**
 * Class for handling HTTP "Accept" header quality argument.
 * <p>
 * The class parses the quality argument in the following form:
 * <p>
 * <code>Accept: text/html; q=1.0, text/*; q=0.8, image/gif; q=0.6, image/jpeg; q=0.6, image/*; q=0.5</code>
 * <p>
 * The value of the <code>q</code> is a short floating point number (range: 0.0-1.0) denoting the relative "acceptance"
 * value of a content-type. If omitted the default value is 1.0.
 */
public class QualityArgument extends ParameterizedArgument {

	/**
	 * The parsed quality argument in the "Accept" header.
	 */
	protected float quality;

	/**
	 * Returns the quality value of this argument.
	 *
	 * @return the quality
	 */
	public float getQuality() {
		return this.quality;
	}

	/**
	 * Sets the quality value to <code>1</code> if <code>nbTokens</code> equals to <code>0</code>.
	 *
	 * @param nbTokens
	 *            Sets the quality value to <code>1</code> if <code>nbTokens</code> equals to <code>0</code>
	 */
	@Override
	public void initializeNbParameters(int nbTokens) {
		if (nbTokens == 0) {
			// no specified quality: consider quality = 1
			this.quality = 1;
		}
	}

	/**
	 * Sets the quality value. The value is in the substring within begin index <code>startValue</code> and end index
	 * <code>stopValue</code>. When the quality value {@link String} cannot be parsed as a floating point number, the
	 * value is considered to be 0.
	 *
	 * @param index
	 *            not used
	 * @param startKey
	 *            not used
	 * @param stopKey
	 *            not used
	 * @param startValue
	 *            start index of quality value
	 * @param stopValue
	 *            end index of quality value
	 */
	@Override
	public void newParameter(int index, int startKey, int stopKey, int startValue, int stopValue) {
		try {
			this.quality = Float.parseFloat(this.currentString.substring(startValue, stopValue).trim());
		} catch (NumberFormatException e) {
			// parse error. consider it is not acceptable
			this.quality = 0;
		}
	}

}
