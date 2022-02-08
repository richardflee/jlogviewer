package com.github.richardflee.voyager.models;

import java.util.List;

import com.github.richardflee.voyager.log_objects.LogMetric;

public interface MetricsTableListener {
	public void updateTable(List<LogMetric> metrics);
}
