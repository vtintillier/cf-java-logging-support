package com.sap.hcp.cf.log4j2.layout;

import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(name = "customField", category = Node.CATEGORY, printObject = true)
public class CustomField {

	private final String key;
	private final boolean retainOriginal;

	private CustomField(Builder builder) {
		this.key = builder.key;
		this.retainOriginal = builder.retainOriginal;
	}

	public String getKey() {
		return key;
	}

	public boolean isRetainOriginal() {
		return retainOriginal;
	}

	@Override
	public String toString() {
		return key + (retainOriginal ? " (retained)" : "");
	}
	
	@PluginBuilderFactory
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder implements org.apache.logging.log4j.core.util.Builder<CustomField> {

		@PluginBuilderAttribute("mdcKeyName")
		private String key;

		@PluginBuilderAttribute("retainOriginal")
		private boolean retainOriginal;

		public Builder setKey(String key) {
			this.key = key;
			return this;
		}

		public Builder setRetainOriginal(boolean retainOriginal) {
			this.retainOriginal = retainOriginal;
			return this;
		}

		@Override
		public CustomField build() {
			return new CustomField(this);
		}
		
	}
}
