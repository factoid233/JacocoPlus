/*******************************************************************************
 * Copyright (c) 2009, 2022 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Evgeny Mandrikov - initial API and implementation
 *
 *******************************************************************************/
package org.jacoco.core.test.validation.java5;

import org.jacoco.core.test.validation.JavaVersion;
import org.jacoco.core.test.validation.Source.Line;
import org.jacoco.core.test.validation.ValidationTestBase;
import org.jacoco.core.test.validation.java5.targets.EnumSwitchTarget;

/**
 * Test of filtering of a synthetic class that is generated by javac for a enum
 * in switch statement.
 */
public class EnumSwitchTest extends ValidationTestBase {

	public EnumSwitchTest() {
		super(EnumSwitchTarget.class);
	}

	public void assertSwitch(final Line line) {
		if (isJDKCompiler && JavaVersion.current().isBefore("1.6")) {
			// class that holds "switch map" is not marked as synthetic when
			// compiling with javac 1.5
			assertPartlyCovered(line, 0, 2);
		} else {
			assertFullyCovered(line, 0, 2);
		}
	}

}