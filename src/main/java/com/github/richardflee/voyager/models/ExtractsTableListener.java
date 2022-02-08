package com.github.richardflee.voyager.models;

import java.util.List;

import com.github.richardflee.voyager.log_objects.LogExtract;

@FunctionalInterface
public interface ExtractsTableListener {
	public void updateTable(List<LogExtract> extracts);
}
