package de.metas.ui.web.document.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.adempiere.util.Check;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.metas.i18n.ITranslatableString;
import de.metas.i18n.ImmutableTranslatableString;
import de.metas.ui.web.document.filter.DocumentFilterParam.Operator;
import lombok.NonNull;

/*
 * #%L
 * metasfresh-webui-api
 * %%
 * Copyright (C) 2016 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

/**
 * Also see {@link de.metas.ui.web.document.filter.sql.SqlDocumentFilterConverter}.
 *
 * @author metas-dev <dev@metasfresh.com>
 *
 */
@Immutable
public final class DocumentFilter
{
	public static final Builder builder()
	{
		return new Builder();
	}

	public static DocumentFilter singleParameterFilter(final String filterId, final String fieldName, final Operator operator, final Object value)
	{
		return builder()
				.setFilterId(filterId)
				.addParameter(DocumentFilterParam.builder()
						.setFieldName(fieldName)
						.setOperator(operator)
						.setValue(value)
						.build())
				.build();
	}

	public static DocumentFilter inArrayFilter(final String filterId, final String fieldName, final Collection<Integer> values)
	{
		return builder()
				.setFilterId(filterId)
				.addParameter(DocumentFilterParam.builder()
						.setFieldName(fieldName)
						.setOperator(Operator.IN_ARRAY)
						.setValue(ImmutableList.copyOf(values))
						.build())
				.build();
	}

	private final String filterId;
	private final ITranslatableString caption;
	private final ImmutableList<DocumentFilterParam> parameters;
	private final ImmutableSet<String> internalParameterNames;

	private DocumentFilter(final Builder builder)
	{
		filterId = builder.filterId;
		Check.assumeNotEmpty(filterId, "filterId is not empty");

		caption = builder.caption;

		parameters = builder.parameters != null ? ImmutableList.copyOf(builder.parameters) : ImmutableList.of();
		internalParameterNames = builder.internalParameterNames != null ? ImmutableSet.copyOf(builder.internalParameterNames) : ImmutableSet.of();
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.omitNullValues()
				.add("filterId", filterId)
				.add("caption", caption)
				.add("parameters", parameters.isEmpty() ? null : parameters)
				.add("internalParameterNames", internalParameterNames.isEmpty() ? null : internalParameterNames)
				.toString();
	}

	public String getFilterId()
	{
		return filterId;
	}

	public String getCaption(@Nullable final String adLanguage)
	{
		return caption != null ? caption.translate(adLanguage) : null;
	}

	/**
	 * @return never returns {@code null}
	 */
	public List<DocumentFilterParam> getParameters()
	{
		return parameters;
	}

	public boolean isInternalParameter(final String parameterName)
	{
		return internalParameterNames.contains(parameterName);
	}

	public DocumentFilterParam getParameter(@NonNull final String parameterName)
	{
		return parameters
				.stream()
				.filter(param -> parameterName.equals(param.getFieldName()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Parameter " + parameterName + " not found in " + this));
	}

	public DocumentFilterParam getParameterOrNull(@NonNull final String parameterName)
	{
		return parameters
				.stream()
				.filter(param -> parameterName.equals(param.getFieldName()))
				.findFirst()
				.orElse(null);
	}

	public String getParameterValueAsString(@NonNull final String parameterName)
	{
		final DocumentFilterParam param = getParameter(parameterName);
		return param.getValueAsString();
	}

	public String getParameterValueAsString(@NonNull final String parameterName, final String defaultValue)
	{
		final DocumentFilterParam param = getParameterOrNull(parameterName);
		if (param == null)
		{
			return defaultValue;
		}

		return param.getValueAsString();
	}

	public int getParameterValueAsInt(@NonNull final String parameterName, final int defaultValue)
	{
		final DocumentFilterParam param = getParameterOrNull(parameterName);
		if (param == null)
		{
			return defaultValue;
		}

		return param.getValueAsInt(defaultValue);
	}

	public Boolean getParameterValueAsBoolean(@NonNull final String parameterName, final Boolean defaultValue)
	{
		final DocumentFilterParam param = getParameterOrNull(parameterName);
		if (param == null)
		{
			return defaultValue;
		}

		return param.getValueAsBoolean(defaultValue);
	}

	public boolean getParameterValueAsBoolean(@NonNull final String parameterName, final boolean defaultValue)
	{
		final DocumentFilterParam param = getParameterOrNull(parameterName);
		if (param == null)
		{
			return defaultValue;
		}

		return param.getValueAsBoolean(defaultValue);
	}

	public Date getParameterValueAsDate(@NonNull final String parameterName, final Date defaultValue)
	{
		final DocumentFilterParam param = getParameterOrNull(parameterName);
		if (param == null)
		{
			return defaultValue;
		}

		return param.getValueAsDate(defaultValue);
	}

	public <T> T getParameterValueAs(@NonNull final String parameterName)
	{
		final DocumentFilterParam param = getParameterOrNull(parameterName);
		if (param == null)
		{
			return null;
		}

		@SuppressWarnings("unchecked")
		final T value = (T)param.getValue();
		return value;
	}

	//
	//
	//
	//
	//

	public static final class Builder
	{
		private String filterId;
		private ITranslatableString caption = ImmutableTranslatableString.empty();
		private List<DocumentFilterParam> parameters;
		private Set<String> internalParameterNames;

		private Builder()
		{
			super();
		}

		public DocumentFilter build()
		{
			return new DocumentFilter(this);
		}

		public Builder setFilterId(final String filterId)
		{
			this.filterId = filterId;
			return this;
		}

		public Builder setCaption(@NonNull final ITranslatableString caption)
		{
			this.caption = caption;
			return this;
		}

		public Builder setCaption(@NonNull final String caption)
		{
			return setCaption(ImmutableTranslatableString.constant(caption));
		}

		public Builder setParameters(final List<DocumentFilterParam> parameters)
		{
			this.parameters = parameters;
			return this;
		}

		public Builder addParameter(@NonNull final DocumentFilterParam parameter)
		{
			if (parameters == null)
			{
				parameters = new ArrayList<>();
			}
			parameters.add(parameter);
			return this;
		}

		public Builder addInternalParameter(@NonNull final DocumentFilterParam parameter)
		{
			addParameter(parameter);
			addInternalParameterName(parameter.getFieldName());
			return this;
		}

		private void addInternalParameterName(final String parameterName)
		{
			if (internalParameterNames == null)
			{
				internalParameterNames = new HashSet<>();
			}
			internalParameterNames.add(parameterName);
		}

	}
}
