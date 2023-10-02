/*******************************************************************************
 * Copyright (c) 2009, 2022 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 *******************************************************************************/
package org.jacoco.report.internal.html.table;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.jacoco.core.analysis.CounterComparator;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.resources.Resources;

/**
 * Column with a graphical bar that represents the total amount of items in with
 * length, and the coverage ratio with a red/green sections. The implementation
 * is stateful, instances must not be used in parallel.
 */
public class BarColumn implements IColumnRenderer {

	private static final int WIDTH = 120;

	private final CounterEntity entity;

	private final NumberFormat integerFormat;

	private int max;

	private final Comparator<ITableItem> comparator;

	/**
	 * Creates a new column that is based on the {@link ICounter} for the given
	 * entity.
	 *
	 * @param entity
	 *            counter entity for visualization
	 * @param locale
	 *            locale for rendering numbers
	 */
	public BarColumn(final CounterEntity entity, final Locale locale) {
		this.entity = entity;
		this.integerFormat = NumberFormat.getIntegerInstance(locale);
		this.comparator = new TableItemComparator(
				CounterComparator.MISSEDITEMS.reverse().on(entity).second(
						CounterComparator.TOTALITEMS.reverse().on(entity)));
	}

	public boolean init(final List<? extends ITableItem> items,
			final ICoverageNode total) {
		this.max = 0;
		for (final ITableItem item : items) {
			final int count = item.getNode().getCounter(entity).getTotalCount();
			if (count > this.max) {
				this.max = count;
			}
		}
		return true;
	}

	public void footer(final HTMLElement td, final ICoverageNode total,
			final Resources resources, final ReportOutputFolder base)
			throws IOException {
		final ICounter counter = total.getCounter(entity);
		td.text(integerFormat.format(counter.getMissedCount()));
		td.text(" of ");
		td.text(integerFormat.format(counter.getTotalCount()));
	}

	public void item(final HTMLElement td, final ITableItem item,
			final Resources resources, final ReportOutputFolder base)
			throws IOException {
		if (max > 0) {
			final ICounter counter = item.getNode().getCounter(entity);
			//增加total数值
			final int total = counter.getTotalCount();
			final int covered = counter.getCoveredCount();
			bar(td, covered, Resources.GREENBAR, resources, base,total);
			final int missed = counter.getMissedCount();
			bar(td, missed, Resources.REDBAR, resources, base,total);

		}
	}

	private void bar(final HTMLElement td, final int count, final String image,
			final Resources resources, final ReportOutputFolder base,final int total)
			throws IOException {
		// 确定柱子最大宽度为固定值，使用百分比填充bar最大宽度
		int width = 0;
		if (total != 0){
			width = count * WIDTH / total;
		}

		if (width > 0) {
			td.img(resources.getLink(base, image), width, 10,
					integerFormat.format(count));
		}
	}

	public Comparator<ITableItem> getComparator() {
		return comparator;
	}

}
