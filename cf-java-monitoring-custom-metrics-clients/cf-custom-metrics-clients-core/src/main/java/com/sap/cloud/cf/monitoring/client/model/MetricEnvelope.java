package com.sap.cloud.cf.monitoring.client.model;

import java.util.List;

public class MetricEnvelope {

	private String type = "metrics";
	private List<Metric> metrics;

	public MetricEnvelope(List<Metric> metrics) {
		this.metrics = metrics;
	}

	public List<Metric> getMetrics() {
		return metrics;
	}

	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((metrics == null) ? 0 : metrics.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetricEnvelope other = (MetricEnvelope) obj;
		if (metrics == null) {
			if (other.metrics != null)
				return false;
		} else if (!metrics.equals(other.metrics))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
